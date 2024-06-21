package src.test.br.com.winepurchase.api.service;

import br.com.winepurchase.api.model.dto.*;
import br.com.winepurchase.api.config.*;
import br.com.winepurchase.api.service.CompraService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CompraServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CompraService compraService;

    @Value("${app.urls.clientes}")
    private String urlClientes;

    @Value("${app.urls.produtos}")
    private String urlProdutos;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost", 8080);
    }

    @Test
    public void testListarComprasAgrupadas() {
        ClienteDTO[] clientes = {
                new ClienteDTO("Cliente 1", "12345678901", Arrays.asList(new CompraDTO("1", 2))),
                new ClienteDTO("Cliente 2", "12345678902", Arrays.asList(new CompraDTO("2", 3)))
        };
        ProdutoDTO[] produtos = {
                new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019),
                new ProdutoDTO(2, "Branco", 50.0, "2019", 2020)
        };

        when(restTemplate.getForObject(urlClientes, ClienteDTO[].class)).thenReturn(clientes);
        when(restTemplate.getForObject(urlProdutos, ProdutoDTO[].class)).thenReturn(produtos);

        List<ClienteComprasDTO> result = compraService.listarComprasAgrupadas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cliente 1", result.get(0).getNome());
        assertEquals("Cliente 2", result.get(1).getNome());
    }

    @Test
    public void testClientesFieis() {
        ClienteDTO[] clientes = {
                new ClienteDTO("Cliente 1", "12345678901", Arrays.asList(new CompraDTO("1", 2))),
                new ClienteDTO("Cliente 2", "12345678902", Arrays.asList(new CompraDTO("2", 3))),
                new ClienteDTO("Cliente 3", "12345678903", Arrays.asList(new CompraDTO("3", 5))),
                new ClienteDTO("Cliente 4", "12345678904", Arrays.asList(new CompraDTO("4", 1)))
        };
        ProdutoDTO[] produtos = {
                new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019),
                new ProdutoDTO(2, "Branco", 50.0, "2019", 2020),
                new ProdutoDTO(3, "Rosé", 75.0, "2020", 2021),
                new ProdutoDTO(4, "Espumante", 120.0, "2021", 2022)
        };

        when(restTemplate.getForObject(urlClientes, ClienteDTO[].class)).thenReturn(clientes);
        when(restTemplate.getForObject(urlProdutos, ProdutoDTO[].class)).thenReturn(produtos);

        List<ClienteComprasDTO> result = compraService.clientesFieis();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Cliente 3", result.get(0).getNome());
        assertEquals("Cliente 1", result.get(1).getNome());
        assertEquals("Cliente 2", result.get(2).getNome());
    }

    @Test
    public void testRecomendarVinho() {
        ClienteDTO[] clientes = {
                new ClienteDTO("Cliente 1", "12345678901", Arrays.asList(new CompraDTO("1", 2), new CompraDTO("2", 3)))
        };
        ProdutoDTO[] produtos = {
                new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019),
                new ProdutoDTO(2, "Branco", 50.0, "2019", 2020)
        };

        when(restTemplate.getForObject(urlClientes, ClienteDTO[].class)).thenReturn(clientes);
        when(restTemplate.getForObject(urlProdutos, ProdutoDTO[].class)).thenReturn(produtos);

        ProdutoDTO recomendacao = compraService.recomendarVinho("12345678901");

        assertNotNull(recomendacao);
        assertEquals("Branco", recomendacao.getTipoVinho());
    }

    @Test
    public void testMaiorCompraDoAno() {
        ClienteDTO[] clientes = {
                new ClienteDTO("Cliente 1", "12345678901", Arrays.asList(new CompraDTO("1", 2))),
                new ClienteDTO("Cliente 2", "12345678902", Arrays.asList(new CompraDTO("2", 3)))
        };
        ProdutoDTO[] produtos = {
                new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019),
                new ProdutoDTO(2, "Branco", 50.0, "2019", 2020)
        };

        when(restTemplate.getForObject(urlClientes, ClienteDTO[].class)).thenReturn(clientes);
        when(restTemplate.getForObject(urlProdutos, ProdutoDTO[].class)).thenReturn(produtos);

        MaiorCompraDTO maiorCompra = compraService.maiorCompraDoAno(2020);

        assertNotNull(maiorCompra);
        assertEquals("Cliente 2", maiorCompra.getNomeCliente());
        assertEquals("Branco", maiorCompra.getProduto().getTipoVinho());
    }
}
