package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Notas;
import com.personal.vinculo.Service.NotasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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
}
