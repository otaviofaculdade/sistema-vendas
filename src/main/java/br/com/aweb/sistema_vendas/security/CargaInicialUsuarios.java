package br.com.aweb.sistema_vendas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.aweb.sistema_vendas.model.Usuario;
import br.com.aweb.sistema_vendas.model.UsuarioRole;
import br.com.aweb.sistema_vendas.service.UsuarioService;

// Cadastra os usuários de teste na primeira execução (banco vazio)
@Component
public class CargaInicialUsuarios implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Value("${app.usuarios.admin.senha:admin123}")
    private String senhaAdmin;

    @Value("${app.usuarios.padrao.senha:padrao123}")
    private String senhaPadrao;

    public CargaInicialUsuarios(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        criarSeNaoExistir("Administrador", "admin@sistemavendas.com", senhaAdmin, UsuarioRole.ADMIN);
        criarSeNaoExistir("Vendedor Padrão", "padrao@sistemavendas.com", senhaPadrao, UsuarioRole.PADRAO);
    }

    private void criarSeNaoExistir(String nome, String email, String senha, UsuarioRole role) {
        if (!usuarioService.existePorEmail(email)) {
            usuarioService.salvar(new Usuario(null, nome, email, senha, role));
        }
    }

}
