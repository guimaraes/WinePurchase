package br.com.winepurchase.api.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClienteDTO {
    private String nome;
    private String cpf;
    private List<CompraDTO> compras;
}