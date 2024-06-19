package br.com.winepurchase.api.model.compra.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDomain {
    private Long codigo;
    private String tipoVinho;
    private double preco;
    private String safra;
    private int anoCompra;
}
