// JS para cadastro de usuário
window.addEventListener('DOMContentLoaded', function() {
    const alertArea = document.getElementById('alert-area');

    function showAlert(message, type = 'danger') {
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }

    function clearAlert() {
        alertArea.innerHTML = '';
    }

    document.getElementById('registerForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        clearAlert();
        
        const name = document.getElementById('register-name').value.trim();
        const email = document.getElementById('register-email').value.trim();
        const password = document.getElementById('register-password').value;
        const cpf = document.getElementById('register-cpf').value;

        // Validações mínimas
        if (name.length < 2) {
            showAlert('Nome deve ter pelo menos 2 caracteres.');
            return;
        }

        if (password.length < 8) {
            showAlert('Senha deve ter pelo menos 8 caracteres.');
            return;
        }

        // Mostrar loading
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Cadastrando...';
        submitBtn.disabled = true;

        try {
            const response = await fetch('http://localhost:8080/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    name, 
                    email, 
                    password, 
                    cpf 
                })
            });
            
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error);
            }
            
            showAlert('Cadastro realizado com sucesso! Redirecionando para login...', 'success');
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        } catch (err) {
            showAlert('Erro no cadastro: ' + err.message);
        } finally {
            // Restaurar botão
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    });
}); 