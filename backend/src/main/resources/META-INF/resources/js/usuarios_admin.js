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

    function showModalUsuarioAlert(msg, type = 'danger') {
        const alertDiv = document.getElementById('modal-usuario-alert');
        if (alertDiv) {
            alertDiv.innerHTML = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${msg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>`;
            setTimeout(() => {
                if (alertDiv.querySelector('.alert')) {
                    alertDiv.querySelector('.alert').classList.remove('show');
                    setTimeout(() => { alertDiv.innerHTML = ''; }, 150);
                }
            }, 5000);
        }
    }

    formUsuario.addEventListener('submit', async e => {
        e.preventDefault();
        const nome = document.getElementById('nome').value.trim();
        const email = document.getElementById('email').value.trim();
        const cpf = document.getElementById('cpf').value.trim();
        const senha = document.getElementById('senha').value;

        if (nome.length < 2) return showModalUsuarioAlert('Nome inválido');
        if (email.length < 5) return showModalUsuarioAlert('Email inválido');
        if (!modoEdicao.ativo && (!senha || senha.length < 8)) return showModalUsuarioAlert('Senha inválida');

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
                showModalUsuarioAlert(errorMsg);
                return;
            }
            showModalUsuarioAlert(modoEdicao.ativo ? 'Atualizado!' : 'Criado!', 'success');
            setTimeout(() => {
                modalUsuario.hide();
                formUsuario.reset();
                document.getElementById('senha-container').style.display = 'block';
                modoEdicao = { ativo: false, id: null };
                carregarUsuarios();
            }, 1200);
        } catch (e) {
            showModalUsuarioAlert('Erro: ' + e.message);
        }
    });

    document.getElementById('btn-logout').addEventListener('click', () => {
        window.location.href = '/';
    });

    // Encerrar Conta
    const btnDeleteAccount = document.getElementById('btn-delete-account');
    btnDeleteAccount?.addEventListener('click', async () => {
        if (!confirm('Tem certeza que deseja encerrar sua conta? Esta ação é irreversível.')) return;
        try {
            const res = await fetch('/user/delete', {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!res.ok) throw new Error(await res.text());
            showModalUsuarioAlert('Conta encerrada com sucesso!', 'success');
            setTimeout(() => { window.location.href = '/'; }, 1500);
        } catch (err) {
            showModalUsuarioAlert('Erro ao encerrar conta: ' + err.message);
        }
    });

    carregarUsuarios();

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
