package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Notas;
import com.personal.vinculo.Service.NotasService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/controller")
public class NotasController {

    private NotasService notas;

    public NotasController(NotasService notas) {
        this.notas = notas;
    }

    @PostMapping
    public Optional<Notas> sacarPorID(int id) {
        return notas.buscarPorID(id);
    }

    @PostMapping
    public List<Notas> todas() {
        return notas.sacarTodos();
    }

    @PostMapping
    public int sacarContador() {
        return notas.contadorDeNotas();
    }
}
