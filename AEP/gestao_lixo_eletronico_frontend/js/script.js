document.addEventListener('DOMContentLoaded', function() {
    const btnMostrarRegistro = document.getElementById('btnMostrarRegistro');
    const btnMostrarLogin = document.getElementById('btnMostrarLogin');
    const loginContainer = document.getElementById('loginContainer');
    const formLogin = document.getElementById('formLogin');
    const formRegistro = document.getElementById('formRegistro');
    const notificacao = document.getElementById('notificacao');

    function mostrarNotificacao(mensagem, isErro = false) {
        notificacao.textContent = mensagem;
        notificacao.style.display = 'block';
        notificacao.className = isErro ? 'notification-message error' : 'notification-message';
        
        setTimeout(() => {
            notificacao.style.display = 'none';
        }, 5000);
    }

    btnMostrarRegistro.addEventListener('click', (e) => {
        e.preventDefault();
        loginContainer.classList.add("right-panel-active");
    });

    btnMostrarLogin.addEventListener('click', (e) => {
        e.preventDefault();
        loginContainer.classList.remove("right-panel-active");
    });

    formLogin.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const email = document.getElementById('inputEmailLogin').value;
        const senha = document.getElementById('inputSenhaLogin').value;
        
        try {
            const response = await fetch('http://localhost:8080/usuarios/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, senha })
            });
            
            if (response.ok) {
                const usuario = await response.json();
                mostrarNotificacao(`Login bem-sucedido! Bem-vindo, ${usuario.nome}`);
                
                localStorage.setItem('usuario', JSON.stringify(usuario));
                
                if (usuario.tipoUsuario === 'ADMIN') {
                    window.location.href = 'admin.html';
                } else {
                    window.location.href = 'dashboard.html';
                }
            } else {
                const error = await response.text();
                mostrarNotificacao(error || 'Email ou senha incorretos', true);
            }
        } catch (error) {
            mostrarNotificacao('Erro ao conectar com o servidor', true);
            console.error('Erro:', error);
        }
    });

    formRegistro.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const nome = document.getElementById('inputNomeRegistro').value;
        const email = document.getElementById('inputEmailRegistro').value;
        const senha = document.getElementById('inputSenhaRegistro').value;
        
        try {
            const response = await fetch('http://localhost:8080/usuarios', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ nome, email, senha })
            });
            
            if (response.ok) {
                const novoUsuario = await response.json();
                mostrarNotificacao(`Cadastro bem-sucedido! Bem-vindo, ${novoUsuario.nome}`);
                loginContainer.classList.remove("right-panel-active");
                formRegistro.reset();
            } else {
                const error = await response.json();
                mostrarNotificacao(error.message || 'Erro ao cadastrar', true);
            }
        } catch (error) {
            mostrarNotificacao('Erro ao conectar com o servidor', true);
            console.error('Erro:', error);
        }
    });
});