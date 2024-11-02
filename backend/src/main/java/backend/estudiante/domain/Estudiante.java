package backend.estudiante.domain;

import backend.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Estudiante extends Usuario {


}
