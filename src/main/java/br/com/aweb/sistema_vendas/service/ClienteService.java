package br.com.aweb.sistema_vendas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    // CREATE
    @Transactional
    public Cliente salvar(Cliente cliente) {
        cliente.normalizar();
        return clienteRepository.save(cliente);
    }

    // READ
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // UPDATE
    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        var optionalCliente = buscarPorId(id);
        if (!optionalCliente.isPresent())
            throw new IllegalArgumentException("Cliente não encontrado.");

        clienteAtualizado.normalizar();
        var clienteExistente = optionalCliente.get();

        clienteExistente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(clienteExistente);
    }

    // DELETE
    @Transactional
    public void excluir(Long id) {
        var optionalCliente = buscarPorId(id);
        if (!optionalCliente.isPresent())
            throw new IllegalArgumentException("Cliente não encontrado.");

        clienteRepository.deleteById(id);
    }

    // Unicidade: ignora o próprio cliente quando é uma edição
    public boolean emailEmUso(String email, Long idAtual) {
        if (email == null || email.isBlank())
            return false;
        return clienteRepository.findByEmail(email.trim().toLowerCase())
                .filter(cliente -> !cliente.getId().equals(idAtual))
                .isPresent();
    }

    public boolean cpfEmUso(String cpf, Long idAtual) {
        if (cpf == null || cpf.isBlank())
            return false;
        return clienteRepository.findByCpf(cpf.replaceAll("\\D", ""))
                .filter(cliente -> !cliente.getId().equals(idAtual))
                .isPresent();
    }

}
