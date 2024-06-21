package br.com.winepurchase.api.controller;

import br.com.winepurchase.api.model.dto.*;
import br.com.winepurchase.api.service.CompraService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompraController.class)
public class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompraService compraService;

    @Test
    public void testListarCompras() throws Exception {
        ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO(
                "Pedin Pé de Cana",
                "12345678901",
                Arrays.asList(new CompraDetalhadaDTO(
                        new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022),
                        2,
                        200.0)),
                200.0);

        when(compraService.listarComprasAgrupadas()).thenReturn(Arrays.asList(clienteComprasDTO));

        mockMvc.perform(get("/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Pedin Pé de Cana"))
                .andExpect(jsonPath("$[0].totalCompras").value(200.0));
    }


    @Test
    public void testMaiorCompraDoAno() throws Exception {
        MaiorCompraDTO maiorCompraDTO = new MaiorCompraDTO("Pedin Pé de Cana", "12345678901", new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022), 2, 200.0);
        when(compraService.maiorCompraDoAno(2022)).thenReturn(maiorCompraDTO);

        mockMvc.perform(get("/compras/maior-compra/2022")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCliente").value("Pedin Pé de Cana"))
                .andExpect(jsonPath("$.valorTotal").value(200.0));
    }

    @Test
    public void testClientesFieis() throws Exception {
        ClienteComprasDTO clienteComprasDTO = new ClienteComprasDTO("Pedin Pé de Cana", "12345678901", Arrays.asList(new CompraDetalhadaDTO(new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022), 2, 200.0)), 200.0);
        when(compraService.clientesFieis()).thenReturn(Arrays.asList(clienteComprasDTO));

        mockMvc.perform(get("/compras/clientes-fieis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Pedin Pé de Cana"))
                .andExpect(jsonPath("$[0].totalCompras").value(200.0));
    }

    @Test
    public void testRecomendarVinho() throws Exception {
        ProdutoDTO produtoDTO = new ProdutoDTO(1, "Tinto", 100.0, "2015", 2022);
        when(compraService.recomendarVinho("12345678901")).thenReturn(produtoDTO);

        mockMvc.perform(get("/compras/recomendacao/cliente/tipo")
                        .param("cpfCliente", "12345678901")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo_vinho").value("Tinto"));
    }
}