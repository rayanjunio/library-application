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

    // Botão Meus Dados
    const btnMeusDados = document.getElementById('btn-meus-dados');
    function showAlert(message, type = 'danger') {
        const alertArea = document.getElementById('alert-area');
        if (!alertArea) {
            alert(message);
            return;
        }
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }
    btnMeusDados?.addEventListener('click', async () => {
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            const modalBody = document.getElementById('userModalBody');
            modalBody.innerHTML = `
                <ul class="list-group">
                    <li class="list-group-item"><strong>Nome:</strong> ${user.name}</li>
                    <li class="list-group-item"><strong>Email:</strong> ${user.email}</li>
                    <li class="list-group-item"><strong>CPF:</strong> ${user.cpf}</li>
                    <li class="list-group-item"><strong>Status:</strong> ${user.status}</li>
                    <li class="list-group-item"><strong>Perfil:</strong> ${user.profile}</li>
                </ul>
            `;
            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        } catch (e) {
            showAlert('Erro ao carregar dados do usuário: ' + e.message);
        }
    });
}); 