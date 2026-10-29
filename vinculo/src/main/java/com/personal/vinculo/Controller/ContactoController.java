package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Contactos;
import com.personal.vinculo.Service.ContactoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/todos")
    public List<Contactos> traerTodos() {
        return contacto.sacarTodos();
    }

    @PostMapping("/porID")
    public Optional<Contactos> porID(@RequestBody long id) {
        return contacto.buscarPorID(id);
    }

    @PostMapping("/contador")
    public int sacarContador() {
        return contacto.contadorDeContactos();
    }
}