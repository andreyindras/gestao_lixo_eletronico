package com.gestaolixoeletronico;

import com.gestaolixoeletronico.entities.Usuario;
import com.gestaolixoeletronico.enums.TipoUsuario;
import com.gestaolixoeletronico.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GestaoLixoEletronicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoLixoEletronicoApplication.class, args);
	}

	@Bean
	public CommandLineRunner inicializacao(UsuarioService usuarioService) {
		return args -> {
			if (usuarioService.buscarPorEmail("admin@eco.com").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setNome("Administrador");
				admin.setEmail("admin@eco.com");
				admin.setSenha("123");
				admin.setTipoUsuario(TipoUsuario.ADMIN);

				usuarioService.salvarUsuario(admin);
				System.out.println("Usuário admin criado com sucesso!");
			}
		};
	}


}
