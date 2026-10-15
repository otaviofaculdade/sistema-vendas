package br.com.aweb.sistema_vendas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    // O POST /login é processado pelo Spring Security
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de confirmação; o formulário dela faz o POST /logout tratado pelo Spring
    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    // Qualquer método: o Spring encaminha para cá a própria requisição bloqueada (GET ou POST)
    @RequestMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }

}
