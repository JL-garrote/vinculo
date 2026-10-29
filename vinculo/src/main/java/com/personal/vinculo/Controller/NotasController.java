package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Notas;
import com.personal.vinculo.Service.NotasService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/id")
    public Optional<Notas> sacarPorID(@RequestBody int id) {
        return notas.buscarPorID(id);
    }

    @PostMapping("todas")
    public List<Notas> todas() {
        return notas.sacarTodos();
    }

    @PostMapping("/contadorNotas")
    public int sacarContador() {
        return notas.contadorDeNotas();
    }
}
