package br.com.winepurchase.api.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDomain {
    private int codigo;
    private String tipoVinho;
    private double preco;
    private String safra;
    private int anoCompra;
}
