package br.com.winepurchase.api.service;

import br.com.winepurchase.api.config.ResourceNotFoundException;
import br.com.winepurchase.api.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {

    private final RestTemplate restTemplate;

    @Value("${app.urls.clientes}")
    private String urlClientes;

    @Value("${app.urls.produtos}")
    private String urlProdutos;

    public CompraService() {
        this.restTemplate = null;
    }

    public CompraService(RestTemplate restTemplate, String urlClientes, String urlProdutos) {
        this.restTemplate = restTemplate;
        this.urlClientes = urlClientes;
        this.urlProdutos = urlProdutos;
    }

    public List<ClienteDTO> obterClientes() {
        log.info("Buscando clientes do URL: {}", urlClientes);
        ClienteDTO[] clientes = restTemplate.getForObject(urlClientes, ClienteDTO[].class);
        log.info("Produtos {} cliente", clientes != null ? clientes.length : 0);
        return Arrays.asList(clientes);
    }

    public List<ProdutoDTO> obterProdutos() {
        log.info("Buscando produtos do URL: {}", urlProdutos);
        ProdutoDTO[] produtos = restTemplate.getForObject(urlProdutos, ProdutoDTO[].class);
        log.info("Produtos {} buscados", produtos != null ? produtos.length : 0);
        return Arrays.asList(produtos);
    }

    public List<ClienteComprasDTO> listarComprasAgrupadas() {
        List<ClienteDTO> clientes = obterClientes();
        List<ProdutoDTO> produtos = obterProdutos();

        if (clientes.isEmpty() || produtos.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma compra encontrada.");
        }

        List<ClienteComprasDTO> clientesCompras = new ArrayList<>();

        for (ClienteDTO cliente : clientes) {
            log.info("Processando cliente: {}", cliente.getNome());
            List<CompraDetalhadaDTO> comprasDetalhadas = cliente.getCompras().stream()
                    .map(compra -> {
                        ProdutoDTO produto = produtos.stream()
                                .filter(p -> p.getCodigo() == Integer.parseInt(compra.getCodigo()))
                                .findFirst()
                                .orElse(null);

                        if (produto != null) {
                            log.info("Found product: {} with anoCompra: {}", produto.getCodigo(), produto.getAnoCompra());
                            BigDecimal valorTotal = BigDecimal.valueOf(produto.getPreco())
                                    .multiply(BigDecimal.valueOf(compra.getQuantidade()))
                                    .setScale(2, RoundingMode.HALF_UP);
                            return new CompraDetalhadaDTO(produto, compra.getQuantidade(), valorTotal.doubleValue());
                        } else {
                            log.warn("Product not found for code: {}", compra.getCodigo());
                        }
                        return null;
                    })
                    .filter(compra -> compra != null)
                    .collect(Collectors.toList());

            BigDecimal totalCompras = comprasDetalhadas.stream()
                    .map(compra -> BigDecimal.valueOf(compra.getValorTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO(
                    cliente.getNome(),
                    cliente.getCpf(),
                    comprasDetalhadas,
                    totalCompras.doubleValue()
            );

            clientesCompras.add(clienteComprasDTO);
        }

        if (clientesCompras.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma compra encontrada.");
        }

        return clientesCompras.stream()
                .sorted((c1, c2) -> Double.compare(c1.getTotalCompras(), c2.getTotalCompras()))
                .collect(Collectors.toList());
    }


    public List<ClienteComprasDTO> clientesFieis() {
        List<ClienteDTO> clientes = obterClientes();
        List<ProdutoDTO> produtos = obterProdutos();

        List<ClienteComprasDTO> clientesCompras = new ArrayList<>();

        for (ClienteDTO cliente : clientes) {
            log.info("Processando cliente: {}", cliente.getNome());
            List<CompraDetalhadaDTO> comprasDetalhadas = cliente.getCompras().stream()
                    .map(compra -> {
                        ProdutoDTO produto = produtos.stream()
                                .filter(p -> p.getCodigo() == Integer.parseInt(compra.getCodigo()))
                                .findFirst()
                                .orElse(null);

                        if (produto != null) {
                            log.info("Produto encontrado: {} com anoCompra: {}", produto.getCodigo(), produto.getAnoCompra());
                            BigDecimal valorTotal = BigDecimal.valueOf(produto.getPreco())
                                    .multiply(BigDecimal.valueOf(compra.getQuantidade()))
                                    .setScale(2, RoundingMode.HALF_UP);
                            return new CompraDetalhadaDTO(produto, compra.getQuantidade(), valorTotal.doubleValue());
                        } else {
                            log.warn("Produto não encontrado para código: {}", compra.getCodigo());
                        }
                        return null;
                    })
                    .filter(compra -> compra != null)
                    .collect(Collectors.toList());

            BigDecimal totalCompras = comprasDetalhadas.stream()
                    .map(compra -> BigDecimal.valueOf(compra.getValorTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO(
                    cliente.getNome(),
                    cliente.getCpf(),
                    comprasDetalhadas,
                    totalCompras.doubleValue()
            );

            clientesCompras.add(clienteComprasDTO);
        }

        if (clientesCompras.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma compra encontrada.");
        }

        return clientesCompras.stream()
                .sorted(Comparator.comparing(ClienteComprasDTO::getTotalCompras).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    public ProdutoDTO recomendarVinho(String cpfCliente) {
        List<ClienteDTO> clientes = obterClientes();
        List<ProdutoDTO> produtos = obterProdutos();

        ClienteDTO cliente = clientes.stream()
                .filter(c -> c.getCpf().equals(cpfCliente))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Map<String, Long> tipoVinhoCount = cliente.getCompras().stream()
                .map(compra -> produtos.stream()
                        .filter(p -> p.getCodigo() == Integer.parseInt(compra.getCodigo()))
                        .findFirst()
                        .orElse(null))
                .filter(produto -> produto != null)
                .collect(Collectors.groupingBy(ProdutoDTO::getTipoVinho, Collectors.counting()));

        String tipoVinhoMaisComprado = tipoVinhoCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma compra encontrada para recomendar vinho"));

        return produtos.stream()
                .filter(p -> p.getTipoVinho().equals(tipoVinhoMaisComprado))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum vinho encontrado para recomendação"));
    }

    public MaiorCompraDTO maiorCompraDoAno(int ano) {
        List<ClienteDTO> clientes = obterClientes();
        List<ProdutoDTO> produtos = obterProdutos();

        return clientes.stream()
                .flatMap(cliente -> cliente.getCompras().stream()
                        .map(compra -> {
                            ProdutoDTO produto = produtos.stream()
                                    .filter(p -> p.getCodigo() == Integer.parseInt(compra.getCodigo()) && p.getAnoCompra() == ano)
                                    .findFirst()
                                    .orElse(null);

                            if (produto != null) {
                                BigDecimal valorTotal = BigDecimal.valueOf(produto.getPreco())
                                        .multiply(BigDecimal.valueOf(compra.getQuantidade()))
                                        .setScale(2, RoundingMode.HALF_UP);
                                return new MaiorCompraDTO(cliente.getNome(), cliente.getCpf(), produto, compra.getQuantidade(), valorTotal.doubleValue());
                            }
                            return null;
                        })
                        .filter(compra -> compra != null)
                )
                .max((c1, c2) -> Double.compare(c1.getValorTotal(), c2.getValorTotal()))
                .orElseThrow(() -> new ResourceNotFoundException("Ano não encontrado"));
    }
}
