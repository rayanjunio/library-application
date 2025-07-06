// JS para gerenciamento de usuários
window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    if (!token || userProfile !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }
    const alertArea = document.getElementById('alert-area');
    const tabelaUsuarios = document.getElementById('tabelaUsuarios');
    const btnLogout = document.getElementById('btn-logout');
    const formUsuario = document.getElementById('formUsuario');

    function showAlert(message, type = 'danger') {
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }

    function clearAlert() {
        alertArea.innerHTML = '';
    }

    function getAuthHeaders() {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    }

    async function carregarUsuarios() {
        try {
            const response = await fetch('http://localhost:8080/users', {
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                throw new Error('Erro ao carregar usuários');
            }
            const usuarios = await response.json();
            exibirUsuarios(usuarios);
        } catch (error) {
            showAlert('Erro ao carregar usuários: ' + error.message);
        }
    }

    function exibirUsuarios(usuarios) {
        tabelaUsuarios.innerHTML = '';
        usuarios.forEach(usuario => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${usuario.id}</td>
                <td>${usuario.name}</td>
                <td>${usuario.email}</td>
                <td>${usuario.cpf}</td>
                <td><span class="badge bg-${usuario.status === 'ACTIVE' ? 'success' : 'danger'}">${usuario.status}</span></td>
                <td>${usuario.profile}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarUsuario(${usuario.id})">Editar</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="excluirUsuario(${usuario.id})">Excluir</button>
                </td>
            `;
            tabelaUsuarios.appendChild(row);
        });
    }

    // Formulário de adicionar usuário
    if (formUsuario) {
        formUsuario.addEventListener('submit', async function(e) {
            e.preventDefault();
            const nome = document.getElementById('nome').value;
            const email = document.getElementById('email').value;
            const senha = document.getElementById('senha').value;
            const cpf = document.getElementById('cpf').value;

            try {
                const response = await fetch('http://localhost:8080/users', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        name: nome,
                        email: email,
                        password: senha,
                        cpf: cpf
                    })
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error);
                }

                showAlert('Usuário adicionado com sucesso!', 'success');
                carregarUsuarios();
                // Fechar modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('modalUsuario'));
                modal.hide();
                formUsuario.reset();
            } catch (error) {
                showAlert('Erro ao adicionar usuário: ' + error.message);
            }
        });
    }

    // Logout
    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            window.location.href = 'index.html';
        });
    }

    // Edição de usuário
    window.editarUsuario = async function(id) {
        try {
            const response = await fetch(`http://localhost:8080/users/${id}`, {
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                throw new Error('Erro ao carregar dados do usuário');
            }
            const usuario = await response.json();
            document.getElementById('editar-id').value = usuario.id;
            document.getElementById('editar-nome').value = usuario.name;
            document.getElementById('editar-email').value = usuario.email;
            document.getElementById('editar-cpf').value = usuario.cpf;
            const modal = new bootstrap.Modal(document.getElementById('modalEditarUsuario'));
            modal.show();
        } catch (error) {
            showAlert('Erro ao carregar dados do usuário: ' + error.message);
        }
    };

    // Submissão do formulário de edição
    const formEditarUsuario = document.getElementById('formEditarUsuario');
    if (formEditarUsuario) {
        formEditarUsuario.addEventListener('submit', async function(e) {
            e.preventDefault();
            const id = document.getElementById('editar-id').value;
            const nome = document.getElementById('editar-nome').value;
            const email = document.getElementById('editar-email').value;
            const cpf = document.getElementById('editar-cpf').value;
            try {
                const response = await fetch(`http://localhost:8080/users/${id}`, {
                    method: 'PUT',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ name: nome, email: email, cpf: cpf })
                });
                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error);
                }
                showAlert('Usuário atualizado com sucesso!', 'success');
                const modal = bootstrap.Modal.getInstance(document.getElementById('modalEditarUsuario'));
                modal.hide();
                carregarUsuarios();
            } catch (error) {
                showAlert('Erro ao atualizar usuário: ' + error.message);
            }
        });
    }

    // Exclusão de usuário
    window.excluirUsuario = async function(id) {
        if (confirm('Tem certeza que deseja excluir este usuário?')) {
            try {
                const response = await fetch(`http://localhost:8080/users/${id}`, {
                    method: 'DELETE',
                    headers: getAuthHeaders()
                });
                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error);
                }
                showAlert('Usuário excluído com sucesso!', 'success');
                carregarUsuarios();
            } catch (error) {
                showAlert('Erro ao excluir usuário: ' + error.message);
            }
        }
    };

    // Carregar usuários ao iniciar
    carregarUsuarios();
});

// Funções globais para editar e excluir
function editarUsuario(id) {
    // Implementar edição
    console.log('Editar usuário:', id);
}

function excluirUsuario(id) {
    if (confirm('Tem certeza que deseja excluir este usuário?')) {
        // Implementar exclusão
        console.log('Excluir usuário:', id);
    }
} 