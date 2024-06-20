package br.com.winepurchase.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaiorCompraDTO {
    private String nomeCliente;
    private String cpfCliente;
    private ProdutoDTO produto;
    private int quantidade;
    private double valorTotal;
}
