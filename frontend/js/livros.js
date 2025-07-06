// JS para gerenciamento de livros
window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    if (!token || userProfile !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }
    const alertArea = document.getElementById('alert-area');
    const tabelaLivros = document.getElementById('tabelaLivros');
    const btnLogout = document.getElementById('btn-logout');
    const formLivro = document.getElementById('formLivro');
    const modalLivro = document.getElementById('modalLivro');

    let editandoLivro = false;
    let livroAtual = null;

    function showAlert(message, type = 'danger') {
        alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>`;
    }

    function clearAlert() {
        alertArea.innerHTML = '';
    }

    function getAuthHeaders() {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    }

    // Máscara para ISBN
    function aplicarMascaraISBN(input) {
        let value = input.value.replace(/\D/g, '');
        if (value.length <= 13) {
            value = value.replace(/(\d{3})(\d)/, '$1-$2');
            value = value.replace(/(\d{1})(\d{4})(\d)/, '$1-$2-$3');
            value = value.replace(/(\d{1})(\d{4})(\d)/, '$1-$2-$3');
        }
        input.value = value;
    }

    async function carregarLivros() {
        try {
            const response = await fetch('http://localhost:8080/api/books', {
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                throw new Error('Erro ao carregar livros');
            }
            const livros = await response.json();
            exibirLivros(livros);
        } catch (error) {
            showAlert('Erro ao carregar livros: ' + error.message);
        }
    }

    function exibirLivros(livros) {
        tabelaLivros.innerHTML = '';
        if (livros.length === 0) {
            tabelaLivros.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted">
                        Nenhum livro cadastrado.
                    </td>
                </tr>
            `;
            return;
        }
        
        livros.forEach(livro => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${livro.isbn}</td>
                <td>${livro.title}</td>
                <td>${livro.author}</td>
                <td>${livro.quantity}</td>
                <td>
                    <span class="badge bg-${livro.availableQuantity > 0 ? 'success' : 'danger'}">
                        ${livro.availableQuantity}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="editarLivro('${livro.isbn}')">
                        <i class="bi bi-pencil"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="excluirLivro('${livro.isbn}')">
                        <i class="bi bi-trash"></i> Excluir
                    </button>
                </td>
            `;
            tabelaLivros.appendChild(row);
        });
    }

    // Formulário de adicionar/editar livro
    if (formLivro) {
        // Aplicar máscara ao ISBN
        const isbnInput = document.getElementById('isbn');
        if (isbnInput) {
            isbnInput.addEventListener('input', function() {
                aplicarMascaraISBN(this);
            });
        }

        formLivro.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const isbn = document.getElementById('isbn').value.replace(/\D/g, '');
            const titulo = document.getElementById('titulo').value.trim();
            const autor = document.getElementById('autor').value.trim();
            const quantidade = document.getElementById('quantidade').value;

            // Validações
            if (isbn.length < 10) {
                showAlert('ISBN deve ter pelo menos 10 dígitos.');
                return;
            }

            if (titulo.length < 2) {
                showAlert('Título deve ter pelo menos 2 caracteres.');
                return;
            }

            if (autor.length < 2) {
                showAlert('Autor deve ter pelo menos 2 caracteres.');
                return;
            }

            if (quantidade < 1) {
                showAlert('Quantidade deve ser pelo menos 1.');
                return;
            }

            // Mostrar loading
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = editandoLivro ? 'Salvando...' : 'Adicionando...';
            submitBtn.disabled = true;

            try {
                const url = editandoLivro 
                    ? `http://localhost:8080/api/books/${livroAtual}`
                    : 'http://localhost:8080/api/books';
                
                const method = editandoLivro ? 'PUT' : 'POST';
                
                const response = await fetch(url, {
                    method: method,
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        isbn: isbn,
                        title: titulo,
                        author: autor,
                        quantity: parseInt(quantidade)
                    })
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error);
                }

                showAlert(
                    editandoLivro ? 'Livro atualizado com sucesso!' : 'Livro adicionado com sucesso!', 
                    'success'
                );
                carregarLivros();
                
                // Fechar modal
                const modal = bootstrap.Modal.getInstance(modalLivro);
                modal.hide();
                formLivro.reset();
                editandoLivro = false;
                livroAtual = null;
                
                // Atualizar título do modal
                document.getElementById('modalLivroLabel').textContent = 'Adicionar Livro';
                
            } catch (error) {
                showAlert('Erro ao ' + (editandoLivro ? 'atualizar' : 'adicionar') + ' livro: ' + error.message);
            } finally {
                // Restaurar botão
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        });
    }

    // Logout
    if (btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('token');
            window.location.href = 'index.html';
        });
    }

    // Carregar livros ao iniciar
    carregarLivros();
});

// Funções globais para editar e excluir
async function editarLivro(isbn) {
    try {
        const response = await fetch(`http://localhost:8080/api/books/${isbn}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        
        if (!response.ok) {
            throw new Error('Erro ao carregar dados do livro');
        }
        
        const livro = await response.json();
        
        // Preencher formulário
        document.getElementById('isbn').value = livro.isbn;
        document.getElementById('titulo').value = livro.title;
        document.getElementById('autor').value = livro.author;
        document.getElementById('quantidade').value = livro.quantity;
        
        // Configurar para edição
        window.editandoLivro = true;
        window.livroAtual = isbn;
        
        // Atualizar título do modal
        document.getElementById('modalLivroLabel').textContent = 'Editar Livro';
        
        // Abrir modal
        const modal = new bootstrap.Modal(document.getElementById('modalLivro'));
        modal.show();
        
    } catch (error) {
        alert('Erro ao carregar dados do livro: ' + error.message);
    }
}

async function excluirLivro(isbn) {
    if (confirm('Tem certeza que deseja excluir este livro?')) {
        try {
            const response = await fetch(`http://localhost:8080/api/books/${isbn}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Erro ao excluir livro');
            }
            
            alert('Livro excluído com sucesso!');
            location.reload(); // Recarregar para atualizar a lista
            
        } catch (error) {
            alert('Erro ao excluir livro: ' + error.message);
        }
    }
} 