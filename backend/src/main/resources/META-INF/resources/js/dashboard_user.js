window.addEventListener('DOMContentLoaded', function() {
    const btnLogout = document.getElementById('btn-logout');
    const countLivros = document.getElementById('count-livros');
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

    // Available books count
    fetch('/book/count-available', { credentials: 'include' })
        .then(res => res.ok ? res.json() : Promise.reject())
        .then(data => countLivros.textContent = data.availableBooksCount)
        .catch(() => countLivros.textContent = '...');

    // Active loans from user count
    fetch('/loan/count-from-user', { credentials: 'include' })
        .then(res => res.ok ? res.json() : Promise.reject())
        .then(data => countEmprestimos.textContent = data.activeLoansCount)
        .catch(() => countEmprestimos.textContent = '...');

    // Botão Meus Dados
    const btnMeusDados = document.getElementById('btn-meus-dados');
    function showAlert(message, type = 'danger') {
        const alertArea = document.getElementById('alert-area');
        if (!alertArea) {
            alert(message);
            return;
        }
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }
    let currentUserId = null;
    let currentUserData = null;

    btnMeusDados?.addEventListener('click', async () => {
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            currentUserId = user.id;
            currentUserData = user;
            const modalBody = document.getElementById('userModalBody');
            let fineAlertHtml = '';
            if (user.status === 'FINED') {
                fineAlertHtml = `<div class='alert alert-danger mb-3'><i class='bi bi-exclamation-triangle'></i> Você está multado e não pode fazer novos empréstimos até regularizar sua situação.</div>`;
            }
            modalBody.innerHTML = `
                ${fineAlertHtml}
                <ul class="list-group">
                    <li class="list-group-item"><strong>Nome:</strong> ${user.name}</li>
                    <li class="list-group-item"><strong>Email:</strong> ${user.email}</li>
                    <li class="list-group-item"><strong>CPF:</strong> ${user.cpf}</li>
                    <li class="list-group-item"><strong>Status:</strong> ${user.status}</li>
                    <li class="list-group-item"><strong>Perfil:</strong> ${user.profile}</li>
                </ul>
            `;
            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        } catch (e) {
            showAlert('Erro ao carregar dados do usuário: ' + e.message);
        }
    });

    // Abrir modal de edição ao clicar em 'Editar Dados'
    document.getElementById('btn-edit-user')?.addEventListener('click', () => {
        if (!currentUserData) return;
        document.getElementById('edit-user-name').value = currentUserData.name || '';
        document.getElementById('edit-user-email').value = currentUserData.email || '';
        document.getElementById('edit-user-cpf').value = currentUserData.cpf || '';
        document.getElementById('edit-user-alert').innerHTML = '';
        const editModal = new bootstrap.Modal(document.getElementById('editUserModal'));
        editModal.show();
    });

    // Submeter edição de dados
    document.getElementById('editUserForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const name = document.getElementById('edit-user-name').value.trim();
        const email = document.getElementById('edit-user-email').value.trim();
        const cpf = document.getElementById('edit-user-cpf').value.trim();
        const alertDiv = document.getElementById('edit-user-alert');
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
            const res = await fetch(`/user/update/${currentUserId}`, {
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
                alertDiv.innerHTML = `<div class="alert alert-danger">${errorMsg}</div>`;
                return;
            }
            alertDiv.innerHTML = '<div class="alert alert-success">Dados atualizados com sucesso!</div>';
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
                // Atualizar dados exibidos no modal principal
                btnMeusDados.click();
            }, 1200);
        } catch (err) {
            alertDiv.innerHTML = `<div class="alert alert-danger">Erro ao atualizar dados: ${err.message}</div>`;
        }
    });

    // Botão Meus Dados (agora pelo menu)
    document.getElementById('menu-user-meus-dados')?.addEventListener('click', async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            currentUserId = user.id;
            currentUserData = user;
            const modalBody = document.getElementById('userModalBody');
            let fineAlertHtml = '';
            if (user.status === 'FINED') {
                fineAlertHtml = `<div class='alert alert-danger mb-3'><i class='bi bi-exclamation-triangle'></i> Você está multado e não pode fazer novos empréstimos até regularizar sua situação.</div>`;
            }
            modalBody.innerHTML = `
                ${fineAlertHtml}
                <ul class="list-group">
                    <li class="list-group-item"><strong>Nome:</strong> ${user.name}</li>
                    <li class="list-group-item"><strong>Email:</strong> ${user.email}</li>
                    <li class="list-group-item"><strong>CPF:</strong> ${user.cpf}</li>
                    <li class="list-group-item"><strong>Status:</strong> ${user.status}</li>
                    <li class="list-group-item"><strong>Perfil:</strong> ${user.profile}</li>
                </ul>
            `;
            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        } catch (e) {
            showAlert('Erro ao carregar dados do usuário: ' + e.message);
        }
    });

    // Abrir modal de alteração de senha (agora pelo menu)
    document.getElementById('menu-user-change-password')?.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('change-password-alert').innerHTML = '';
        document.getElementById('current-password').value = '';
        document.getElementById('new-password').value = '';
        document.getElementById('confirm-new-password').value = '';
        const modal = new bootstrap.Modal(document.getElementById('changePasswordModal'));
        modal.show();
    });

    // Submeter alteração de senha
    document.getElementById('changePasswordForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmNewPassword = document.getElementById('confirm-new-password').value;
        const alertDiv = document.getElementById('change-password-alert');
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
                alertDiv.innerHTML = `<div class="alert alert-danger">${errorMsg}</div>`;
                return;
            }
            alertDiv.innerHTML = '<div class="alert alert-success">Senha alterada com sucesso!</div>';
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('changePasswordModal')).hide();
            }, 1200);
        } catch (err) {
            alertDiv.innerHTML = `<div class="alert alert-danger">Erro ao alterar senha: ${err.message}</div>`;
        }
    });
}); 