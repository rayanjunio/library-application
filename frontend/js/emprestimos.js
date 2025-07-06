// JS para gerenciamento de empréstimos
window.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    const userProfile = localStorage.getItem('userProfile');
    if (!token || userProfile !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }
    const alertArea = document.getElementById('alert-area');
    const tabelaEmprestimos = document.getElementById('tabelaEmprestimos');
    const btnLogout = document.getElementById('btn-logout');
    const formEmprestimo = document.getElementById('formEmprestimo');

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

    async function carregarEmprestimos() {
        try {
            const response = await fetch('http://localhost:8080/users', {
                headers: getAuthHeaders()
            });
            if (!response.ok) {
                throw new Error('Erro ao carregar empréstimos');
            }
            const usuarios = await response.json();
            
            // Juntar todos os empréstimos de todos os usuários
            let emprestimos = [];
            usuarios.forEach(usuario => {
                if (usuario.loans && usuario.loans.length > 0) {
                    usuario.loans.forEach(loan => {
                        emprestimos.push({
                            ...loan,
                            userEmail: usuario.email
                        });
                    });
                }
            });
            
            console.log('Empréstimos carregados:', emprestimos); // Debug
            exibirEmprestimos(emprestimos);
        } catch (error) {
            showAlert('Erro ao carregar empréstimos: ' + error.message);
        }
    }

    function isEmprestimoAtivo(emp) {
        // Verificar múltiplas possibilidades de como o campo pode vir
        return emp.isActive === true || emp.active === true || emp.isActive === 'true' || emp.active === 'true';
    }

    function exibirEmprestimos(emprestimos) {
        tabelaEmprestimos.innerHTML = '';
        if (emprestimos.length === 0) {
            tabelaEmprestimos.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        Nenhum empréstimo encontrado.
                    </td>
                </tr>
            `;
            return;
        }
        emprestimos.forEach(emp => {
            const isAtivo = isEmprestimoAtivo(emp);
            const row = document.createElement('tr');
            
            // Formatar data
            const formatDate = (dateString) => {
                if (!dateString) return '-';
                const date = new Date(dateString);
                return date.toLocaleDateString('pt-BR');
            };
            
            row.innerHTML = `
                <td>${emp.id}</td>
                <td>${emp.userEmail}</td>
                <td>
                    <strong>${emp.bookTitle || 'Livro não encontrado'}</strong>
                    ${emp.bookAuthor ? `<br><small class="text-muted">${emp.bookAuthor}</small>` : ''}
                    <br><small class="text-muted">ISBN: ${emp.bookIsbn}</small>
                </td>
                <td>${formatDate(emp.loanDate)}</td>
                <td>${formatDate(emp.expectedReturnDate)}</td>
                <td>
                    <span class="badge bg-${isAtivo ? 'success' : 'secondary'}">
                        ${isAtivo ? 'Ativo' : 'Finalizado'}
                    </span>
                </td>
                <td>
                    ${isAtivo ? `<button class="btn btn-sm btn-outline-success" onclick="finalizarEmprestimo(${emp.id})"><i class="bi bi-check2-circle"></i> Finalizar</button>` : '<span class="text-muted">-</span>'}
                </td>
            `;
            tabelaEmprestimos.appendChild(row);
        });
    }

    // Formulário de criar empréstimo
    if (formEmprestimo) {
        formEmprestimo.addEventListener('submit', async function(e) {
            e.preventDefault();
            const emailUsuario = document.getElementById('emailUsuario').value;
            const isbnLivro = document.getElementById('isbnLivro').value;
            const dataDevolucao = document.getElementById('dataDevolucao').value;

            try {
                const response = await fetch('http://localhost:8080/loan', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({
                        userEmail: emailUsuario,
                        bookIsbn: isbnLivro,
                        expectedReturnDate: dataDevolucao
                    })
                });

                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error);
                }

                showAlert('Empréstimo criado com sucesso!', 'success');
                carregarEmprestimos();
                // Fechar modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('modalEmprestimo'));
                modal.hide();
                formEmprestimo.reset();
            } catch (error) {
                showAlert('Erro ao criar empréstimo: ' + error.message);
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

    // Carregar empréstimos ao iniciar
    carregarEmprestimos();
});

// Função global para finalizar empréstimo
async function finalizarEmprestimo(id) {
    if (confirm('Tem certeza que deseja finalizar este empréstimo?')) {
        try {
            const response = await fetch(`http://localhost:8080/loan/${id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            if (!response.ok) {
                const error = await response.text();
                throw new Error(error);
            }
            
            // Mostrar mensagem de sucesso
            const alertArea = document.getElementById('alert-area');
            alertArea.innerHTML = `<div class="alert alert-success alert-dismissible fade show" role="alert">
                Empréstimo finalizado com sucesso!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>`;
            
            // Recarregar os empréstimos
            carregarEmprestimos();
        } catch (error) {
            const alertArea = document.getElementById('alert-area');
            alertArea.innerHTML = `<div class="alert alert-danger alert-dismissible fade show" role="alert">
                Erro ao finalizar empréstimo: ${error.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>`;
        }
    }
} 