package br.com.winepurchase.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraDetalhadaDTO {
    private ProdutoDTO produto;
    private int quantidade;
    private double valorTotal;

    public double getValorTotal() {
        return Math.round(valorTotal * 100.0) / 100.0;
    }
}
