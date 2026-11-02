package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Circulos;
import com.personal.vinculo.Service.CirculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/controller/circulo")
public class CirculoController {

    private CirculoService service;

    public CirculoController(CirculoService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<Circulos> traerTodos(){
        return service.sacarTodos();
    }

    @GetMapping("/porID/{id}")
    public Optional<Circulos> porID(@PathVariable int id) {
        return service.buscarPorID(id);
    }

    public record PeticionCirculo(String nombre, String color, Integer cadenciaDefectoDias, Integer orden) {
    }

    @GetMapping("/borrar/{id}")
    public void borrado(@PathVariable int id) {
        service.borrarContacto(id);
    }

    @PostMapping
    public ResponseEntity<Circulos> crear(@RequestBody PeticionCirculo p) {
        Validar.obligatorio(p.nombre(), "nombre");
        if (p.color() != null && !p.color().matches("^#[0-9A-Fa-f]{6}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El color debe tener el formato #RRGGBB");
        }

        Circulos circulo = new Circulos();
        circulo.setNombre(p.nombre().trim());
        circulo.setColor(p.color() != null ? p.color().toUpperCase() : null);
        circulo.setDefectoDias(p.cadenciaDefectoDias() != null ? p.cadenciaDefectoDias() : 0);
        circulo.setOrden(p.orden() != null ? p.orden() : 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(circulo));
    }
}
