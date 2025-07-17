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

    fetch(`/loan/get-from-user`, { credentials: 'include' })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao carregar dados do usuário');
        }
        return response.json();
    })
    .then(userData => {
        emprestimosTbody.innerHTML = '';
        const emprestimos = userData.activeLoans || [];
        
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
            emprestimosTbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-5">
                    <i class="bi bi-exclamation-triangle display-4 text-danger"></i>
                    <h5 class="mt-3 text-danger">Erro ao carregar empréstimos</h5>
                    <p class="text-muted">${error.message}</p>
                </td>
            </tr>`;
        });
}); 