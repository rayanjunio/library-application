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

    // Botão Meus Dados
    const btnMeusDados = document.getElementById('btn-meus-dados');
    btnMeusDados?.addEventListener('click', async () => {
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            const modalBody = document.getElementById('userModalBody');
            modalBody.innerHTML = `
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
            alert('Erro ao carregar dados do usuário: ' + e.message);
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