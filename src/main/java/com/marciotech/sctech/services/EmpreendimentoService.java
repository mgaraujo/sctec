package com.marciotech.sctech.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marciotech.sctech.entities.Empreendimento;
import com.marciotech.sctech.entities.Segmento;
import com.marciotech.sctech.exceptions.ApiValidationException;
import com.marciotech.sctech.repositories.EmpreendimentoRepository;
import com.marciotech.sctech.repositories.EmpreendimentoSpecifications;
import com.marciotech.sctech.repositories.MunicipioRepository;
import com.marciotech.sctech.utils.TextNormalizer;

@Service
public class EmpreendimentoService {
	
    @Autowired
    private EmpreendimentoRepository repository;

    @Autowired
    private MunicipioRepository municipioRepository;
    
    
    @Transactional(readOnly = true)
    public List<Empreendimento> listarTodos() {
        return repository.findAll();

    }

    @Transactional(readOnly = true)
        public Page<Empreendimento> listarTodos(
            String nome,
            String nomeResponsavel,
            String municipio,
            Segmento segmento,
            String contato,
            Boolean status,
            Pageable pageable) {

        Specification<Empreendimento> spec = (root, query, cb) -> cb.conjunction();

        String nomeNorm = TextNormalizer.normalize(nome);
        if (nomeNorm != null && !nomeNorm.isBlank()) {
            spec = spec.and(EmpreendimentoSpecifications.nomeContains(nomeNorm));
        }

        String responsavelNorm = TextNormalizer.normalize(nomeResponsavel);
        if (responsavelNorm != null && !responsavelNorm.isBlank()) {
            spec = spec.and(EmpreendimentoSpecifications.nomeResponsavelContains(responsavelNorm));
        }

        String municipioNorm = TextNormalizer.normalize(municipio);
        if (municipioNorm != null && !municipioNorm.isBlank()) {
            spec = spec.and(EmpreendimentoSpecifications.municipioContains(municipioNorm));
        }

        String contatoNorm = TextNormalizer.normalize(contato);
        if (contatoNorm != null && !contatoNorm.isBlank()) {
            spec = spec.and(EmpreendimentoSpecifications.contatoContains(contatoNorm));
        }

        if (segmento != null) {
            spec = spec.and(EmpreendimentoSpecifications.segmentoEquals(segmento));
        }

        if (status != null) {
            spec = spec.and(EmpreendimentoSpecifications.statusEquals(status));
        }

        return repository.findAll(spec, pageable);
    }
    
    @Transactional(readOnly = true)
    public Boolean existeByCodigo(Long codigo) {
        return repository.existsById(codigo);
    }
    
    @Transactional(readOnly = true)
    public Optional<Empreendimento> buscarPorCodigo(Long codigo) {
        return repository.findById(codigo);
    }
    
    @Transactional
    public Empreendimento salvarNovo(Empreendimento empreendimento) {
        normalizarInput(empreendimento);

        if (empreendimento.getMunicipio() == null || empreendimento.getMunicipio().isBlank()) {
            throw new ApiValidationException("Municipio e obrigatorio");
        }

        if (!municipioExisteEmSC(empreendimento.getMunicipio())) {
            throw new ApiValidationException("Municipio nao pertence a SC");
        }
    	return repository.save(empreendimento);
    }
    
    @Transactional
    public Empreendimento atualizar(Long codigo, Empreendimento novo) {
        normalizarInput(novo);

        if (novo.getMunicipio() == null || novo.getMunicipio().isBlank()) {
            throw new ApiValidationException("Municipio e obrigatorio");
        }

        if (!municipioExisteEmSC(novo.getMunicipio())) {
            throw new ApiValidationException("Municipio nao pertence a SC");
        }

        return repository.findById(codigo)
            .map(emp -> {

                emp.setNome(novo.getNome());
                emp.setNomeResponsavel(novo.getNomeResponsavel());
                emp.setMunicipio(novo.getMunicipio());
                emp.setSegmento(novo.getSegmento());
                emp.setContato(novo.getContato());
                emp.setStatus(novo.getStatus());

                return repository.save(emp);
            })
            .orElseThrow(() -> new RuntimeException("Empreendimento não encontrado"));
    }
    
    @Transactional
    public void apagar(Long codigo) {
        repository.deleteById(codigo);
    }

    private static void normalizarInput(Empreendimento empreendimento) {
        if (empreendimento == null) {
            return;
        }

        empreendimento.setNome(TextNormalizer.normalize(empreendimento.getNome()));
        empreendimento.setNomeResponsavel(TextNormalizer.normalize(empreendimento.getNomeResponsavel()));
        empreendimento.setMunicipio(TextNormalizer.normalize(empreendimento.getMunicipio()));
        empreendimento.setContato(TextNormalizer.normalize(empreendimento.getContato()));
    }

    private boolean municipioExisteEmSC(String municipio) {
        String municipioNorm = TextNormalizer.normalize(municipio);
        if (municipioNorm == null || municipioNorm.isBlank()) {
            return false;
        }

        if (municipioRepository.findByNomeIgnoreCase(municipioNorm).isPresent()) {
            return true;
        }

        // Alias comum: o municipio oficial e "Balneario X", mas o usuario informa apenas "X".
        String balneario = "Balneario " + municipioNorm;
        if (municipioRepository.findByNomeIgnoreCase(balneario).isPresent()) {
            return true;
        }

        return false;
    }

}
