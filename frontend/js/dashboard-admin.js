window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    
    // Verificar se está logado e se é admin
    if (!token || userProfile !== 'ADMIN') {
        console.log('Redirecionando: token=', !!token, 'profile=', userProfile);
        window.location.href = 'index.html';
        return;
    }
    const btnLogout = document.getElementById('btn-logout');
    const countLivros = document.getElementById('count-livros');
    const countUsuarios = document.getElementById('count-usuarios');
    const countEmprestimos = document.getElementById('count-emprestimos');

    // Logout
    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            localStorage.removeItem('userProfile');
            window.location.href = 'index.html';
        });
    }

    // Buscar estatísticas
    if (token) {
        fetch('http://localhost:8080/api/books', { headers: { 'Authorization': `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => { countLivros.textContent = data.length; })
            .catch(() => { countLivros.textContent = '...'; });

        fetch('http://localhost:8080/users', { headers: { 'Authorization': `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => { countUsuarios.textContent = data.length; })
            .catch(() => { countUsuarios.textContent = '...'; });

        // Para admin, buscar todos os usuários e contar empréstimos ativos
        fetch('http://localhost:8080/users', { headers: { 'Authorization': `Bearer ${token}` } })
            .then(res => res.json())
            .then(users => {
                let totalAtivos = 0;
                users.forEach(user => {
                    if (user.loans) {
                        // Verificar se o empréstimo está ativo usando o campo isActive
                        const ativos = user.loans.filter(e => e.isActive === true || e.active === true);
                        totalAtivos += ativos.length;
                    }
                });
                countEmprestimos.textContent = totalAtivos;
            })
            .catch(() => { countEmprestimos.textContent = '...'; });
    }
}); 