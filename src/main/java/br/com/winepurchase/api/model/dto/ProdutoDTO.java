package br.com.winepurchase.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDTO {
    private int codigo;

    @JsonProperty("tipo_vinho")
    private String tipoVinho;

    private double preco;
    private String safra;

    @JsonProperty("ano_compra")
    private int anoCompra;
}
