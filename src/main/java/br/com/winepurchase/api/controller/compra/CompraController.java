package br.com.winepurchase.api.controller.compra;

import br.com.winepurchase.api.model.compra.domain.ProdutoDomain;
import br.com.winepurchase.api.model.compra.dto.ClienteDTO;
import br.com.winepurchase.api.model.compra.dto.CompraDTO;
import br.com.winepurchase.api.service.compra.CompraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@Slf4j
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public List<CompraDTO> getComprasOrdenadas() {
        return compraService.getComprasOrdenadas();
    }

    @GetMapping("/maior-compra/{ano}")
    public CompraDTO getMaiorCompraPorAno(@PathVariable int ano) {
        return compraService.getMaiorCompraPorAno(ano);
    }

    @GetMapping("/clientes-fieis")
    public List<ClienteDTO> getClientesFieis() {
        return compraService.getClientesFieis();
    }

    @GetMapping("/recomendacao/{cpf}")
    public ProdutoDomain getRecomendacaoVinho(@PathVariable String cpf) {
        return compraService.getRecomendacaoVinho(cpf);
    }
}
