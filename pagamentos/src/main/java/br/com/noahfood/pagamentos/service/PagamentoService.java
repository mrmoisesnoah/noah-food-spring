package br.com.noahfood.pagamentos.service;

import br.com.noahfood.pagamentos.dto.PagamentoDTO;
import br.com.noahfood.pagamentos.httpclient.PedidoClient;
import br.com.noahfood.pagamentos.model.PagamentoEntity;
import br.com.noahfood.pagamentos.model.Status;
import br.com.noahfood.pagamentos.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    private final ModelMapper modelMapper;

    private final PedidoClient pedido;

    public Page<PagamentoDTO> findAll(Pageable page){
        return pagamentoRepository.findAll(page)
                .map(pagamentoEntity -> modelMapper.map(pagamentoEntity, PagamentoDTO.class));
    }

    public PagamentoDTO findById(Long id){
        PagamentoEntity pagamentoEntity =  pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
                return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public PagamentoDTO createPagamento(PagamentoDTO pagamentoDTO){
        PagamentoEntity pagamentoEntity = modelMapper.map(pagamentoDTO, PagamentoEntity.class);
        pagamentoEntity.setStatus(Status.CREATED);
        pagamentoEntity = pagamentoRepository.save(pagamentoEntity);
        return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public PagamentoDTO updatePagamento (Long id, PagamentoDTO pagamentoDTO){
        PagamentoEntity pagamentoEntity = modelMapper.map(pagamentoDTO, PagamentoEntity.class);
        pagamentoEntity.setStatus(Status.CREATED);
        pagamentoEntity.setId(id);
        pagamentoEntity = pagamentoRepository.save(pagamentoEntity);
        return modelMapper.map(pagamentoEntity, PagamentoDTO.class);
    }

    public void deletePagamento(Long id){
        pagamentoRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<PagamentoEntity> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMED);
        pagamentoRepository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<PagamentoEntity> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMED_WITHOUT_INTEG);
        pagamentoRepository.save(pagamento.get());

    }
}
