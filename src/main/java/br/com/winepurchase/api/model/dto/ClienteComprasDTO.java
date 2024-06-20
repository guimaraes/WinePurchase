package br.com.winepurchase.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClienteComprasDTO {
    private String nome;
    private String cpf;
    private List<CompraDetalhadaDTO> compras;
    private double totalCompras;
}
