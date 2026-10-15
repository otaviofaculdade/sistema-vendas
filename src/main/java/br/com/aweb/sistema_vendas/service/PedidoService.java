package br.com.aweb.sistema_vendas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.dto.ItemPedidoForm;
import br.com.aweb.sistema_vendas.dto.PedidoForm;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.repository.ClienteRepository;
import br.com.aweb.sistema_vendas.repository.PedidoRepository;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Uma linha por produto cadastrado, com quantidade zerada
    public PedidoForm novoFormulario() {
        var form = new PedidoForm();
        for (var produto : produtoRepository.findAll()) {
            form.getItens().add(new ItemPedidoForm(produto.getId(), produto.getNome(),
                    produto.getPreco(), produto.getQuantidadeEmEstoque(), 0));
        }
        return form;
    }

    // CREATE: grava o pedido e baixa o estoque dos produtos
    @Transactional
    public Pedido realizar(PedidoForm form) {
        var cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        var pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(StatusPedido.REALIZADO);

        for (var item : form.getItens()) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0)
                continue;

            var produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            if (produto.getQuantidadeEmEstoque() < item.getQuantidade())
                throw new IllegalArgumentException("Estoque insuficiente para o produto " + produto.getNome() + ".");

            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - item.getQuantidade());
            pedido.adicionarItem(produto, item.getQuantidade());
        }

        if (pedido.getItens().isEmpty())
            throw new IllegalArgumentException("Informe a quantidade de pelo menos um produto.");

        return pedidoRepository.save(pedido);
    }

    // READ
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByDataHoraDesc();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // UPDATE: cancela o pedido e devolve os itens ao estoque
    @Transactional
    public void cancelar(Long id) {
        var pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if (pedido.isCancelado())
            return;

        for (var item : pedido.getItens()) {
            var produto = item.getProduto();
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + item.getQuantidade());
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    public boolean clientePossuiPedidos(Long clienteId) {
        return pedidoRepository.existsByClienteId(clienteId);
    }

    public boolean produtoPossuiPedidos(Long produtoId) {
        return pedidoRepository.existsByItensProdutoId(produtoId);
    }

}
