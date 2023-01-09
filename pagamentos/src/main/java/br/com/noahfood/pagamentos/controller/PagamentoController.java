package br.com.noahfood.pagamentos.controller;

import br.com.noahfood.pagamentos.dto.PagamentoDTO;
import br.com.noahfood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PagamentoDTO> buscarPagamentos(@PageableDefault(size = 10)Pageable page){
        return pagamentoService.findAll(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> buscarPagamentos(@PathVariable @NotNull Long id){
        PagamentoDTO pagamentoDTO = pagamentoService.findById(id);
        return  ResponseEntity.ok(pagamentoDTO);
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> create(@RequestBody @Valid PagamentoDTO pagamentoDTO,
                                                       UriComponentsBuilder builder) {
        PagamentoDTO pagamento = pagamentoService.createPagamento(pagamentoDTO);
        URI endereco = builder.path("/pagamento/{id}").buildAndExpand(pagamento.getId()).toUri();

        rabbitTemplate.convertAndSend("payment.ex","", pagamento);
        return ResponseEntity.created(endereco).body(pagamento);

    }
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> update(@PathVariable @NotNull Long id,
                                               @RequestBody @Valid PagamentoDTO pagamentoDTO){
        PagamentoDTO pagamento = pagamentoService.updatePagamento(id,pagamentoDTO);
        return ResponseEntity.ok(pagamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDTO> delete(@PathVariable @NotNull Long id){
        pagamentoService.deletePagamento(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alteraStatus(id);
    }
}
