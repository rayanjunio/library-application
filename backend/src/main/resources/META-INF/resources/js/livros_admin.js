// JS para gerenciamento de livros
window.addEventListener('DOMContentLoaded', () => {
    const alertArea = document.getElementById('alert-area');
    const tabelaLivros = document.getElementById('tabelaLivros');
    const btnLogout = document.getElementById('btn-logout');
    const formLivro = document.getElementById('formLivro');
    const modalLivro = document.getElementById('modalLivro');
    const modalLabel = document.getElementById('modalLivroLabel');
    const btnAddLivro = document.getElementById('btn-add-livro');

    let livroEditando = null;

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
            const res = await fetch('/book/available-books', { credentials: 'include' });
            if (!res.ok) throw new Error('Erro ao buscar livros');
            const livros = await res.json();
            exibirLivros(livros);
        } catch (e) {
            showAlert('Erro ao carregar livros disponíveis');
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

    // Formulário de adicionar/editar livro
    const editarLivro = async (isbn) => {
        try {
            const res = await fetch(`/book/get/${isbn}`, { credentials: 'include' });
            if (!res.ok) throw new Error();
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
            if (!res.ok) throw new Error(await res.text());
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
            showAlert('O ISBN deve ter pelo menos 13 dígitos.');
            return;
        }

        if (title.length < 2) {
            showAlert('O título deve ter pelo menos 2 caracteres.');
            return;
        }

        if (author.length < 2) {
            showAlert('O autor deve ter pelo menos 2 caracteres.');
            return;
        }

        if (!quantity || quantity < 1) {
            showAlert('A quantidade deve ser pelo menos 1.');
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

            if (!res.ok) throw new Error(await res.text());

            showAlert(`Livro ${livroEditando ? 'atualizado' : 'adicionado'} com sucesso!`, 'success');
            bootstrap.Modal.getInstance(modalLivro).hide();
            formLivro.reset();
            modalLabel.textContent = 'Adicionar Livro';
            livroEditando = null;
            carregarLivros();
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