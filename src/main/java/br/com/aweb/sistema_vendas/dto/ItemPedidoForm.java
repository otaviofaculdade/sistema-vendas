package br.com.aweb.sistema_vendas.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoForm {

    private Long produtoId;

    // Somente para exibição na tela
    private String nome;
    private BigDecimal preco;
    private Integer estoque;

    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    private Integer quantidade;

}
