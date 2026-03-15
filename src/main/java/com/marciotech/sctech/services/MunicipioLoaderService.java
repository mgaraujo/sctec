package com.marciotech.sctech.services;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marciotech.sctech.dto.MunicipioDTO;
import com.marciotech.sctech.entities.Municipio;
import com.marciotech.sctech.repositories.MunicipioRepository;
import com.marciotech.sctech.utils.TextNormalizer;

import jakarta.annotation.PostConstruct;

@Service
public class MunicipioLoaderService {

    private static final Logger log = LoggerFactory.getLogger(MunicipioLoaderService.class);

    private static final String URL =
        "https://servicodados.ibge.gov.br/api/v1/localidades/estados/SC/municipios";

    private final MunicipioRepository repository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public MunicipioLoaderService(MunicipioRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3_000);
        requestFactory.setReadTimeout(10_000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @PostConstruct
    public void carregarMunicipios() {

        if (repository.count() > 0) {
            return;
        }

        MunicipioDTO[] municipios = carregarDaApi();
        if (municipios == null || municipios.length == 0) {
            municipios = carregarDoArquivo();
        }

        if (municipios == null || municipios.length == 0) {
            log.warn("Nenhum municipio carregado (API offline e arquivo indisponivel). Aplicacao vai subir sem municipios.");
            return;
        }

        List<Municipio> entidades = Arrays.stream(municipios)
                .filter(dto -> dto != null && dto.getId() != null && dto.getNome() != null)
                .map(dto -> {
                    Municipio m = new Municipio();
                    m.setId(dto.getId());
                    m.setNome(TextNormalizer.normalize(dto.getNome()));
                    return m;
                })
                .toList();

        repository.saveAll(entidades);
        log.info("{} municipios de SC carregados no banco.", entidades.size());
    }

    private MunicipioDTO[] carregarDaApi() {
        try {
            MunicipioDTO[] municipios = restTemplate.getForObject(URL, MunicipioDTO[].class);
            if (municipios != null && municipios.length > 0) {
                log.info("Municipios carregados via API do IBGE.");
            }
            return municipios;
        } catch (Exception e) {
            log.warn("API do IBGE indisponivel. Usando fallback local (municipios_sc.json).");
            return null;
        }
    }

    private MunicipioDTO[] carregarDoArquivo() {
        try (InputStream in = new ClassPathResource("municipios_sc.json").getInputStream()) {
            MunicipioDTO[] municipios = objectMapper.readValue(in, MunicipioDTO[].class);
            if (municipios != null && municipios.length > 0) {
                log.info("Municipios carregados via arquivo local municipios_sc.json.");
            }
            return municipios;
        } catch (Exception e) {
            log.warn("Falha ao ler municipios_sc.json do classpath.", e);
            return null;
        }
    }
}
