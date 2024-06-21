package br.com.winepurchase.api.service;

import br.com.winepurchase.api.model.dto.ClienteDTO;
import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import br.com.winepurchase.api.model.dto.CompraDTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "app.urls.clientes=https://rgr3viiqdl8sikgv.public.blob.vercel-storage.com/clientes-Vz1U6aR3GTsjb3W8BRJhcNKmA81pVh.json",
        "app.urls.produtos=https://rgr3viiqdl8sikgv.public.blob.vercel-storage.com/produtos-mnboX5IPl6VgG390FECTKqHsD9SkLS.json"
})
public class CompraServiceTest {

    @InjectMocks
    private CompraService compraService;

    @Mock
    private RestTemplate restTemplate;

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

        String dynamicUrlClientes = wireMockServer.baseUrl() + "/clientes";
        String dynamicUrlProdutos = wireMockServer.baseUrl() + "/produtos";

        ReflectionTestUtils.setField(compraService, "urlClientes", dynamicUrlClientes);
        ReflectionTestUtils.setField(compraService, "urlProdutos", dynamicUrlProdutos);
        ReflectionTestUtils.setField(compraService, "restTemplate", restTemplate); // Injetando manualmente o RestTemplate
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
                        .withBody("[{\"nome\": \"Pedin Pé de Cana\", \"cpf\": \"12345678901\", \"compras\": []}]")));

        ClienteDTO[] mockClientes = {new ClienteDTO("Pedin Pé de Cana", "12345678901", new ArrayList<>())};
        when(restTemplate.getForObject(anyString(), eq(ClienteDTO[].class))).thenReturn(mockClientes);

        List<ClienteDTO> resultado = compraService.obterClientes();

        assertEquals(1, resultado.size());
        assertEquals("Pedin Pé de Cana", resultado.get(0).getNome());
    }

    @Test
    public void testObterProdutos() {
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ProdutoDTO[] mockProdutos = {new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022)};
        when(restTemplate.getForObject(anyString(), eq(ProdutoDTO[].class))).thenReturn(mockProdutos);

        List<ProdutoDTO> resultado = compraService.obterProdutos();

        assertEquals(1, resultado.size());
        assertEquals("Tinto", resultado.get(0).getTipoVinho());
    }

    @Test
    public void testListarComprasAgrupadas() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"Pedin Pé de Cana\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ClienteDTO[] mockClientes = {new ClienteDTO("Pedin Pé de Cana", "12345678901", Arrays.asList(new CompraDTO("1", 2)))};
        ProdutoDTO[] mockProdutos = {new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022)};
        when(restTemplate.getForObject(anyString(), eq(ClienteDTO[].class))).thenReturn(mockClientes);
        when(restTemplate.getForObject(anyString(), eq(ProdutoDTO[].class))).thenReturn(mockProdutos);

        List<ClienteComprasDTO> resultado = compraService.listarComprasAgrupadas();

        assertEquals(1, resultado.size());
        assertEquals("Pedin Pé de Cana", resultado.get(0).getNome());
        assertEquals(200.0, resultado.get(0).getTotalCompras());
    }

    @Test
    public void testClientesFieis() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"Pedin Pé de Cana\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ClienteDTO[] mockClientes = {new ClienteDTO("Pedin Pé de Cana", "12345678901", Arrays.asList(new CompraDTO("1", 2)))};
        ProdutoDTO[] mockProdutos = {new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022)};
        when(restTemplate.getForObject(anyString(), eq(ClienteDTO[].class))).thenReturn(mockClientes);
        when(restTemplate.getForObject(anyString(), eq(ProdutoDTO[].class))).thenReturn(mockProdutos);

        List<ClienteComprasDTO> resultado = compraService.clientesFieis();

        assertEquals(1, resultado.size());
        assertEquals("Pedin Pé de Cana", resultado.get(0).getNome());
        assertEquals(200.0, resultado.get(0).getTotalCompras());
    }

    @Test
    public void testRecomendarVinho() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"Pedin Pé de Cana\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ClienteDTO[] mockClientes = {new ClienteDTO("Pedin Pé de Cana", "12345678901", Arrays.asList(new CompraDTO("1", 2)))};
        ProdutoDTO[] mockProdutos = {new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022)};
        when(restTemplate.getForObject(anyString(), eq(ClienteDTO[].class))).thenReturn(mockClientes);
        when(restTemplate.getForObject(anyString(), eq(ProdutoDTO[].class))).thenReturn(mockProdutos);

        ProdutoDTO resultado = compraService.recomendarVinho("12345678901");

        assertEquals("Tinto", resultado.getTipoVinho());
    }

    @Test
    public void testMaiorCompraDoAno() {
        wireMockServer.stubFor(get(urlEqualTo("/clientes"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"nome\": \"Pedin Pé de Cana\", \"cpf\": \"12345678901\", \"compras\": [{\"codigo\": \"1\", \"quantidade\": 2}]}]")));
        wireMockServer.stubFor(get(urlEqualTo("/produtos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"codigo\": 1, \"tipo_vinho\": \"Tinto\", \"preco\": 100.0, \"safra\": \"2015\", \"ano_compra\": 2022}]")));

        ClienteDTO[] mockClientes = {new ClienteDTO("Pedin Pé de Cana", "12345678901", Arrays.asList(new CompraDTO("1", 2)))};
        ProdutoDTO[] mockProdutos = {new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022)};
        when(restTemplate.getForObject(anyString(), eq(ClienteDTO[].class))).thenReturn(mockClientes);
        when(restTemplate.getForObject(anyString(), eq(ProdutoDTO[].class))).thenReturn(mockProdutos);

        MaiorCompraDTO resultado = compraService.maiorCompraDoAno(2022);

        assertEquals("Pedin Pé de Cana", resultado.getNomeCliente());
        assertEquals(200.0, resultado.getValorTotal());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
