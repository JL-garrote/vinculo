package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Perfil;
import com.personal.vinculo.Service.PerfilService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PerfilController {

    private PerfilService perfil = new PerfilService();

    @PostMapping
    public Perfil sacarPerfil() {
        return (Perfil) perfil.sacarUsuario();
    }
}