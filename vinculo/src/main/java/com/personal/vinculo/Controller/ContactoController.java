package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Contactos;
import com.personal.vinculo.Service.ContactoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/controller/contacto")
public class ContactoController {

    private ContactoService contacto;

    public ContactoController(ContactoService contacto) {
        this.contacto = contacto;
    }

    @GetMapping("/todos")
    public List<Contactos> traerTodos() {
        return contacto.sacarTodos();
    }

    @GetMapping("/porID/{id}")
    public Optional<Contactos> porID(@PathVariable long id) {
        return contacto.buscarPorID(id);
    }

    @GetMapping("/contador")
    public int sacarContador() {
        return contacto.contadorDeContactos();
    }
}