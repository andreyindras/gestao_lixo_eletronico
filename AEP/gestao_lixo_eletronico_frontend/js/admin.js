document.addEventListener('DOMContentLoaded', async () => {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    if (!usuario || usuario.tipoUsuario !== 'ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    document.getElementById('adminName').textContent = usuario.nome;
    document.querySelector('.avatar').textContent = usuario.nome.charAt(0).toUpperCase();

    const formPonto = document.getElementById('pontoForm');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnLogout = document.getElementById('btnLogout');
    const tabela = document.getElementById('tabelaPontos').getElementsByTagName('tbody')[0];

    formPonto.addEventListener('submit', salvarPonto);
    btnCancelar.addEventListener('click', resetForm);
    btnLogout.addEventListener('click', logout);

    await carregarPontos();

    async function carregarPontos() {
        try {
            const response = await fetch('http://localhost:8080/pontos-coleta');
            
            if (!response.ok) {
                throw new Error('Erro ao carregar pontos');
            }
            
            const pontos = await response.json();
            renderizarTabela(pontos);
        } catch (error) {
            console.error('Erro ao carregar pontos:', error);
            mostrarNotificacao('Erro ao carregar pontos. Tente recarregar a página.', true);
        }
    }

    function renderizarTabela(pontos) {
        tabela.innerHTML = '';
        
        if (pontos.length === 0) {
            tabela.innerHTML = '<tr><td colspan="4" class="no-data">Nenhum ponto cadastrado</td></tr>';
            return;
        }
        
        pontos.forEach(ponto => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${ponto.nome}</td>
                <td>${ponto.endereco}, ${ponto.numero || 'S/N'} - ${ponto.bairro}</td>
                <td>${ponto.diasFuncionamento} ${ponto.horarioAbertura}-${ponto.horarioFechamento}</td>
                <td>
                    <button class="action-btn" onclick="editarPonto('${ponto.id}')">Editar</button>
                    <button class="action-btn delete" onclick="excluirPonto('${ponto.id}')">Excluir</button>
                </td>
            `;
            tabela.appendChild(tr);
        });
    }

    async function salvarPonto(e) {
        e.preventDefault();
        
        const id = document.getElementById('pontoId').value;
        const ponto = {
            nome: document.getElementById('pontoNome').value,
            endereco: document.getElementById('pontoEndereco').value,
            numero: document.getElementById('pontoNumero').value,
            bairro: document.getElementById('pontoBairro').value,
            cidade: document.getElementById('pontoCidade').value,
            estado: document.getElementById('pontoEstado').value,
            diasFuncionamento: document.getElementById('pontoDias').value,
            horarioAbertura: document.getElementById('pontoAbertura').value,
            horarioFechamento: document.getElementById('pontoFechamento').value
        };
        
        try {
            const url = id ? `http://localhost:8080/pontos-coleta/${id}?usuarioId=${usuario.id}` 
                           : 'http://localhost:8080/pontos-coleta';
            const method = id ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: id ? JSON.stringify(ponto) : JSON.stringify({...ponto, usuarioId: usuario.id})
            });
    
            if (!response.ok) {
                throw new Error('Erro ao salvar ponto');
            }
    
            mostrarNotificacao(id ? 'Ponto atualizado!' : 'Ponto cadastrado!');
            await carregarPontos();
            resetForm();
            
        } catch (error) {
            mostrarNotificacao(error.message || 'Erro na requisição', true);
        }
    }
    
    function resetForm() {
        formPonto.reset();
        document.getElementById('pontoId').value = '';
        document.getElementById('formTitle').textContent = 'Adicionar Novo Ponto';
        document.getElementById('btnSalvar').textContent = 'Salvar';
        btnCancelar.style.display = 'none';
    }

    function logout() {
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    }

    function mostrarNotificacao(mensagem, isErro = false) {
        const notificacao = document.createElement('div');
        notificacao.className = `notification ${isErro ? 'error' : 'success'}`;
        notificacao.textContent = mensagem;
        document.body.appendChild(notificacao);
        
        setTimeout(() => {
            notificacao.classList.add('fade-out');
            setTimeout(() => notificacao.remove(), 300);
        }, 3000);
    }

    window.editarPonto = async function(id) {
        try {
            const response = await fetch(`http://localhost:8080/pontos-coleta/${id}`);
            
            if (!response.ok) throw new Error('Erro ao carregar ponto');
            
            const ponto = await response.json();
            
            document.getElementById('pontoId').value = ponto.id;
            document.getElementById('pontoNome').value = ponto.nome;
            document.getElementById('pontoEndereco').value = ponto.endereco;
            document.getElementById('pontoNumero').value = ponto.numero || '';
            document.getElementById('pontoBairro').value = ponto.bairro;
            document.getElementById('pontoCidade').value = ponto.cidade;
            document.getElementById('pontoEstado').value = ponto.estado;
            document.getElementById('pontoDias').value = ponto.diasFuncionamento;
            document.getElementById('pontoAbertura').value = ponto.horarioAbertura;
            document.getElementById('pontoFechamento').value = ponto.horarioFechamento;
            
            document.getElementById('formTitle').textContent = 'Editar Ponto';
            document.getElementById('btnSalvar').textContent = 'Atualizar';
            btnCancelar.style.display = 'inline-block';
            
            document.getElementById('formContainer').scrollIntoView({ behavior: 'smooth' });
        } catch (error) {
            mostrarNotificacao(error.message, true);
        }
    };

    window.excluirPonto = function(id) {
        const modal = document.getElementById('confirmModal');
        modal.style.display = 'flex';
        
        document.getElementById('confirmDelete').onclick = null;
        document.getElementById('confirmCancel').onclick = null;
        
        document.getElementById('confirmDelete').onclick = async function() {
            modal.style.display = 'none';
            
            try {
                const response = await fetch(`http://localhost:8080/pontos-coleta/${id}?usuarioId=${usuario.id}`, {
                    method: 'DELETE'
                });
                
                if (!response.ok) {
                    throw new Error('Erro ao excluir ponto');
                }
                
                mostrarNotificacao('Ponto excluído com sucesso!');
                await carregarPontos();
            } catch (error) {
                mostrarNotificacao(error.message, true);
            }
        };
        
        document.getElementById('confirmCancel').onclick = function() {
            modal.style.display = 'none';
        };
    };
});