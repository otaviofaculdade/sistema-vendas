package br.com.aweb.sistema_vendas.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Dados do formulário de novo pedido: o cliente e uma linha por produto
@Data
public class PedidoForm {

    @NotNull(message = "Selecione o cliente.")
    private Long clienteId;

    @Valid
    private List<ItemPedidoForm> itens = new ArrayList<>();

}
