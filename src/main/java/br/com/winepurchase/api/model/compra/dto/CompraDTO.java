package br.com.winepurchase.api.model.compra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private Long codigo;
    private String nomeCliente;
    private String cpfCliente;
    private String tipoVinho;
    private double preco;
    private int quantidade;
    private double valorTotal;
    private int anoCompra;
}
