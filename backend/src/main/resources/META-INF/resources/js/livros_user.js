window.addEventListener('DOMContentLoaded', function () {
    const btnLogout = document.getElementById('btn-logout');
    const booksContainer = document.getElementById('books-container');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const alertArea = document.getElementById('alert-area');
    const loading = document.getElementById('loading');
    const pagination = document.getElementById('pagination');

    let allBooks = [];
    let filteredBooks = [];
    let currentPage = 0;
    let currentUserId = null;
    let currentUserData = null;

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

    // Encerrar Conta
    const btnDeleteAccount = document.getElementById('btn-delete-account');
    btnDeleteAccount?.addEventListener('click', async () => {
        if (!confirm('Tem certeza que deseja encerrar sua conta? Esta ação é irreversível.')) return;
        try {
            const res = await fetch('/user/delete', {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!res.ok) throw new Error(await res.text());
            alert('Conta encerrada com sucesso!');
            window.location.href = '/';
        } catch (err) {
            alert('Erro ao encerrar conta: ' + err.message);
        }
    });

    // Button My Data
    const btnMeusDados = document.getElementById('btn-meus-dados');
    btnMeusDados?.addEventListener('click', async () => {
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            currentUserId = user.id;
            currentUserData = user;
            const modalBody = document.getElementById('userModalBody');
            let fineAlertHtml = '';
            if (user.status === 'FINED') {
                fineAlertHtml = `<div class='alert alert-danger mb-3'><i class='bi bi-exclamation-triangle'></i> Você está multado e não pode fazer novos empréstimos até regularizar sua situação.</div>`;
            }
            modalBody.innerHTML = `
                ${fineAlertHtml}
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

    // Abrir modal de edição ao clicar em 'Editar Dados'
    document.getElementById('btn-edit-user')?.addEventListener('click', () => {
        if (!currentUserData) return;
        document.getElementById('edit-user-name').value = currentUserData.name || '';
        document.getElementById('edit-user-email').value = currentUserData.email || '';
        document.getElementById('edit-user-cpf').value = currentUserData.cpf || '';
        document.getElementById('edit-user-alert').innerHTML = '';
        const editModal = new bootstrap.Modal(document.getElementById('editUserModal'));
        editModal.show();
    });

    // Submeter edição de dados
    document.getElementById('editUserForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const name = document.getElementById('edit-user-name').value.trim();
        const email = document.getElementById('edit-user-email').value.trim();
        const cpf = document.getElementById('edit-user-cpf').value.trim();
        const alertDiv = document.getElementById('edit-user-alert');
        alertDiv.innerHTML = '';
        if (name.length < 2) {
            showModalAlert('edit-user-alert', 'Nome deve ter pelo menos 2 caracteres.');
            return;
        }
        if (!email.match(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)) {
            showModalAlert('edit-user-alert', 'E-mail inválido.');
            return;
        }
        if (cpf.length < 11) {
            showModalAlert('edit-user-alert', 'CPF inválido.');
            return;
        }
        try {
            const res = await fetch(`/user/update`, {
                method: 'PUT',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, cpf })
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao atualizar dados';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao atualizar dados';
                }
                showModalAlert('edit-user-alert', errorMsg);
                return;
            }
            showModalAlert('edit-user-alert', 'Dados atualizados com sucesso!', 'success');
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
                btnMeusDados.click();
            }, 1200);
        } catch (err) {
            showModalAlert('edit-user-alert', 'Erro ao atualizar dados: ' + err.message);
        }
    });

    // Botão Meus Dados (agora pelo menu)
    document.getElementById('menu-user-meus-dados')?.addEventListener('click', async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('/user/get', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar dados do usuário');
            const user = await res.json();
            currentUserId = user.id;
            currentUserData = user;
            const modalBody = document.getElementById('userModalBody');
            let fineAlertHtml = '';
            if (user.status === 'FINED') {
                fineAlertHtml = `<div class='alert alert-danger mb-3'><i class='bi bi-exclamation-triangle'></i> Você está multado e não pode fazer novos empréstimos até regularizar sua situação.</div>`;
            }
            modalBody.innerHTML = `
                ${fineAlertHtml}
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

    // Abrir modal de alteração de senha (agora pelo menu)
    document.getElementById('menu-user-change-password')?.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('change-password-alert').innerHTML = '';
        document.getElementById('current-password').value = '';
        document.getElementById('new-password').value = '';
        document.getElementById('confirm-new-password').value = '';
        const modal = new bootstrap.Modal(document.getElementById('changePasswordModal'));
        modal.show();
    });

    // Submeter alteração de senha
    document.getElementById('changePasswordForm')?.addEventListener('submit', async function(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmNewPassword = document.getElementById('confirm-new-password').value;
        const alertDiv = document.getElementById('change-password-alert');
        alertDiv.innerHTML = '';
        if (newPassword.length < 8) {
            showModalAlert('change-password-alert', 'A nova senha deve ter pelo menos 8 caracteres.');
            return;
        }
        if (newPassword !== confirmNewPassword) {
            showModalAlert('change-password-alert', 'A confirmação da nova senha não confere.');
            return;
        }
        try {
            const res = await fetch('/user/set-password', {
                method: 'PATCH',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ currentPassword, newPassword, confirmNewPassword })
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao alterar senha';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao alterar senha';
                }
                showModalAlert('change-password-alert', errorMsg);
                return;
            }
            showModalAlert('change-password-alert', 'Senha alterada com sucesso!', 'success');
            setTimeout(() => {
                bootstrap.Modal.getInstance(document.getElementById('changePasswordModal')).hide();
            }, 1200);
        } catch (err) {
            showModalAlert('change-password-alert', 'Erro ao alterar senha: ' + err.message);
        }
    });

    function showAlert(message, type = 'danger') {
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }

    function clearAlert() {
        alertArea.innerHTML = '';
    }

    function showLoading() {
        loading.style.display = 'block';
        booksContainer.innerHTML = '';
    }

    function hideLoading() {
        loading.style.display = 'none';
    }

    function getStatusBadge(availableQuantity, totalQuantity) {
        if (availableQuantity > 0) {
            return `<span class="badge bg-success">Available (${availableQuantity}/${totalQuantity})</span>`;
        } else {
            return `<span class="badge bg-danger">Unavailable</span>`;
        }
    }

    function renderBooks(books) {
        if (books.length === 0) {
            booksContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="bi bi-book display-1 text-muted"></i>
                    <h4 class="mt-3 text-muted">No books found</h4>
                    <p class="text-muted">Try adjusting your search filters</p>
                </div>
            `;
            return;
        }

        booksContainer.innerHTML = books.map(book => `
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="card h-100 shadow-sm">
                <div class="card-body">
                    <h5 class="card-title text-truncate" title="${book.title}">${book.title}</h5>
                    <p class="card-text text-muted mb-2">
                        <strong>Author:</strong> ${book.author || 'Not informed'}
                    </p>
                    <p class="card-text text-muted mb-2">
                        <strong>ISBN:</strong> ${book.isbn || 'Not informed'}
                    </p>
                    <p class="card-text mb-3">
                        ${getStatusBadge(book.availableQuantity, book.quantity)}
                    </p>
                    <button class="btn btn-outline-primary btn-sm" onclick="showBookDetails(${book.id})">
                        View Details
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    }

    function renderPagination(total, current, size) {
        const totalPages = Math.ceil(total / size);
        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHTML = '';

        // Previous button
        paginationHTML += `
        <li class="page-item ${current === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${current - 1})">Previous</a>
        </li>
    `;

        // Pages
        for (let i = 0; i < totalPages; i++) {
            if (i === 0 || i === totalPages - 1 || (i >= current - 2 && i <= current + 2)) {
                paginationHTML += `
                <li class="page-item ${i === current ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
                </li>
            `;
            } else if (i === current - 3 || i === current + 3) {
                paginationHTML += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // Next button
        paginationHTML += `
        <li class="page-item ${current === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${current + 1})">Next</a>
        </li>
    `;
        pagination.innerHTML = paginationHTML;
    }

    // Função para mudar de página
    window.changePage = function(page) {
        currentPage = page;
        loadBooks();
    };

    // Função para mostrar detalhes do livro
    window.showBookDetails = function(bookId) {
        const book = (allBooks || []).find(b => b.id === bookId);
        if (!book) {
            showAlert('Book not found!');
            return;
        }
        fetch(`/book/get/${book.isbn}`, { credentials: 'include' })
            .then(response => {
                if (!response.ok) throw new Error('Erro ao buscar detalhes do livro');
                return response.json();
            })
            .then(data => {
                const modalBody = document.getElementById('bookModalBody');
                modalBody.innerHTML = `
                    <div class="row">
                        <div class="col-md-8">
                            <h4>${data.title}</h4>
                            <p><strong>Author:</strong> ${data.author || 'Not informed'}</p>
                            <p><strong>ISBN:</strong> ${data.isbn}</p>
                            <p><strong>Total quantity:</strong> ${data.quantity}</p>
                            <p><strong>Available:</strong> ${data.availableQuantity}</p>
                        </div>
                    </div>
                `;
                const modal = new bootstrap.Modal(document.getElementById('bookModal'));
                modal.show();
            })
            .catch(error => {
                showAlert('Erro ao carregar detalhes do livro: ' + error.message);
            });
    };

    // Carregar livros ao iniciar
    function loadBooks() {
        showLoading();
        clearAlert();
        fetch(`/book/available-books?page=${currentPage}`, {credentials: 'include'})
            .then(response => {
                if (!response.ok) throw new Error('Erro ao carregar livros');
                return response.json();
            })
            .then(data => {
                allBooks = data.content;
                hideLoading();
                renderBooks(allBooks);
                renderPagination(data.total, data.page, data.size);
            })
            .catch(error => {
                hideLoading();
                showAlert('Erro ao carregar livros: ' + error.message);
                booksContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="bi bi-exclamation-triangle display-1 text-danger"></i>
                    <h4 class="mt-3 text-danger">Erro ao carregar livros</h4>
                    <p class="text-muted">Tente novamente mais tarde</p>
                </div>
            `;
            });
    }

    // Eventos de busca
    searchBtn.addEventListener('click', () => loadBooks());
    searchInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            loadBooks();
        }
    });

    // Inicialização
    loadBooks();
}); 