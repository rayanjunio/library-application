window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    
    // Proteção de acesso
    if (!token || userProfile === 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    const btnLogout = document.getElementById('btn-logout');
    const emprestimosTbody = document.getElementById('emprestimos-tbody');

    // Logout
    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            localStorage.removeItem('userProfile');
            window.location.href = 'index.html';
        });
    }

    // Função para decodificar JWT
    function parseJwt (token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }

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
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }

    // Buscar empréstimos do usuário logado
    const payload = parseJwt(token);
    const userId = payload && payload.userId ? payload.userId : null;

    if (!userId) {
        emprestimosTbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Erro ao identificar usuário.</td></tr>';
        return;
    }

    fetch(`http://localhost:8080/users/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao carregar dados do usuário');
        }
        return response.json();
    })
    .then(userData => {
        emprestimosTbody.innerHTML = '';
        const meus = userData.loans || [];
        
        if (meus.length === 0) {
            emprestimosTbody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center py-5">
                        <i class="bi bi-journal-x display-4 text-muted"></i>
                        <h5 class="mt-3 text-muted">Nenhum empréstimo encontrado</h5>
                        <p class="text-muted">Você ainda não possui empréstimos ativos.</p>
                    </td>
                </tr>
            `;
            return;
        }

        meus.forEach(emp => {
            const daysRemaining = calculateDaysRemaining(emp.expectedReturnDate);
            const isOverdue = daysRemaining < 0;
            // Verificar se o empréstimo está ativo usando o campo isActive
            const isActive = emp.isActive === true || emp.active === true;
            
            // Determinar o status baseado no campo isActive
            let statusDisplay;
            if (isActive) {
                if (isOverdue) {
                    statusDisplay = '<span class="badge bg-danger">ATRASADO</span>';
                } else {
                    statusDisplay = '<span class="badge bg-success">ATIVO</span>';
                }
            } else {
                statusDisplay = '<span class="badge bg-secondary">FINALIZADO</span>';
            }

            emprestimosTbody.innerHTML += `
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
                            </small>
                        ` : ''}
                    </td>
                    <td>${statusDisplay}</td>
                </tr>
            `;
        });
    })
    .catch(error => {
        emprestimosTbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-5">
                    <i class="bi bi-exclamation-triangle display-4 text-danger"></i>
                    <h5 class="mt-3 text-danger">Erro ao carregar empréstimos</h5>
                    <p class="text-muted">${error.message}</p>
                </td>
            </tr>
        `;
    });
}); 