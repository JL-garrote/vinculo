package com.personal.vinculo.Controller;

import com.personal.vinculo.Models.Contactos;
import com.personal.vinculo.Service.ContactoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public record PeticionContacto(String tratamiento, String nombre, String apellidos, String fotoURI,
                                   String empresa, String cargo, String ciudad, String pais, String email,
                                   String telefono, String linkedinURI, String canalPreferido, Long circuloID,
                                   String rolRelacion, Integer cadenciaDias, Boolean prioritario, String intereses,
                                   String cumpleanos, String familia, String aficiones, String hitoProfesional,
                                   String comoConocidos, Long presentadoPorID, String proximoPaso,
                                   String proximoPasoFecha, String origen, String idExterno) {
    }

    @PostMapping
    public ResponseEntity<Contactos> crear(@RequestBody PeticionContacto p) {
        Validar.obligatorio(p.nombre(), "nombre");
        Validar.enumerado(p.canalPreferido(), Set.of("whatsapp", "llamada", "email", "presencial", "videollamada"), "canalPreferido");
        Validar.enumerado(p.origen(), Set.of("manual", "google", "linkedin", "csv"), "origen");

        Contactos c = new Contactos();
        c.setTratamiento(p.tratamiento());
        c.setNombre(p.nombre().trim());
        c.setApellidos(p.apellidos());
        c.setFotoURI(p.fotoURI());
        c.setEmpresa(p.empresa());
        c.setCargo(p.cargo());
        c.setCiudad(p.ciudad());
        c.setPais(p.pais());
        c.setEmail(p.email());
        c.setTelefono(p.telefono());
        c.setLinkedinURI(p.linkedinURI());
        c.setCanalPreferido(p.canalPreferido());
        c.setCirculoID(p.circuloID() != null ? p.circuloID() : 0);
        c.setRolRelacion(p.rolRelacion());
        c.setCadenciaDias(p.cadenciaDias() != null ? p.cadenciaDias() : 0);
        c.setPrioritario(Boolean.TRUE.equals(p.prioritario()) ? 1 : 0);
        c.setIntereses(p.intereses());
        c.setDate(p.cumpleanos());
        c.setFamilia(p.familia());
        c.setAficiones(p.aficiones());
        c.setHitoProfesional(p.hitoProfesional());
        c.setComoConocidos(p.comoConocidos());
        c.setPresentadoPorID(p.presentadoPorID() != null ? p.presentadoPorID() : 0);
        c.setProximoPaso(p.proximoPaso());
        c.setProximoPasoFecha(p.proximoPasoFecha());
        c.setOrigen(p.origen());
        c.setIdExterno(p.idExterno());

        return ResponseEntity.status(HttpStatus.CREATED).body(contacto.crear(c));
    }
}