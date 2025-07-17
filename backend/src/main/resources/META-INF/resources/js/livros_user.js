window.addEventListener('DOMContentLoaded', function() {
    const btnLogout = document.getElementById('btn-logout');
    const booksContainer = document.getElementById('books-container');
    const searchInput = document.getElementById('search-input');
    const searchBtn = document.getElementById('search-btn');
    const alertArea = document.getElementById('alert-area');
    const loading = document.getElementById('loading');
    const pagination = document.getElementById('pagination');

    let allBooks = [];
    let filteredBooks = [];
    let currentPage = 1;
    const booksPerPage = 12;

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

        const startIndex = (currentPage - 1) * booksPerPage;
        const endIndex = startIndex + booksPerPage;
        const booksToShow = books.slice(startIndex, endIndex);

        booksContainer.innerHTML = booksToShow.map(book => `
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

    function renderPagination(totalBooks) {
        const totalPages = Math.ceil(totalBooks / booksPerPage);
        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHTML = '';
        
        // Botão anterior
        paginationHTML += `
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Anterior</a>
            </li>
        `;

        // Páginas
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
                paginationHTML += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="changePage(${i})">${i}</a>
                    </li>
                `;
            } else if (i === currentPage - 3 || i === currentPage + 3) {
                paginationHTML += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // Botão próximo
        paginationHTML += `
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Próximo</a>
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

        currentPage = 1;
        renderBooks(filteredBooks);
        renderPagination(filteredBooks.length);
    }

    function loadBooks() {
        showLoading();
        clearAlert();

        fetch('/book/available-books', { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao carregar livros');

            return response.json();
        })
        .then(data => {
            allBooks = data;
            filteredBooks = data;
            hideLoading();
            renderBooks(filteredBooks);
            renderPagination(filteredBooks.length);
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
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            filterBooks();
        }
    });

    // Funções globais para paginação e modal
    window.changePage = function(page) {
        const totalPages = Math.ceil(filteredBooks.length / booksPerPage);
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            renderBooks(filteredBooks);
            renderPagination(filteredBooks.length);
        }
    };

    window.showBookDetails = function(bookId) {
        // Implementar modal de detalhes do livro se necessário
        alert('Funcionalidade de detalhes do livro será implementada em breve!');
    };

    // Carregar livros ao iniciar
    loadBooks();
}); 