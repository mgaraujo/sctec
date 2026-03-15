package com.marciotech.sctech.repositories;

import org.springframework.data.jpa.domain.Specification;

import com.marciotech.sctech.entities.Empreendimento;
import com.marciotech.sctech.entities.Segmento;

public final class EmpreendimentoSpecifications {

    private EmpreendimentoSpecifications() {}

    public static Specification<Empreendimento> nomeContains(String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nome")), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Empreendimento> nomeResponsavelContains(String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nomeResponsavel")), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Empreendimento> municipioContains(String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("municipio")), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Empreendimento> contatoContains(String value) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("contato")), "%" + value.toLowerCase() + "%");
    }

    public static Specification<Empreendimento> segmentoEquals(Segmento value) {
        return (root, query, cb) -> cb.equal(root.get("segmento"), value);
    }

    public static Specification<Empreendimento> statusEquals(Boolean value) {
        return (root, query, cb) -> cb.equal(root.get("status"), value);
    }
}

