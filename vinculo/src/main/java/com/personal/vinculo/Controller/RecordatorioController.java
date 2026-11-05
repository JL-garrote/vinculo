package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Recordatorios;
import com.personal.vinculo.Service.RecordatorioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/controller/recordatorio")
public class RecordatorioController {

    private RecordatorioService recordatorios;

    public RecordatorioController(RecordatorioService recordatorios) {
        this.recordatorios = recordatorios;
    }

    @GetMapping("/pendientes")
    public List<Recordatorios> pendientes() {
        return recordatorios.pendientes();
    }

    @GetMapping("/contacto/{contactoID}")
    public List<Recordatorios> deContacto(@PathVariable long contactoID) {
        return recordatorios.deContacto(contactoID);
    }

    @PutMapping("/actualizar/{id}")
    public void actualizar(@PathVariable int id,@RequestBody Recordatorios record) {
        recordatorios.actualizar(id,record);
    }

    public record PeticionRecordatorio(Long contactoID, String titulo, String descripcion, String venceEn,
                                       Integer recurrenciaDias) {
    }

    @PostMapping
    public ResponseEntity<Recordatorios> crear(@RequestBody PeticionRecordatorio p) {
        Validar.obligatorio(p.titulo(), "titulo");
        Validar.obligatorio(p.venceEn(), "venceEn");

        Recordatorios r = new Recordatorios();
        r.setContactoID(p.contactoID() != null ? p.contactoID() : 0);
        r.setTitulo(p.titulo().trim());
        r.setDescripcion(p.descripcion());
        r.setVenceEnFecha(p.venceEn());
        r.setRecurrenciaDias(p.recurrenciaDias() != null ? p.recurrenciaDias() : 0);

        return ResponseEntity.status(HttpStatus.CREATED).body(recordatorios.crear(r));
    }
}
