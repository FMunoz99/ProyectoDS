package backend.empleado.domain;

import backend.incidente.domain.Incidente;
import backend.objetoPerdido.domain.ObjetoPerdido;
import backend.usuario.domain.Usuario;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Empleado extends Usuario {

    @ElementCollection
    private Map<String, String> horarioDeTrabajo;  // Ejemplo: {"Lunes": "09:00-17:00", "Martes": "09:00-17:00"}

    @OneToMany(mappedBy = "empleado")
    private List<Incidente> incidentes;

    @OneToMany(mappedBy = "empleado")
    private List<ObjetoPerdido> objetoPerdidos;

}
