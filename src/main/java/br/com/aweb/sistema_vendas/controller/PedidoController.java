package br.com.aweb.sistema_vendas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_vendas.dto.PedidoForm;
import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.service.ClienteService;
import br.com.aweb.sistema_vendas.service.PedidoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @ModelAttribute("clientes")
    public List<Cliente> clientes() {
        return clienteService.listarTodos();
    }

    // Listar pedidos
    @GetMapping
    public ModelAndView list() {
        return new ModelAndView("pedido/list", Map.of("pedidos", pedidoService.listarTodos()));
    }

    // Formulário de novo pedido
    @GetMapping("/novo")
    public ModelAndView create() {
        return new ModelAndView("pedido/form", Map.of("pedidoForm", pedidoService.novoFormulario()));
    }

    // Realizar pedido
    @PostMapping("/novo")
    public String create(@Valid PedidoForm pedidoForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "pedido/form";
        }
        try {
            var pedido = pedidoService.realizar(pedidoForm);
            return "redirect:/pedidos/" + pedido.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "pedido/form";
        }
    }

    // Detalhes do pedido
    @GetMapping("/{id}")
    public ModelAndView show(@PathVariable Long id) {
        var optionalPedido = pedidoService.buscarPorId(id);
        if (optionalPedido.isPresent()) {
            return new ModelAndView("pedido/show", Map.of("pedido", optionalPedido.get()));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    // Cancelar pedido (devolve os itens ao estoque)
    @PostMapping("/cancelar/{id}")
    public String cancel(@PathVariable Long id) {
        if (pedidoService.buscarPorId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        pedidoService.cancelar(id);
        return "redirect:/pedidos/" + id;
    }

}
