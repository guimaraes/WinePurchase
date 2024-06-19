package br.com.winepurchase.api.mapper.compra;

import br.com.winepurchase.api.model.compra.domain.ProdutoDomain;
import br.com.winepurchase.api.model.compra.dto.ProdutoDTO;
import br.com.winepurchase.api.model.compra.dto.CompraDTO;
import br.com.winepurchase.api.model.compra.request.ProdutoRequest;
import br.com.winepurchase.api.model.compra.dto.ClienteDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CompraMapper {

    public static ProdutoDomain toDomain(ProdutoDTO dto) {
        return ProdutoDomain.builder()
                .codigo(dto.getCodigo())
                .tipoVinho(dto.getTipoVinho())
                .preco(dto.getPreco())
                .safra(dto.getSafra())
                .anoCompra(dto.getAnoCompra())
                .build();
    }

    public static ProdutoDTO toDTO(ProdutoDomain domain) {
        return ProdutoDTO.builder()
                .codigo(domain.getCodigo())
                .tipoVinho(domain.getTipoVinho())
                .preco(domain.getPreco())
                .safra(domain.getSafra())
                .anoCompra(domain.getAnoCompra())
                .build();
    }

    public static ProdutoDomain requestToDomain(ProdutoRequest request) {
        return ProdutoDomain.builder()
                .tipoVinho(request.getTipoVinho())
                .preco(request.getPreco())
                .safra(request.getSafra())
                .anoCompra(request.getAnoCompra())
                .build();
    }

    public static CompraDTO toCompraDTO(Long codigo, String nomeCliente, String cpfCliente, ProdutoDomain produto, int quantidade) {
        double valorTotal = produto.getPreco() * quantidade;

        return CompraDTO.builder()
                .codigo(codigo)
                .nomeCliente(nomeCliente)
                .cpfCliente(cpfCliente)
                .tipoVinho(produto.getTipoVinho())
                .preco(produto.getPreco())
                .quantidade(quantidade)
                .valorTotal(valorTotal)
                .build();
    }

    public static List<CompraDTO> toCompraDTOList(List<ProdutoDomain> produtos, ClienteDTO cliente) {
        return cliente.getCompras().stream()
                .map(compra -> {
                    ProdutoDomain produto = produtos.stream()
                            .filter(p -> p.getCodigo().equals(compra.getCodigo()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + compra.getCodigo()));
                    return toCompraDTO(compra.getCodigo(), cliente.getNome(), cliente.getCpf(), produto, compra.getQuantidade());
                })
                .collect(Collectors.toList());
    }
}
