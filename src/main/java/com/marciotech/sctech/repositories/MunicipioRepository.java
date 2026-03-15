package com.marciotech.sctech.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marciotech.sctech.entities.Municipio;

public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    Optional<Municipio> findByNomeIgnoreCase(String nome);

}