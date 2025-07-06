window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    
    // Verificar se está logado e se é usuário comum
    if (!token || userProfile === 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }
    
    const btnLogout = document.getElementById('btn-logout');
    const countLivros = document.getElementById('count-livros');
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
        fetch('http://localhost:8080/api/books/available/', { headers: { 'Authorization': `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => { countLivros.textContent = data.length; })
            .catch(() => { countLivros.textContent = '...'; });

        // Buscar dados do usuário logado para obter empréstimos
        const payload = parseJwt(token);
        const userId = payload && payload.userId ? payload.userId : null;
        
        if (userId) {
            fetch(`http://localhost:8080/users/${userId}`, { headers: { 'Authorization': `Bearer ${token}` } })
                .then(res => res.json())
                .then(userData => {
                    const meus = userData.loans || [];
                    // Verificar se o empréstimo está ativo usando o campo isActive
                    const ativos = meus.filter(e => e.isActive === true || e.active === true);
                    countEmprestimos.textContent = ativos.length;
                })
                .catch(() => { countEmprestimos.textContent = '...'; });
        } else {
            countEmprestimos.textContent = '...';
        }
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
}); 