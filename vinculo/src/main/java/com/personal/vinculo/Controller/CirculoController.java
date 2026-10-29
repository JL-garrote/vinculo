package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Circulos;
import com.personal.vinculo.Service.CirculoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
