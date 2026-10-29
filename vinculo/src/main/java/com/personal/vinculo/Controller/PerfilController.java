package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Perfil;
import com.personal.vinculo.Service.PerfilService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/controller")
public class PerfilController {

    private PerfilService perfil;

    public PerfilController(PerfilService perfil) {
        this.perfil = perfil;
    }

    @PostMapping("perfil")
    public Perfil sacarPerfil() {
        return (Perfil) perfil.sacarUsuario();
    }
}