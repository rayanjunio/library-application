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

    function aplicarMascaraCPF(input) {
        let value = input.value.replace(/\D/g, '').slice(0, 11);
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        input.value = value;
    }

    const cpfInput = document.getElementById('register-cpf');
    cpfInput.addEventListener('input', () => aplicarMascaraCPF(cpfInput));

    const nameInput = document.getElementById('register-name');
    nameInput.focus();

    document.getElementById('registerForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        clearAlert();

        const name = nameInput.value.trim();
        const email = document.getElementById('register-email').value.trim();
        const password = document.getElementById('register-password').value;
        const cpf = cpfInput.value;

        // Validações
        if (name.length < 2) return showAlert('Nome deve ter pelo menos 2 caracteres.');
        if (password.length < 8) return showAlert('Senha deve ter pelo menos 8 caracteres.');

        // Mostrar loading
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Cadastrando...';
        submitBtn.disabled = true;

        try {
            const response = await fetch('/user/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password, cpf })
            });

            if (!response.ok) {
                let errorMsg = 'Erro no cadastro';
                try {
                    const errorText = await response.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro no cadastro';
                }
                showAlert(errorMsg);
                return;
            }

            showAlert('Cadastro realizado com sucesso! Redirecionando para login...', 'success');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } catch (err) {
            showAlert('Erro no cadastro: ' + err.message);
        } finally {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    });
}); 