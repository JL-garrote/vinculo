package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Pipelines;
import com.personal.vinculo.Models.PipelinesContactos;
import com.personal.vinculo.Service.PipelineService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/controller/pipeline")
public class PipelineController {

    private PipelineService pipeline;

    public PipelineController(PipelineService pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping("/todos")
    public List<Pipelines> traerTodos() {
        return pipeline.sacarTodos();
    }

    @GetMapping("/porID/{id}")
    public Optional<Pipelines> porID(@PathVariable long id) {
        return pipeline.buscarPorID(id);
    }

    @GetMapping("/{id}/contactos")
    public List<PipelinesContactos> contactos(@PathVariable long id) {
        return pipeline.contactosDelPipeline(id);
    }

    /**
     * Cuerpo de las peticiones POST/PUT: solo los campos que el cliente puede enviar.
     * Se usa en lugar de PipelinesContactos porque Jackson rechaza los campos primitivos que faltan en el JSON.
     */
    public record PeticionEntrada(Long contactoID, String etapa, String prioridad,
                                  String siguienteAccion, String siguienteAccionFecha) {

        PipelinesContactos aEntrada() {
            PipelinesContactos entrada = new PipelinesContactos();
            entrada.setContactoID(contactoID != null ? contactoID : 0);
            entrada.setEtapa(etapa);
            entrada.setPrioridad(prioridad);
            entrada.setSiguienteAccion(siguienteAccion);
            entrada.setSiguienteAccionFecha(siguienteAccionFecha);
            return entrada;
        }
    }

    @PostMapping("/{id}/contactos")
    public ResponseEntity<PipelinesContactos> anadirContacto(@PathVariable long id, @RequestBody PeticionEntrada peticion) {
        if (peticion.contactoID() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta contactoID");
        }
        PipelinesContactos entrada = peticion.aEntrada();
        validar(entrada);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pipeline.anadirContacto(id, entrada));
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El contacto ya está en este pipeline");
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pipeline o el contacto no existen");
        }
    }

    @PutMapping("/contactos/{entradaID}")
    public PipelinesContactos actualizarContacto(@PathVariable long entradaID, @RequestBody PeticionEntrada peticion) {
        PipelinesContactos cambios = peticion.aEntrada();
        validar(cambios);
        return pipeline.actualizarEntrada(entradaID, cambios)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));
    }

    @DeleteMapping("/contactos/{entradaID}")
    public ResponseEntity<Void> quitarContacto(@PathVariable long entradaID) {
        if (!pipeline.quitarEntrada(entradaID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada");
        }
        return ResponseEntity.noContent().build();
    }

    private void validar(PipelinesContactos entrada) {
        if (entrada.getEtapa() != null && !PipelineService.ETAPAS.contains(entrada.getEtapa())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etapa no válida: " + entrada.getEtapa());
        }
        if (entrada.getPrioridad() != null && !PipelineService.PRIORIDADES.contains(entrada.getPrioridad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prioridad no válida: " + entrada.getPrioridad());
        }
    }
}
