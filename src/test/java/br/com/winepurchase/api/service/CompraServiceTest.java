package br.com.winepurchase.api.service;

import br.com.winepurchase.api.model.dto.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompraServiceTest {

    @InjectMocks
    private CompraService compraService;

    private WireMockServer wireMockServer;

    @Value("${app.urls.clientes}")
    private String urlClientes;

    @Value("${app.urls.produtos}")
    private String urlProdutos;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        urlClientes = wireMockServer.baseUrl() + "/clientes";
        urlProdutos = wireMockServer.baseUrl() + "/produtos";

        compraService = new CompraService(new RestTemplate(), urlClientes, urlProdutos);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testObterClientes() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"John Doe\", \"cpf\": \"12345678901\", \"compras\": []}]")));

        List<ClienteDTO> resultado = compraService.obterClientes();

        assertEquals(1, resultado.size());
        assertEquals("John Doe", resultado.get(0).getNome());
    }

    @Test
    public void testObterProdutos() {
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        List<ProdutoDTO> resultado = compraService.obterProdutos();

        assertEquals(1, resultado.size());
        assertEquals("Tinto", resultado.get(0).getTipoVinho());
    }

    @Test
    public void testListarComprasAgrupadas() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"John Doe\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        List<ClienteComprasDTO> resultado = compraService.listarComprasAgrupadas();

        assertEquals(1, resultado.size());
        assertEquals("John Doe", resultado.get(0).getNome());
        assertEquals(200.0, resultado.get(0).getTotalCompras());
    }

    @Test
    public void testClientesFieis() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"John Doe\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        List<ClienteComprasDTO> resultado = compraService.clientesFieis();

        assertEquals(1, resultado.size());
        assertEquals("John Doe", resultado.get(0).getNome());
        assertEquals(200.0, resultado.get(0).getTotalCompras());
    }

    @Test
    public void testRecomendarVinho() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"John Doe\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ProdutoDTO resultado = compraService    .recomendarVinho("12345678901");

        assertEquals("Tinto", resultado.getTipoVinho());
    }

    @Test
    public void testMaiorCompraDoAno() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"John Doe\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        MaiorCompraDTO resultado = compraService.maiorCompraDoAno(2022);

        assertEquals("John Doe", resultado.getNomeCliente());
        assertEquals(200.0, resultado.getValorTotal());
    }
}
