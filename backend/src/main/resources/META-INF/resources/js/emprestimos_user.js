window.addEventListener('DOMContentLoaded', function() {
    const btnLogout = document.getElementById('btn-logout');
    const emprestimosTbody = document.getElementById('emprestimos-tbody');

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
            alert('Conta encerrada com sucesso!');
            window.location.href = '/';
        } catch (err) {
            alert('Erro ao encerrar conta: ' + err.message);
        }
    });

    // Botão Meus Dados
    const btnMeusDados = document.getElementById('btn-meus-dados');
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
            showModalAlert('edit-user-alert', 'Nome deve ter pelo menos 2 caracteres.');
            return;
        }
        if (!email.match(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)) {
            showModalAlert('edit-user-alert', 'E-mail inválido.');
            return;
        }
        if (cpf.length < 11) {
            showModalAlert('edit-user-alert', 'CPF inválido.');
            return;
        }
        try {
            const res = await fetch(`/user/update`, {
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
                showModalAlert('edit-user-alert', errorMsg);
                return;
            }
            showModalAlert('edit-user-alert', 'Dados atualizados com sucesso!', 'success');
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
                btnMeusDados.click();
            }, 1200);
        } catch (err) {
            showModalAlert('edit-user-alert', 'Erro ao atualizar dados: ' + err.message);
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
            showModalAlert('change-password-alert', 'A nova senha deve ter pelo menos 8 caracteres.');
            return;
        }
        if (newPassword !== confirmNewPassword) {
            showModalAlert('change-password-alert', 'A confirmação da nova senha não confere.');
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
                showModalAlert('change-password-alert', errorMsg);
                return;
            }
            showModalAlert('change-password-alert', 'Senha alterada com sucesso!', 'success');
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('changePasswordModal')).hide();
            }, 1200);
        } catch (err) {
            showModalAlert('change-password-alert', 'Erro ao alterar senha: ' + err.message);
        }
    });

    function formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    }

    function calculateDaysRemaining(returnDate) {
        if (!returnDate) return null;
        const today = new Date();
        const returnDateObj = new Date(returnDate);
        const diffTime = returnDateObj - today;
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }

    // Add fine alert at the top
    function showFineAlertIfNeeded() {
        fetch('/user/get', { credentials: 'include' })
            .then(res => res.ok ? res.json() : null)
            .then(user => {
                if (user && user.status === 'FINED') {
                    const container = document.querySelector('.container');
                    if (container && !document.getElementById('fine-alert')) {
                        const div = document.createElement('div');
                        div.id = 'fine-alert';
                        div.className = 'alert alert-danger mb-3';
                        div.innerHTML = "<i class='bi bi-exclamation-triangle'></i> Você está multado e não pode fazer novos empréstimos até regularizar sua situação.";
                        container.prepend(div);
                    }
                }
            });
    }
    showFineAlertIfNeeded();

    fetch(`/loan/get-from-user`, { credentials: 'include' })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorText => { throw new Error(errorText); });
        }
        return response.json();
    })
    .then(data => {
        emprestimosTbody.innerHTML = '';
        const emprestimos = data.content || [];
        
        if (emprestimos.length === 0) {
            emprestimosTbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center py-5">
                        <i class="bi bi-journal-x display-4 text-muted"></i>
                        <h5 class="mt-3 text-muted">Nenhum empréstimo encontrado</h5>
                        <p class="text-muted">Você ainda não possui empréstimos ativos.</p>
                    </td>
                </tr>`;
            return;
        }

        emprestimosTbody.innerHTML = emprestimos.map(emp => {
            const daysRemaining = calculateDaysRemaining(emp.expectedReturnDate);
            const isOverdue = daysRemaining < 0;
            const isActive = emp.isActive === true || emp.active === true;

            let statusDisplay;
            if (isActive) {
                statusDisplay = isOverdue
                    ? '<span class="badge bg-danger">ATRASADO</span>'
                    : '<span class="badge bg-success">ATIVO</span>';
            } else {
                statusDisplay = '<span class="badge bg-secondary">FINALIZADO</span>';
            }

            return `
                <tr class="${isOverdue && isActive ? 'table-danger' : ''}">
                    <td>
                        <strong>${emp.bookTitle || 'Livro não encontrado'}</strong>
                        ${emp.bookAuthor ? `<br><small class="text-muted">${emp.bookAuthor}</small>` : ''}
                    </td>
                    <td>${formatDate(emp.loanDate)}</td>
                    <td>
                        ${formatDate(emp.expectedReturnDate)}
                        ${isActive && daysRemaining !== null ? `
                            <br><small class="${isOverdue ? 'text-danger' : 'text-muted'}">
                                ${isOverdue ? `${Math.abs(daysRemaining)} dias atrasado` : `${daysRemaining} dias restantes`}
                            </small>` : ''}
                    </td>
                    <td>${statusDisplay}</td>
                </tr>`;
        }).join('');
    })
        .catch(error => {
            let errorMsg = 'Erro ao carregar empréstimos';
            try {
                const errorJson = JSON.parse(error.message);
                if (errorJson && errorJson.message) {
                    errorMsg = errorJson.message;
                } else {
                    errorMsg = error.message;
                }
            } catch (e) {
                errorMsg = error.message;
            }
            emprestimosTbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-5">
                    <i class="bi bi-exclamation-triangle display-4 text-danger"></i>
                    <h5 class="mt-3 text-danger">Erro ao carregar empréstimos</h5>
                    <p class="text-muted">${errorMsg}</p>
                </td>
            </tr>`;
        });
}); 