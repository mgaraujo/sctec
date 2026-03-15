package com.marciotech.sctech.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marciotech.sctech.entities.Empreendimento;
import com.marciotech.sctech.entities.Segmento;
import com.marciotech.sctech.services.EmpreendimentoService;

@RestController
@RequestMapping("/empreendimentos")
public class EmpreendimentoController {

    private final EmpreendimentoService service;

    public EmpreendimentoController(EmpreendimentoService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Empreendimento> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String nomeResponsavel,
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) Segmento segmento,
            @RequestParam(required = false) String contato,
            @RequestParam(required = false) Boolean status,
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable) {

    	return service.listarTodos(nome, nomeResponsavel, municipio, segmento, contato, status, pageable);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Empreendimento> buscar(@PathVariable Long codigo) {

        Optional<Empreendimento> emp = service.buscarPorCodigo(codigo);

        return emp.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empreendimento>  criar(@RequestBody Empreendimento empreendimento) {
        Empreendimento novo = service.salvarNovo(empreendimento);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novo.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(novo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Empreendimento> atualizar(
            @PathVariable Long codigo,
            @RequestBody Empreendimento novo) {
    	
    	if (!service.existeByCodigo(codigo)) {
    		return ResponseEntity.notFound().build();
        }

        Empreendimento atualizado = service.atualizar(codigo, novo);

        return ResponseEntity.ok(atualizado);
    }
    

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deletar(@PathVariable Long codigo) {

        if (!service.existeByCodigo(codigo)) {
            return ResponseEntity.notFound().build();
        }

        service.apagar(codigo);

        return ResponseEntity.noContent().build();
    }

}
