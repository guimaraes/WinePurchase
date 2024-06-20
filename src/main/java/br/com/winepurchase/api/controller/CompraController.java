package br.com.winepurchase.api.controller;

import br.com.winepurchase.api.model.dto.ClienteComprasDTO;
import br.com.winepurchase.api.model.dto.MaiorCompraDTO;
import br.com.winepurchase.api.model.dto.ProdutoDTO;
import br.com.winepurchase.api.service.CompraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/compras")
@Slf4j
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public List<ClienteComprasDTO> listarCompras() {
        return compraService.listarComprasAgrupadas();
    }

    @GetMapping("/maior-compra/{ano}")
    public ResponseEntity<MaiorCompraDTO> maiorCompraDoAno(@PathVariable int ano) {
        MaiorCompraDTO maiorCompra = compraService.maiorCompraDoAno(ano);
        return ResponseEntity.ok(maiorCompra);
    }

    @GetMapping("/clientes-fieis")
    public List<ClienteComprasDTO> clientesFieis() {
        return compraService.clientesFieis();
    }

    @GetMapping("/recomendacao/cliente/tipo")
    public ResponseEntity<ProdutoDTO> recomendarVinho(@RequestParam String cpfCliente) {
        ProdutoDTO recomendacao = compraService.recomendarVinho(cpfCliente);
        return ResponseEntity.ok(recomendacao);
    }
}

