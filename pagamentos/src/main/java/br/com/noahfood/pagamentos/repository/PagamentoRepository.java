package br.com.noahfood.pagamentos.repository;

import br.com.noahfood.pagamentos.model.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {


}
