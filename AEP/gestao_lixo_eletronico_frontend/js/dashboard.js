document.addEventListener('DOMContentLoaded', function() {
    const pontosContainer = document.getElementById('pontosContainer');
    const btnLogout = document.getElementById('btnLogout');
    const userName = document.getElementById('userName');
    
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    if (!usuario) {
        window.location.href = 'index.html';
        return;
    }
    
    userName.textContent = `Olá, ${usuario.nome}`;
    
    async function carregarPontosColeta() {
        try {
            pontosContainer.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Carregando pontos de coleta...</div>';
            
            const response = await fetch('http://localhost:8080/pontos-coleta');
            if (!response.ok) throw new Error('Erro ao carregar pontos de coleta');
            
            const pontos = await response.json();
            exibirPontosColeta(pontos);
            
        } catch (error) {
            pontosContainer.innerHTML = `<div class="error-message"><i class="fas fa-exclamation-circle"></i> ${error.message}</div>`;
            console.error('Erro:', error);
        }
    }

    function exibirPontosColeta(pontos) {
        if (pontos.length === 0) {
            pontosContainer.innerHTML = '<div class="error-message"><i class="fas fa-info-circle"></i> Nenhum ponto de coleta encontrado.</div>';
            return;
        }
        
        pontosContainer.innerHTML = '';
        
        pontos.forEach(ponto => {
            const pontoElement = document.createElement('div');
            pontoElement.className = 'ponto-card';
            pontoElement.innerHTML = `
                <h3 class="ponto-nome">${ponto.nome}</h3>
                <p class="ponto-endereco">
                    <i class="fas fa-map-marker-alt"></i> 
                    ${ponto.endereco}, ${ponto.numero} - ${ponto.bairro}, ${ponto.cidade}/${ponto.estado}
                </p>
                <p class="ponto-horario">
                    <i class="far fa-clock"></i> 
                    ${ponto.diasFuncionamento}: ${ponto.horarioAbertura} - ${ponto.horarioFechamento}
                </p>
            `;
            
            pontosContainer.appendChild(pontoElement);
        });
    }

    btnLogout.addEventListener('click', function() {
        btnLogout.innerHTML = '<i class="fas fa-sign-out-alt"></i>';
        localStorage.removeItem('usuario');
        window.location.href = 'index.html';
    });

    carregarPontosColeta();
});