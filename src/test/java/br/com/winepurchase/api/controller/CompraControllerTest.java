package br.com.winepurchase.api.controller;

import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import br.com.winepurchase.api.service.CompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompraController.class)
public class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompraService compraService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListarCompras() throws Exception {
        ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO("Cliente 1", "123", null, 100.0);
        List<ClienteComprasDTO> clientesCompras = Arrays.asList(clienteComprasDTO);

        when(compraService.listarComprasAgrupadas()).thenReturn(clientesCompras);

        mockMvc.perform(get("/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Cliente 1"));
    }

    @Test
    public void testClientesFieis() throws Exception {
        ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO("Cliente 1", "123", null, 100.0);
        List<ClienteComprasDTO> clientesCompras = Arrays.asList(clienteComprasDTO);

        when(compraService.clientesFieis()).thenReturn(clientesCompras);

        mockMvc.perform(get("/compras/clientes-fieis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Cliente 1"));
    }

    @Test
    public void testRecomendarVinho() throws Exception {
        ProdutoDTO produtoDTO = new ProdutoDTO(1, "Tinto", 10.0, "2018", 2019);

        when(compraService.recomendarVinho(anyString())).thenReturn(produtoDTO);

        mockMvc.perform(get("/compras/recomendacao/cliente/tipo").param("cpfCliente", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoVinho").value("Tinto"));
    }

    @Test
    public void testMaiorCompraDoAno() throws Exception {
        MaiorCompraDTO maiorCompraDTO = new MaiorCompraDTO("Cliente 1", "123", null, 2, 20.0);

        when(compraService.maiorCompraDoAno(anyInt())).thenReturn(maiorCompraDTO);

        mockMvc.perform(get("/compras/maior-compra/2019"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCliente").value("Cliente 1"));
    }
}
