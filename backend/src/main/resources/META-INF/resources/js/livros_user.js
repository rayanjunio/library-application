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
            return `<span class="badge bg-success">Disponível (${availableQuantity}/${totalQuantity})</span>`;
        } else {
            return `<span class="badge bg-danger">Indisponível</span>`;
        }
    }

    function renderBooks(books) {
        if (books.length === 0) {
            booksContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="bi bi-book display-1 text-muted"></i>
                    <h4 class="mt-3 text-muted">Nenhum livro encontrado</h4>
                    <p class="text-muted">Tente ajustar os filtros de busca</p>
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
                        <strong>Autor:</strong> ${book.author || 'Não informado'}
                    </p>
                    <p class="card-text text-muted mb-2">
                        <strong>ISBN:</strong> ${book.isbn || 'Não informado'}
                    </p>
                    <p class="card-text mb-3">
                        ${getStatusBadge(book.availableQuantity, book.quantity)}
                    </p>
                    <button class="btn btn-outline-primary btn-sm" onclick="showBookDetails(${book.id})">
                        Ver Detalhes
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

        // Botão anterior
        paginationHTML += `
        <li class="page-item ${current === 0 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${current - 1})">Anterior</a>
        </li>
    `;

        // Páginas
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

        // Botão próximo
        paginationHTML += `
        <li class="page-item ${current === totalPages - 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${current + 1})">Próximo</a>
        </li>
    `;

        pagination.innerHTML = paginationHTML;
    }

    function filterBooks() {
        const searchTerm = searchInput.value.toLowerCase();

        filteredBooks = allBooks.filter(book => {
            const matchesSearch = !searchTerm ||
                book.title.toLowerCase().includes(searchTerm) ||
                (book.author && book.author.toLowerCase().includes(searchTerm)) ||
                (book.isbn && book.isbn.toLowerCase().includes(searchTerm));

            return matchesSearch;
        });

        //currentPage = 0;
        renderBooks(filteredBooks);
        renderPagination(filteredBooks.length);
    }

    function loadBooks() {
        showLoading();
        clearAlert();

        fetch(`/book/available-books?page=${currentPage}`, {credentials: 'include'})
            .then(response => {
                if (!response.ok) throw new Error('Erro ao carregar livros');
                return response.json();
            })
            .then(data => {
                availableBooks = data.content;
                //filteredBooks = data;
                hideLoading();
                renderBooks(availableBooks);
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

    searchBtn.addEventListener('click', filterBooks);
    searchInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            filterBooks();
        }
    });

    // Funções globais para paginação e modal
    window.changePage = function (page) {
        currentPage = page;
        loadBooks();
    };

    window.showBookDetails = function (bookId) {
        const book = (availableBooks || []).find(b => b.id === bookId);
        if (!book) {
            showAlert('Livro não encontrado!');
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
                            <p><strong>Autor:</strong> ${data.author || 'Não informado'}</p>
                            <p><strong>ISBN:</strong> ${data.isbn}</p>
                            <p><strong>Quantidade total:</strong> ${data.quantity}</p>
                            <p><strong>Disponíveis:</strong> ${data.availableQuantity}</p>
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
    loadBooks();
}); 