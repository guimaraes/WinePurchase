package src.test.br.com.winepurchase.api.service;

import br.com.winepurchase.api.controller.CompraController;
import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import br.com.winepurchase.api.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompraControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Mock
    private CompraService compraService;

    @InjectMocks
    private CompraController compraController;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        headers = new HttpHeaders();
    }

    @Test
    public void testListarCompras() {
        List<ClienteComprasDTO> compras = List.of(
                new ClienteComprasDTO("Cliente 1", "12345678901", List.of(), 200.0),
                new ClienteComprasDTO("Cliente 2", "12345678902", List.of(), 300.0)
        );

        when(compraService.listarComprasAgrupadas()).thenReturn(compras);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/compras"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Cliente 1"));
        assertTrue(response.getBody().contains("Cliente 2"));
    }

    @Test
    public void testMaiorCompraDoAno() {
        MaiorCompraDTO maiorCompra = new MaiorCompraDTO("Cliente 2", "12345678902", new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019), 3, 300.0);

        when(compraService.maiorCompraDoAno(2020)).thenReturn(maiorCompra);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/compras/maior-compra/2020"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Cliente 2"));
        assertTrue(response.getBody().contains("Tinto"));
    }

    @Test
    public void testClientesFieis() {
        List<ClienteComprasDTO> clientes = List.of(
                new ClienteComprasDTO("Cliente 1", "12345678901", List.of(), 500.0),
                new ClienteComprasDTO("Cliente 2", "12345678902", List.of(), 400.0),
                new ClienteComprasDTO("Cliente 3", "12345678903", List.of(), 300.0)
        );

        when(compraService.clientesFieis()).thenReturn(clientes);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/compras/clientes-fieis"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Cliente 1"));
        assertTrue(response.getBody().contains("Cliente 2"));
        assertTrue(response.getBody().contains("Cliente 3"));
    }

    @Test
    public void testRecomendarVinho() {
        ProdutoDTO produto = new ProdutoDTO(1, "Tinto", 100.0, "2018", 2019);

        when(compraService.recomendarVinho("12345678901")).thenReturn(produto);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/compras/recomendacao/cliente/tipo?cpfCliente=12345678901"),
                HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Tinto"));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
