package com.marciotech.sctech.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.marciotech.sctech.entities.Empreendimento;

public interface EmpreendimentoRepository extends JpaRepository<Empreendimento, Long>, JpaSpecificationExecutor<Empreendimento> {


}
