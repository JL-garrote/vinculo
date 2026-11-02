package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Interacciones;
import com.personal.vinculo.Service.InteraccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/controller/interaccion")
public class InteraccionController {

    private InteraccionService interacciones;

    public InteraccionController(InteraccionService interacciones) {
        this.interacciones = interacciones;
    }

    @GetMapping("/todas")
    public List<Interacciones> todas() {
        return interacciones.sacarTodas();
    }

    @GetMapping("/contacto/{contactoID}")
    public List<Interacciones> deContacto(@PathVariable long contactoID) {
        return interacciones.deContacto(contactoID);
    }

    public record PeticionInteraccion(Long contactoID, Long contactoRelacionadoID, String tipo, String titulo,
                                      String fecha, String lugar, String resumen, String temas, Boolean borrador) {
    }

    @PostMapping
    public ResponseEntity<Interacciones> crear(@RequestBody PeticionInteraccion p) {
        Validar.obligatorio(p.contactoID(), "contactoID");
        Validar.obligatorio(p.tipo(), "tipo");
        Validar.obligatorio(p.fecha(), "fecha");
        Validar.enumerado(p.tipo(), InteraccionService.TIPOS, "tipo");

        Interacciones i = new Interacciones();
        i.setContactoID(p.contactoID());
        i.setContactoRelacionadoID(p.contactoRelacionadoID() != null ? p.contactoRelacionadoID() : 0);
        i.setTipo(p.tipo());
        i.setTitulo(p.titulo());
        i.setFecha(p.fecha());
        i.setLugar(p.lugar());
        i.setResumen(p.resumen());
        i.setTemas(p.temas());
        i.setBorrador(Boolean.TRUE.equals(p.borrador()) ? 1 : 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(interacciones.crear(i));
    }
}
