package br.com.aweb.sistema_vendas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.service.ClienteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    private static final List<String> UFS = List.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    @ModelAttribute("ufs")
    public List<String> ufs() {
        return UFS;
    }

    // Listar clientes
    @GetMapping
    public ModelAndView list() {
        return new ModelAndView("cliente/list", Map.of("clientes", clienteService.listarTodos()));
    }

    // Formulário de cadastro
    @GetMapping("/novo")
    public ModelAndView create() {
        return new ModelAndView("cliente/form", Map.of("cliente", new Cliente()));
    }

    // Salvar cliente
    @PostMapping("/novo")
    public String create(@Valid Cliente cliente, BindingResult result) {
        validarUnicidade(cliente, null, result);
        if (result.hasErrors()) {
            return "cliente/form";
        }
        clienteService.salvar(cliente);
        return "redirect:/clientes";
    }

    // Formulário de edição
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id) {
        var optionalCliente = clienteService.buscarPorId(id);
        if (optionalCliente.isPresent()) {
            return new ModelAndView("cliente/form", Map.of("cliente", optionalCliente.get()));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Atualizar cliente
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid Cliente cliente, BindingResult result) {
        if (clienteService.buscarPorId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        validarUnicidade(cliente, id, result);
        if (result.hasErrors()) {
            cliente.setId(id);
            return "cliente/form";
        }

        clienteService.atualizar(id, cliente);
        return "redirect:/clientes";
    }

    // Confirmação de exclusão
    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {
        var optionalCliente = clienteService.buscarPorId(id);
        if (optionalCliente.isPresent()) {
            return new ModelAndView("cliente/delete", Map.of("cliente", optionalCliente.get()));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Excluir cliente
    @PostMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id) {
        clienteService.excluir(id);
        return "redirect:/clientes";
    }

    // E-mail e CPF não podem se repetir entre clientes
    private void validarUnicidade(Cliente cliente, Long idAtual, BindingResult result) {
        if (!result.hasFieldErrors("email") && clienteService.emailEmUso(cliente.getEmail(), idAtual)) {
            result.rejectValue("email", "email.duplicado", "Já existe um cliente cadastrado com este e-mail.");
        }
        if (!result.hasFieldErrors("cpf") && clienteService.cpfEmUso(cliente.getCpf(), idAtual)) {
            result.rejectValue("cpf", "cpf.duplicado", "Já existe um cliente cadastrado com este CPF.");
        }
    }

}
