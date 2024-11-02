package backend.empleado.domain;

import backend.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Empleado extends Usuario {


}
