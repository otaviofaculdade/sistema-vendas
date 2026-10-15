package br.com.aweb.sistema_vendas.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório.")
    @Column(nullable = false, length = 150)
    private String nomeCompleto;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "CPF é obrigatório.")
    @CPF(message = "CPF inválido.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório.")
    @Pattern(regexp = "\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}", message = "Telefone inválido. Use o formato (00) 00000-0000.")
    @Column(nullable = false, length = 11)
    private String telefone;

    @Valid
    @Embedded
    private Endereco endereco = new Endereco();

    // Somente os dígitos, para gravar no banco de forma padronizada
    public void normalizar() {
        if (cpf != null)
            cpf = cpf.replaceAll("\\D", "");
        if (telefone != null)
            telefone = telefone.replaceAll("\\D", "");
        if (email != null)
            email = email.trim().toLowerCase();
        if (endereco != null)
            endereco.normalizar();
    }

    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    public String getTelefoneFormatado() {
        if (telefone == null || telefone.length() < 10)
            return telefone;
        var ddd = telefone.substring(0, 2);
        var restante = telefone.substring(2);
        var meio = restante.length() == 9 ? restante.substring(0, 5) : restante.substring(0, 4);
        var fim = restante.length() == 9 ? restante.substring(5) : restante.substring(4);
        return "(" + ddd + ") " + meio + "-" + fim;
    }

}
