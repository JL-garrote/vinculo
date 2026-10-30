package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Notas;
import com.personal.vinculo.Service.NotasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/controller/notas")
public class NotasController {

    private NotasService notas;

    public NotasController(NotasService notas) {
        this.notas = notas;
    }

    @GetMapping("/id/{id}")
    public Optional<Notas> sacarPorID(@PathVariable int id) {
        return notas.buscarPorID(id);
    }

    @GetMapping("todas")
    public List<Notas> todas() {
        return notas.sacarTodos();
    }

    @GetMapping("/contadorNotas")
    public int sacarContador() {
        return notas.contadorDeNotas();
    }

    public record PeticionNota(Long contactoID, String tipo, String titulo, String contenido, Boolean fijada) {
    }

    @PostMapping
    public ResponseEntity<Notas> crear(@RequestBody PeticionNota p) {
        Validar.obligatorio(p.contactoID(), "contactoID");
        Validar.obligatorio(p.contenido(), "contenido");
        Validar.enumerado(p.tipo(), Set.of("general", "oportunidad", "preferencia", "recurso"), "tipo");

        Notas nota = new Notas();
        nota.setContactoID(p.contactoID());
        nota.setTipo(p.tipo());
        nota.setTitulo(p.titulo());
        nota.setContenido(p.contenido().trim());
        nota.setFijada(Boolean.TRUE.equals(p.fijada()) ? 1 : 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(notas.crear(nota));
    }
}
