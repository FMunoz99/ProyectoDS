package backend.estudiante.domain;

import backend.incidente.domain.Incidente;
import backend.objetoPerdido.domain.ObjetoPerdido;
import backend.usuario.domain.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Estudiante extends Usuario {

    private String direccion;

    // Eliminación en cascada de incidentes y objetos perdidos
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Incidente> incidentes;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObjetoPerdido> objetoPerdidos;

}
