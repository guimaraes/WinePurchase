package br.com.winepurchase.api.service;

import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        wireMockServer = new WireMockServer(8082); // Usar porta diferente para evitar conflito
        wireMockServer.start();

        configureFor("localhost", 8082);
        stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\":\"Cliente 1\",\"cpf\":\"12345678901\",\"compras\":[{\"codigo\":\"1\",\"quantidade\":2}]}]")));
        stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\":1,\"tipo_vinho\":\"Tinto\",\"preco\":100.0,\"safra\":\"2018\",\"ano_compra\":2019}]")));
    }

    @Test
    public void testListarComprasAgrupadas() {
        List<ClienteComprasDTO> result = compraService.listarComprasAgrupadas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cliente 1", result.get(0).getNome());
    }

    @Test
    public void testClientesFieis() {
        List<ClienteComprasDTO> result = compraService.clientesFieis();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cliente 1", result.get(0).getNome());
    }

    @Test
    public void testRecomendarVinho() {
        ProdutoDTO recomendacao = compraService.recomendarVinho("12345678901");

        assertNotNull(recomendacao);
        assertEquals("Tinto", recomendacao.getTipoVinho());
    }

    @Test
    public void testMaiorCompraDoAno() {
        MaiorCompraDTO maiorCompra = compraService.maiorCompraDoAno(2019);

        assertNotNull(maiorCompra);
        assertEquals("Cliente 1", maiorCompra.getNomeCliente());
        assertEquals("Tinto", maiorCompra.getProduto().getTipoVinho());
    }
}
