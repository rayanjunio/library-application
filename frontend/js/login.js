// Alternância entre telas
window.addEventListener('DOMContentLoaded', function() {
    const loginSection = document.getElementById('login-section');
    const alertArea = document.getElementById('alert-area');
    const dashboardSection = document.getElementById('dashboard-section');
    const btnLogout = document.getElementById('btn-logout');

    function showAlert(message, type = 'danger') {
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }

    function clearAlert() {
        alertArea.innerHTML = '';
    }

    function showDashboard() {
        loginSection.style.display = 'none';
        if (dashboardSection) dashboardSection.style.display = 'block';
    }

    // Função para decodificar JWT e extrair payload
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

    // Função para extrair perfil do JWT
    function extractProfileFromJwt(payload) {
        // O backend retorna o perfil no campo 'groups' do JWT
        if (payload && payload.groups && Array.isArray(payload.groups)) {
            // Se tem ADMIN nos groups, é admin
            if (payload.groups.includes('ADMIN')) {
                return 'ADMIN';
            }
            // Se tem MEMBER nos groups, é usuário comum
            if (payload.groups.includes('MEMBER')) {
                return 'USER';
            }
        }
        // Fallback para campos antigos (role/profile)
        if (payload && (payload.role || payload.profile)) {
            return (payload.role || payload.profile || '').toUpperCase();
        }
        return 'USER'; // Default para usuário comum
    }

    // Login
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        clearAlert();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        try {
            const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
    });
            if (!response.ok) {
                const error = await response.text();
                showAlert('Falha no login: ' + error);
                return;
            }
            const data = await response.json();
        localStorage.setItem('token', data.token);
            
            // Extrair perfil do JWT
            const payload = parseJwt(data.token);
            console.log('JWT Payload completo:', payload); // Debug completo
            if (payload) {
                const profile = extractProfileFromJwt(payload);
                localStorage.setItem('userProfile', profile);
                console.log('Perfil extraído:', profile); // Debug
                
                if (profile === 'ADMIN') {
                    window.location.href = 'dashboard-admin.html';
    } else {
                    window.location.href = 'dashboard-user.html';
                }
                return;
            }
            
            showAlert('Erro ao processar token de autenticação.');
        } catch (err) {
            showAlert('Erro ao conectar ao servidor.');
        }
    });

    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            localStorage.removeItem('userProfile');
            if (dashboardSection) dashboardSection.style.display = 'none';
            loginSection.style.display = 'block';
            clearAlert();
        });
    }
});