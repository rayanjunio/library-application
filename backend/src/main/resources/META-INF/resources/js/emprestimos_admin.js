// JS para gerenciamento de empréstimos
window.addEventListener('DOMContentLoaded', () => {
    const alertArea = document.getElementById('alert-area');
    const tabelaEmprestimos = document.getElementById('tabelaEmprestimos');
    const btnLogout = document.getElementById('btn-logout');
    const formEmprestimo = document.getElementById('formEmprestimo');

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
            showModalEmprestimoAlert('Conta encerrada com sucesso!', 'success');
            setTimeout(() => { window.location.href = '/'; }, 1500);
        } catch (err) {
            showModalEmprestimoAlert('Erro ao encerrar conta: ' + err.message);
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

    function showModalEmprestimoAlert(message, type = 'danger') {
        const alertDiv = document.getElementById('modal-emprestimo-alert');
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
    }

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    };

    const isEmprestimoAtivo = (emp) => {
        return emp.active === true;
    };

    const exibirEmprestimos = (emprestimos = []) => {
        tabelaEmprestimos.innerHTML = '';

        if (!emprestimos.length) {
            tabelaEmprestimos.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        Nenhum empréstimo encontrado.
                    </td>
                </tr>`;
            return;
        }

        emprestimos.forEach(emp => {
            const ativo = isEmprestimoAtivo(emp);
            const row = document.createElement('tr');

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
                    <span class="badge bg-${ativo ? 'success' : 'secondary'}">
                        ${ativo ? 'Ativo' : 'Finalizado'}
                    </span>
                </td>
                <td>
                    ${ativo
                ? `<button class="btn btn-sm btn-outline-success" data-finalizar-id="${emp.id}">
                            <i class="bi bi-check2-circle"></i> Finalizar
                        </button>`
                : '<span class="text-muted">-</span>'}
                </td>
            `;
            tabelaEmprestimos.appendChild(row);
        });

        document.querySelectorAll('[data-finalizar-id]').forEach(button => {
            button.addEventListener('click', () => finalizarEmprestimo(button.dataset.finalizarId));
        });
    };

    const carregarEmprestimos = async () => {
        try {
            const res = await fetch('/loan/get-all', {credentials: 'include'});
            if (!res.ok) throw new Error('Erro ao carregar empréstimos');

            const data = await res.json();
            const loans = data.content || [];
            exibirEmprestimos(loans);
        } catch {
            showAlert('Erro ao carregar empréstimos');
        }
    };

    const finalizarEmprestimo = async (id) => {
        if (!confirm('Tem certeza que deseja finalizar este empréstimo?')) return;

        try {
            const res = await fetch(`/loan/finish/${id}`, {
                method: 'PUT',
                credentials: 'include'
            });

            if (!res.ok) {
                let errorMsg = 'Erro ao finalizar empréstimo';
                try {
                    const errorText = await res.text();
                    const errorJson = JSON.parse(errorText);
                    if (errorJson && errorJson.message) {
                        errorMsg = errorJson.message;
                    } else {
                        errorMsg = errorText;
                    }
                } catch (e) {
                    errorMsg = 'Erro ao finalizar empréstimo';
                }
                showAlert(errorMsg);
                return;
            }

            showAlert('Empréstimo finalizado com sucesso!', 'success');
            await carregarEmprestimos();
        } catch (err) {
            showAlert(`Erro ao finalizar empréstimo: ${err.message}`);
        }
    };

    if (formEmprestimo) {
        formEmprestimo.addEventListener('submit', async (e) => {
            e.preventDefault();

            const body = {
                userEmail: document.getElementById('emailUsuario')?.value,
                bookIsbn: document.getElementById('isbnLivro')?.value,
                expectedReturnDate: document.getElementById('dataDevolucao')?.value
            };

            try {
                const res = await fetch('/loan/create', {
                    method: 'POST',
                    credentials: 'include',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(body)
                });

                if (!res.ok) {
                    let errorMsg = 'Erro ao criar empréstimo';
                    try {
                        const errorText = await res.text();
                        const errorJson = JSON.parse(errorText);
                        if (errorJson && errorJson.message) {
                            errorMsg = errorJson.message;
                        } else {
                            errorMsg = errorText;
                        }
                    } catch (e) {
                        errorMsg = 'Erro ao criar empréstimo';
                    }
                    showModalEmprestimoAlert(errorMsg);
                    return;
                }

                showModalEmprestimoAlert('Empréstimo criado com sucesso!', 'success');
                setTimeout(() => {
                    formEmprestimo.reset();
                    const modal = bootstrap.Modal.getInstance(document.getElementById('modalEmprestimo'));
                    modal?.hide();
                    carregarEmprestimos();
                }, 1200);
            } catch (err) {
                showModalEmprestimoAlert('Erro ao criar empréstimo: ' + err.message);
            }
        });
    }
    carregarEmprestimos();
});