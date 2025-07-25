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

    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        clearAlert();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                let errorMsg = 'Falha no login';
                try {
                    const errorText = await response.text();
                    // Tenta fazer o parse como JSON
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    // Se não for JSON, mostra o texto bruto
                    errorMsg = 'Falha no login';
                }
                showAlert(errorMsg);
                return;
            }

            if(response.status === 204) {
                console.log('Redirecionando para /dashboard');
                window.location.href = '/dashboard';
            }
            
        } catch (err) {
            console.error('Erro no login:', err);
            showAlert('Erro ao conectar ao servidor.');
        }
    });

    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            if (dashboardSection) dashboardSection.style.display = 'none';
            loginSection.style.display = 'block';
            clearAlert();
        });
    }
});