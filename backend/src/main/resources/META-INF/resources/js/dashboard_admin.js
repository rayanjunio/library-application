window.addEventListener('DOMContentLoaded', function() {
    const btnLogout = document.getElementById('btn-logout');
    const countLivros = document.getElementById('count-livros');
    const countUsuarios = document.getElementById('count-usuarios');
    const countEmprestimos = document.getElementById('count-emprestimos');

    // Logout
    btnLogout?.addEventListener('click', async () => {
        try {
            await fetch('/auth/logout', {
                method: 'POST',
                credentials: 'include'
            })
            window.location.href = '/';
        } catch (err) {
            console.error('Erro no logout:', err);
            window.location.href = '/';
        }
    });

    // Books count
    fetch('/book/count-all', { credentials: 'include' })
        .then(res => res.json())
        .then(data => { countLivros.textContent = data.allBooksCount; })
        .catch(() => { countLivros.textContent = '...'; });

    // Users count
    fetch('/user/count-all', { credentials: 'include' })
        .then(res => res.json())
        .then(data => { countUsuarios.textContent = data.usersCount; })
        .catch(() => { countUsuarios.textContent = '...'; });

    // Active loans count
    fetch('loan/count-all', { credentials: 'include' })
        .then(res => res.json())
        .then(data => countEmprestimos.textContent = data.activeLoansCount)
        .catch(() => { countEmprestimos.textContent = '...'; });

    let adminUserId = null;
    let adminUserData = null;

    // Botão Meus Dados
    document.getElementById('btn-admin-meus-dados')?.addEventListener('click', async () => {
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do admin');
            const user = await res.json();
            adminUserId = user.id;
            adminUserData = user;
            const modalBody = document.getElementById('adminUserModalBody');
            modalBody.innerHTML = `
                <ul class="list-group">
                    <li class="list-group-item"><strong>Nome:</strong> ${user.name}</li>
                    <li class="list-group-item"><strong>Email:</strong> ${user.email}</li>
                    <li class="list-group-item"><strong>CPF:</strong> ${user.cpf}</li>
                    <li class="list-group-item"><strong>Status:</strong> ${user.status}</li>
                    <li class="list-group-item"><strong>Perfil:</strong> ${user.profile}</li>
                </ul>
            `;
            const modal = new bootstrap.Modal(document.getElementById('adminUserModal'));
            modal.show();
        } catch (e) {
            alert('Erro ao carregar dados do admin: ' + e.message);
        }
    });

    // Abrir modal de edição ao clicar em 'Editar Dados'
    document.getElementById('btn-admin-edit-user')?.addEventListener('click', () => {
        if (!adminUserData) return;
        document.getElementById('admin-edit-user-name').value = adminUserData.name || '';
        document.getElementById('admin-edit-user-email').value = adminUserData.email || '';
        document.getElementById('admin-edit-user-cpf').value = adminUserData.cpf || '';
        document.getElementById('admin-edit-user-alert').innerHTML = '';
        const editModal = new bootstrap.Modal(document.getElementById('adminEditUserModal'));
        editModal.show();
    });

    // Submeter edição de dados
    document.getElementById('adminEditUserForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const name = document.getElementById('admin-edit-user-name').value.trim();
        const email = document.getElementById('admin-edit-user-email').value.trim();
        const cpf = document.getElementById('admin-edit-user-cpf').value.trim();
        const alertDiv = document.getElementById('admin-edit-user-alert');
        alertDiv.innerHTML = '';
        if (name.length < 2) {
            alertDiv.innerHTML = '<div class="alert alert-danger">Nome deve ter pelo menos 2 caracteres.</div>';
            return;
        }
        if (!email.match(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)) {
            alertDiv.innerHTML = '<div class="alert alert-danger">E-mail inválido.</div>';
            return;
        }
        if (cpf.length < 11) {
            alertDiv.innerHTML = '<div class="alert alert-danger">CPF inválido.</div>';
            return;
        }
        try {
            const res = await fetch(`/user/update/${adminUserId}`, {
                method: 'PUT',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, cpf })
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao atualizar dados';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao atualizar dados';
                }
                alertDiv.innerHTML = `<div class=\"alert alert-danger\">${errorMsg}</div>`;
                return;
            }
            alertDiv.innerHTML = '<div class=\"alert alert-success\">Dados atualizados com sucesso!</div>';
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('adminEditUserModal')).hide();
                document.getElementById('btn-admin-meus-dados').click();
            }, 1200);
        } catch (err) {
            alertDiv.innerHTML = `<div class=\"alert alert-danger\">Erro ao atualizar dados: ${err.message}</div>`;
        }
    });

    // Abrir modal de alteração de senha
    document.getElementById('btn-admin-change-password')?.addEventListener('click', () => {
        document.getElementById('admin-change-password-alert').innerHTML = '';
        document.getElementById('admin-current-password').value = '';
        document.getElementById('admin-new-password').value = '';
        document.getElementById('admin-confirm-new-password').value = '';
        const modal = new bootstrap.Modal(document.getElementById('adminChangePasswordModal'));
        modal.show();
    });

    // Submeter alteração de senha
    document.getElementById('adminChangePasswordForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('admin-current-password').value;
        const newPassword = document.getElementById('admin-new-password').value;
        const confirmNewPassword = document.getElementById('admin-confirm-new-password').value;
        const alertDiv = document.getElementById('admin-change-password-alert');
        alertDiv.innerHTML = '';
        if (newPassword.length < 8) {
            alertDiv.innerHTML = '<div class="alert alert-danger">A nova senha deve ter pelo menos 8 caracteres.</div>';
            return;
        }
        if (newPassword !== confirmNewPassword) {
            alertDiv.innerHTML = '<div class="alert alert-danger">A confirmação da nova senha não confere.</div>';
            return;
        }
        try {
            const res = await fetch('/user/set-password', {
                method: 'PATCH',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ currentPassword, newPassword, confirmNewPassword })
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao alterar senha';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao alterar senha';
                }
                alertDiv.innerHTML = `<div class=\"alert alert-danger\">${errorMsg}</div>`;
                return;
            }
            alertDiv.innerHTML = '<div class=\"alert alert-success\">Senha alterada com sucesso!</div>';
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('adminChangePasswordModal')).hide();
            }, 1200);
        } catch (err) {
            alertDiv.innerHTML = `<div class=\"alert alert-danger\">Erro ao alterar senha: ${err.message}</div>`;
        }
    });
}); 