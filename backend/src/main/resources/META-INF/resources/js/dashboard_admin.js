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
}); 