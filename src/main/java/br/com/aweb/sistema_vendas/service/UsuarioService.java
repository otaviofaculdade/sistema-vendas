package br.com.aweb.sistema_vendas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Usuario;
import br.com.aweb.sistema_vendas.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Chamado pelo Spring Security no POST /login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    // Recebe a senha em texto puro e grava somente o hash
    @Transactional
    public Usuario salvar(Usuario usuario) {
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email.trim()).isPresent();
    }

}
