package br.com.winepurchase.api.service;

import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CompraServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CompraService compraService;

    private static WireMockServer wireMockServer;

    @Value("${app.urls.clientes}")
    private String urlClientes;

    @Value("${app.urls.produtos}")
    private String urlProdutos;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testListarComprasAgrupadas() {
        // Setup mock responses
        WireMock.stubFor(WireMock.get(urlEqualTo("/clientes"))
                .willReturn(aResponse().withBody("[{\"nome\":\"Cliente 1\",\"cpf\":\"123\",\"compras\":[{\"codigo\":\"1\",\"quantidade\":2}]}]")));
        WireMock.stubFor(WireMock.get(urlEqualTo("/produtos"))
                .willReturn(aResponse().withBody("[{\"codigo\":1,\"tipo_vinho\":\"Tinto\",\"preco\":10.0,\"safra\":\"2018\",\"ano_compra\":2019}]")));

        // Test method
        List<ClienteComprasDTO> result = compraService.listarComprasAgrupadas();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Cliente 1", result.get(0).getNome());
    }

    @Test
    public void testClientesFieis() {
        // Setup mock responses
        WireMock.stubFor(WireMock.get(urlEqualTo("/clientes"))
                .willReturn(aResponse().withBody("[{\"nome\":\"Cliente 1\",\"cpf\":\"123\",\"compras\":[{\"codigo\":\"1\",\"quantidade\":2}]}]")));
        WireMock.stubFor(WireMock.get(urlEqualTo("/produtos"))
                .willReturn(aResponse().withBody("[{\"codigo\":1,\"tipo_vinho\":\"Tinto\",\"preco\":10.0,\"safra\":\"2018\",\"ano_compra\":2019}]")));

        // Test method
        List<ClienteComprasDTO> result = compraService.clientesFieis();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Cliente 1", result.get(0).getNome());
    }

    @Test
    public void testRecomendarVinho() {
        // Setup mock responses
        WireMock.stubFor(WireMock.get(urlEqualTo("/clientes"))
                .willReturn(aResponse().withBody("[{\"nome\":\"Cliente 1\",\"cpf\":\"123\",\"compras\":[{\"codigo\":\"1\",\"quantidade\":2}]}]")));
        WireMock.stubFor(WireMock.get(urlEqualTo("/produtos"))
                .willReturn(aResponse().withBody("[{\"codigo\":1,\"tipo_vinho\":\"Tinto\",\"preco\":10.0,\"safra\":\"2018\",\"ano_compra\":2019}]")));

        // Test method
        ProdutoDTO result = compraService.recomendarVinho("123");
        assertNotNull(result);
        assertEquals("Tinto", result.getTipoVinho());
    }

    @Test
    public void testMaiorCompraDoAno() {
        // Setup mock responses
        WireMock.stubFor(WireMock.get(urlEqualTo("/clientes"))
                .willReturn(aResponse().withBody("[{\"nome\":\"Cliente 1\",\"cpf\":\"123\",\"compras\":[{\"codigo\":\"1\",\"quantidade\":2}]}]")));
        WireMock.stubFor(WireMock.get(urlEqualTo("/produtos"))
                .willReturn(aResponse().withBody("[{\"codigo\":1,\"tipo_vinho\":\"Tinto\",\"preco\":10.0,\"safra\":\"2018\",\"ano_compra\":2019}]")));

        // Test method
        MaiorCompraDTO result = compraService.maiorCompraDoAno(2019);
        assertNotNull(result);
        assertEquals("Cliente 1", result.getNomeCliente());
    }
}
