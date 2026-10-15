package br.com.aweb.sistema_vendas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @NotBlank(message = "Logradouro é obrigatório.")
    @Column(nullable = false, length = 150)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório.")
    @Column(nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória.")
    @Column(nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "UF é obrigatória.")
    @Size(min = 2, max = 2, message = "A UF deve ter 2 caracteres.")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotBlank(message = "CEP é obrigatório.")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Use o formato 00000-000.")
    @Column(nullable = false, length = 8)
    private String cep;

    // Somente os dígitos, para gravar no banco de forma padronizada
    public void normalizar() {
        if (cep != null)
            cep = cep.replaceAll("\\D", "");
        if (uf != null)
            uf = uf.toUpperCase();
    }

    public String getCepFormatado() {
        if (cep == null || cep.length() != 8)
            return cep;
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    // Endereço completo em uma única linha, usado na listagem
    public String getEnderecoCompleto() {
        var sb = new StringBuilder(logradouro);
        if (numero != null && !numero.isBlank())
            sb.append(", ").append(numero);
        if (complemento != null && !complemento.isBlank())
            sb.append(" - ").append(complemento);
        sb.append(" - ").append(bairro);
        sb.append(" - ").append(cidade).append("/").append(uf);
        sb.append(" - CEP ").append(getCepFormatado());
        return sb.toString();
    }

}
