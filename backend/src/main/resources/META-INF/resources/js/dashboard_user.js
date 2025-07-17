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
}); 