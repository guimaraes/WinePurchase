package br.com.winepurchase.api.service.compra;

import br.com.winepurchase.api.model.compra.domain.ProdutoDomain;
import br.com.winepurchase.api.model.compra.dto.ClienteDTO;
import br.com.winepurchase.api.model.compra.dto.CompraDTO;
import br.com.winepurchase.api.mapper.compra.CompraMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRODUTOS_URL = "https://rgr3viiqdl8sikgv.public.blob.vercel-storage.com/produtos-mnboX5IPl6VgG390FECTKqHsD9SkLS.json";
    private static final String CLIENTES_URL = "https://rgr3viiqdl8sikgv.public.blob.vercel-storage.com/clientes-Vz1U6aR3GTsjb3W8BRJhcNKmA81pVh.json";

    private List<ProdutoDomain> fetchProdutos() {
        try {
            String response = restTemplate.getForObject(PRODUTOS_URL, String.class);
            return objectMapper.readValue(response, new TypeReference<List<ProdutoDomain>>() {});
        } catch (Exception e) {
            log.error("Erro ao buscar produtos", e);
            return Collections.emptyList();
        }
    }

    private List<ClienteDTO> fetchClientes() {
        try {
            String response = restTemplate.getForObject(CLIENTES_URL, String.class);
            return objectMapper.readValue(response, new TypeReference<List<ClienteDTO>>() {});
        } catch (Exception e) {
            log.error("Erro ao buscar clientes", e);
            return Collections.emptyList();
        }
    }

    public List<CompraDTO> getComprasOrdenadas() {
        List<ProdutoDomain> produtos = fetchProdutos();
        List<ClienteDTO> clientes = fetchClientes();

        List<CompraDTO> compras = new ArrayList<>();
        for (ClienteDTO cliente : clientes) {
            compras.addAll(CompraMapper.toCompraDTOList(produtos, cliente));
        }
        return compras.stream()
                .sorted(Comparator.comparingDouble(CompraDTO::getValorTotal))
                .collect(Collectors.toList());
    }

    public CompraDTO getMaiorCompraPorAno(int ano) {
        List<CompraDTO> compras = getComprasOrdenadas();
        return compras.stream()
                .filter(compra -> compra.getAnoCompra() == ano)
                .max(Comparator.comparingDouble(CompraDTO::getValorTotal))
                .orElse(null);
    }

    public List<ClienteDTO> getClientesFieis() {
        List<ClienteDTO> clientes = fetchClientes();
        return clientes.stream()
                .sorted((c1, c2) -> Double.compare(
                        c2.getCompras().stream().mapToDouble(CompraDTO::getValorTotal).sum(),
                        c1.getCompras().stream().mapToDouble(CompraDTO::getValorTotal).sum()
                ))
                .limit(3)
                .collect(Collectors.toList());
    }

    public ProdutoDomain getRecomendacaoVinho(String cpf) {
        List<ProdutoDomain> produtos = fetchProdutos();
        List<ClienteDTO> clientes = fetchClientes();

        ClienteDTO cliente = clientes.stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);

        if (cliente == null) {
            return null;
        }

        Map<String, Long> tipoVinhoCount = cliente.getCompras().stream()
                .collect(Collectors.groupingBy(CompraDTO::getTipoVinho, Collectors.counting()));

        String tipoVinhoMaisComprado = Collections.max(tipoVinhoCount.entrySet(), Map.Entry.comparingByValue()).getKey();

        return produtos.stream()
                .filter(p -> p.getTipoVinho().equalsIgnoreCase(tipoVinhoMaisComprado))
                .findAny()
                .orElse(null);
    }
}
