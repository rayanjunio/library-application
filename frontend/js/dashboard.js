// JS base para dashboard
window.addEventListener('DOMContentLoaded', function() {
    const btnLogout = document.getElementById('btn-logout');
    const navLivros = document.getElementById('nav-livros');
    const navUsuarios = document.getElementById('nav-usuarios');
    const navEmprestimos = document.getElementById('nav-emprestimos');

    // Verificar se o usuário está logado
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    // Verificar perfil do usuário e esconder abas se não for ADMIN
    const userProfile = localStorage.getItem('userProfile');
    if (userProfile !== 'ADMIN') {
        if (navUsuarios) navUsuarios.style.display = 'none';
        if (navEmprestimos) navEmprestimos.style.display = 'none';
    }

    // Navegação
    if (navLivros) {
        navLivros.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = 'livros.html';
        });
    }

    if (navUsuarios) {
        navUsuarios.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = 'usuarios.html';
        });
    }

    if (navEmprestimos) {
        navEmprestimos.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = 'emprestimos.html';
        });
    }

    // Logout
    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            window.location.href = 'index.html';
        });
    }
}); 