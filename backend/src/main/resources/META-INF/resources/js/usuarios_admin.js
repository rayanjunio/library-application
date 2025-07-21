document.addEventListener('DOMContentLoaded', () => {
    const alertArea = document.getElementById('alert-area');
    const tabelaUsuarios = document.getElementById('tabelaUsuarios');
    const formUsuario = document.getElementById('formUsuario');
    const modalUsuario = new bootstrap.Modal(document.getElementById('modalUsuario'));
    let modoEdicao = { ativo: false, id: null };

    async function carregarUsuarios() {
        try {
            const res = await fetch('/user/get-all', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao carregar');
            const data = await res.json();
            const usuarios = data.content || [];
            atualizarTabela(usuarios);
        } catch (e) {
            showError('Erro: ' + e.message);
        }
    }

    function atualizarTabela(usuarios) {
        tabelaUsuarios.innerHTML = usuarios.length === 0
            ? `<tr><td colspan="7" class="text-center">Nenhum usuário</td></tr>`
            : usuarios.map(u => `
        <tr>
          <td>${u.id}</td>
          <td>${u.name}</td>
          <td>${u.email}</td>
          <td>${u.cpf}</td>
          <td><span class="badge bg-${u.status === 'ACTIVE' ? 'success' : 'warning'}">${u.status}</span>
            ${u.status === 'FINED' ? `<button class='btn btn-sm btn-outline-danger ms-2' data-remove-fine='${u.email}'>Remover Multa</button>` : ''}
          </td>
          <td>${u.profile}</td>
        </tr>
      `).join('');
        // Adicionar evento aos botões de remover multa
        document.querySelectorAll('[data-remove-fine]').forEach(btn => {
            btn.addEventListener('click', async function() {
                const email = btn.getAttribute('data-remove-fine');
                btn.disabled = true;
                btn.textContent = 'Removendo...';
                try {
                    const res = await fetch(`/loan/remove-fine/${email}`, {
                        method: 'PATCH',
                        credentials: 'include'
                    });
                    if (!res.ok) {
                        let errorMsg = 'Erro ao remover multa';
                        try {
                            const errorText = await res.text();
                            const errorJson = JSON.parse(errorText);
                            if (errorJson && errorJson.message) {
                                errorMsg = errorJson.message;
                            } else {
                                errorMsg = errorText;
                            }
                        } catch (e) {
                            errorMsg = 'Erro ao remover multa';
                        }
                        showError(errorMsg);
                        btn.disabled = false;
                        btn.textContent = 'Remover Multa';
                        return;
                    }
                    showSuccess('Multa removida com sucesso!');
                    carregarUsuarios();
                } catch (e) {
                    showError('Erro: ' + e.message);
                    btn.disabled = false;
                    btn.textContent = 'Remover Multa';
                }
            });
        });
    }

    function showAlert(msg, type = 'danger') {
        alertArea.innerHTML = `
      <div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${msg}
        <button class="btn-close" data-bs-dismiss="alert"></button>
      </div>`;
    }

    function showSuccess(msg) { showAlert(msg, 'success'); }
    function showError(msg) { showAlert(msg, 'danger'); }

    formUsuario.addEventListener('submit', async e => {
        e.preventDefault();
        const nome = document.getElementById('nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const cpf = document.getElementById('cpf').value.trim();
        const senha = document.getElementById('senha').value;

        if (nome.length < 2) return showError('Nome inválido');
        if (email.length < 5) return showError('Email inválido');
        if (!modoEdicao.ativo && (!senha || senha.length < 8)) return showError('Senha inválida');

        const userData = { name: nome, email, cpf };
        if (!modoEdicao.ativo) userData.password = senha;

        const url = modoEdicao.ativo
            ? `/user/update/${modoEdicao.id}`
            : '/user/add-admin';

        const method = modoEdicao.ativo ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(userData)
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao salvar usuário';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao salvar usuário';
                }
                showError(errorMsg);
                return;
            }
            showSuccess(modoEdicao.ativo ? 'Atualizado!' : 'Criado!');
            modalUsuario.hide();
            formUsuario.reset();
            document.getElementById('senha-container').style.display = 'block';
            modoEdicao = { ativo: false, id: null };
            carregarUsuarios();
        } catch (e) {
            showError('Erro: ' + e.message);
        }
    });

    document.getElementById('btn-logout').addEventListener('click', () => {
        window.location.href = '/';
    });

    carregarUsuarios();

    window.editarUsuario = async (id) => {
        try {
            const res = await fetch(`/user/get`, { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao carregar usuário');
            const u = await res.json();
            document.getElementById('nome').value = u.name;
            document.getElementById('email').value = u.email;
            document.getElementById('cpf').value = u.cpf;
            document.getElementById('senha-container').style.display = 'none'; // esconde campo senha
            document.getElementById('modalUsuarioLabel').textContent = 'Editar Usuário';
            modoEdicao = { ativo: true, id };
            modalUsuario.show();
        } catch (e) {
            showError('Erro: ' + e.message);
        }
    };

    window.excluirUsuario = async (id) => {
        if (!confirm('Confirmar exclusão?')) return;
        try {
            const res = await fetch(`/user/delete/${id}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao excluir usuário';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao excluir usuário';
                }
                showError(errorMsg);
                return;
            }
            showSuccess('Usuário excluído');
            carregarUsuarios();
        } catch (e) {
            showError('Erro: ' + e.message);
        }
    };

    const cpfInput = document.getElementById('cpf');
    cpfInput.addEventListener('input', () => aplicarMascaraCPF(cpfInput));

    document.getElementById('btn-add-usuario').addEventListener('click', () => {
        modoEdicao = { ativo: false, id: null };
        document.getElementById('formUsuario').reset();
        document.getElementById('senha-container').style.display = 'block'; // mostrar campo senha
        document.getElementById('modalUsuarioLabel').textContent = 'Adicionar Usuário';
    });

    function aplicarMascaraCPF(input) {
        let value = input.value.replace(/\D/g, '').slice(0, 11);
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        input.value = value;
    }
});
