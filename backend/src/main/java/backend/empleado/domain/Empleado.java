package backend.empleado.domain;

import backend.incidente.domain.Incidente;
import backend.objetoPerdido.domain.ObjetoPerdido;
import backend.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Empleado extends Usuario {

    @OneToMany(mappedBy = "empleado")
    private List<Incidente> incidentes;

    @OneToMany(mappedBy = "empleado")
    private List<ObjetoPerdido> objetoPerdidos;

}
