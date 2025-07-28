window.addEventListener('DOMContentLoaded', () => {
    const alertArea = document.getElementById('alert-area');
    const tabelaLivros = document.getElementById('tabelaLivros');
    const btnLogout = document.getElementById('btn-logout');
    const formLivro = document.getElementById('formLivro');
    const modalLivro = document.getElementById('modalLivro');
    const modalLabel = document.getElementById('modalLivroLabel');
    const btnAddLivro = document.getElementById('btn-add-livro');
    const pagination = document.getElementById('pagination');

    let livroEditando = null;
    let currentPage = 0;

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
            if (!res.ok) {
                let errorMsg = 'Erro ao encerrar conta';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    }
                } catch (e) {}
                throw new Error(errorMsg);
            }
            showModalAlert('Conta encerrada com sucesso!', 'success');
            setTimeout(() => { window.location.href = '/'; }, 1500);
        } catch (err) {
            showModalAlert(err.message);
        }
    });

    const showAlert = (message, type = 'danger') => {
        alertArea.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;

        const alertElement = alertArea.querySelector('.alert');

        // Animação bootstrap para remover alerta
        if (alertElement) {
            setTimeout(() => {
                alertElement.classList.remove('show');

                setTimeout(() => {
                    alertArea.innerHTML = '';
                }, 150);
            }, 5000);
        }
    };

    const showModalAlert = (message, type = 'danger') => {
        const alertDiv = document.getElementById('modal-livro-alert');
        if (alertDiv) {
            alertDiv.innerHTML = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>`;
            setTimeout(() => {
                if (alertDiv.querySelector('.alert')) {
                    alertDiv.querySelector('.alert').classList.remove('show');
                    setTimeout(() => { alertDiv.innerHTML = ''; }, 150);
                }
            }, 5000);
        }
    };

    // Máscara para ISBN
    const aplicarMascaraISBN = (input) => {
        let value = input.value.replace(/\D/g, '');

        // Limita a 13 dígitos
        value = value.substring(0, 13);

        // Aplica a máscara no formato 111-1-11-111111-1
        value = value.replace(/^(\d{3})(\d)/, '$1-$2');
        value = value.replace(/^(\d{3}-\d)(\d{2,5})/, '$1-$2');
        value = value.replace(/^(\d{3}-\d{1,5}-\d{2,5})(\d{1,5})/, '$1-$2');
        value = value.replace(/^(\d{3}-\d{1,5}-\d{1,5}-\d{1,5})(\d)$/, '$1-$2');

        input.value = value;
    };

    // Limpar dados ao clicar para adicionar livro
    btnAddLivro?.addEventListener('click', () => {
        livroEditando = null;
        modalLabel.textContent = 'Adicionar Livro';
        formLivro.reset();
        document.getElementById('isbn').value = '';
    });

    const carregarLivros = async () => {
        try {
            const res = await fetch(`/book/get-all?page=${currentPage}`, { credentials: 'include' });
            if (!res.ok) {
                let errorMsg = 'Erro ao carregar livros';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    }
                } catch (e) {}
                throw new Error(errorMsg);
            }
            const data = await res.json();
            exibirLivros(data.content);
            renderPagination(data.total, data.page, data.size);
        } catch (e) {
            showAlert(e.message);
        }
    };

    const exibirLivros = (livros) => {
        tabelaLivros.innerHTML = '';
        if (livros.length === 0) {
            tabelaLivros.innerHTML = `<tr><td colspan="6" class="text-center">Nenhum livro cadastrado.</td></tr>`;
            return;
        }

        livros.forEach(livro => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${livro.isbn}</td>
                <td>${livro.title}</td>
                <td>${livro.author}</td>
                <td>${livro.quantity}</td>
                <td>${livro.availableQuantity}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" data-edit="${livro.isbn}">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" data-delete="${livro.isbn}">
                        <i class="bi bi-trash"></i> Excluir
                    </button>
                </td>`;
            tabelaLivros.appendChild(row);
        });

        document.querySelectorAll('[data-edit]').forEach(btn => {
            btn.addEventListener('click', () => editarLivro(btn.dataset.edit));
        });

        document.querySelectorAll('[data-delete]').forEach(btn => {
            btn.addEventListener('click', () => excluirLivro(btn.dataset.delete));
        });
    };

    const renderPagination = (total, page, size) => {
        const totalPages = Math.ceil(total / size);
        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let html = `
            <li class="page-item ${page === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${page - 1})">Anterior</a>
            </li>`;

        for (let i = 0; i < totalPages; i++) {
            if (i === 0 || i === totalPages - 1 || (i >= page - 2 && i <= page + 2)) {
                html += `
                    <li class="page-item ${i === page ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
                    </li>`;
            } else if (i === page - 3 || i === page + 3) {
                html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        html += `
            <li class="page-item ${page === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${page + 1})">Próximo</a>
            </li>`;

        pagination.innerHTML = html;
    };

    window.changePage = (page) => {
        currentPage = page;
        carregarLivros();
    };

    // Formulário de adicionar/editar livro
    const editarLivro = async (isbn) => {
        try {
            const res = await fetch(`/book/get/${isbn}`, { credentials: 'include' });
            if (!res.ok) {
                let errorMsg = 'Erro ao carregar dados do livro';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    }
                } catch (e) {}
                throw new Error(errorMsg);
            }
            const livro = await res.json();

            const isbnInput = document.getElementById('isbn');
            isbnInput.value = livro.isbn;
            aplicarMascaraISBN(isbnInput);

            document.getElementById('titulo').value = livro.title;
            document.getElementById('autor').value = livro.author;
            document.getElementById('quantidade').value = livro.quantity;

            livroEditando = isbn;
            modalLabel.textContent = 'Editar Livro';
            new bootstrap.Modal(modalLivro).show();
        } catch (error) {
            showAlert('Erro ao carregar dados do livro');
        }
    };

    const excluirLivro = async (isbn) => {
        if (!confirm('Tem certeza que deseja excluir este livro?')) return;

        try {
            const res = await fetch(`/book/delete/${isbn}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!res.ok) {
                let errorMsg = 'Erro ao excluir livro';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao excluir livro';
                }
                showAlert(errorMsg);
                return;
            }
            showAlert('Livro excluído com sucesso!', 'success');
            carregarLivros();
        } catch (error) {
            showAlert('Erro ao excluir livro: ' + error.message);
        }
    };

    formLivro?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const isbn = document.getElementById('isbn').value.replace(/\D/g, '');
        const title = document.getElementById('titulo').value.trim();
        const author = document.getElementById('autor').value.trim();
        const quantity = parseInt(document.getElementById('quantidade').value);

        if (isbn.length < 10) {
            showModalAlert('O ISBN deve ter pelo menos 13 dígitos.');
            return;
        }

        if (title.length < 2) {
            showModalAlert('O título deve ter pelo menos 2 caracteres.');
            return;
        }

        if (author.length < 2) {
            showModalAlert('O autor deve ter pelo menos 2 caracteres.');
            return;
        }

        if (!quantity || quantity < 1) {
            showModalAlert('A quantidade deve ser pelo menos 1.');
            return;
        }

        const btn = e.target.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.textContent = livroEditando ? 'Salvando...' : 'Adicionando...';
        btn.disabled = true;

        try {
            const method = livroEditando ? 'PUT' : 'POST';
            const url = livroEditando
                ? `/book/update/${livroEditando}`
                : `/book`;

            const res = await fetch(url, {
                method,
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ isbn, title, author, quantity })
            });

            if (!res.ok) {
                let errorMsg = 'Erro ao salvar livro';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao salvar livro';
                }
                showModalAlert(errorMsg);
                btn.textContent = originalText;
                btn.disabled = false;
                return;
            }

            showModalAlert(`Livro ${livroEditando ? 'atualizado' : 'adicionado'} com sucesso!`, 'success');
            setTimeout(() => {
                bootstrap.Modal.getInstance(modalLivro).hide();
                formLivro.reset();
                modalLabel.textContent = 'Adicionar Livro';
                livroEditando = null;
                carregarLivros();
            }, 1200);
        } catch (err) {
            showAlert('Erro ao salvar livro: ' + err.message);
        } finally {
            btn.textContent = originalText;
            btn.disabled = false;
        }
    });

    document.getElementById('isbn')?.addEventListener('input', function () {
        aplicarMascaraISBN(this);
    });

    carregarLivros();
});