package br.com.winepurchase.api.model.domain;

import lombok.Data;

@Data
public class ProdutoDomain {
    private int codigo;
    private String tipoVinho;
    private double preco;
    private String safra;
    private int anoCompra;
}
