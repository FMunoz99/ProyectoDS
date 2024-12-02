package backend.admin.domain;

import backend.noticias.domain.Noticias;
import backend.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends Usuario {

}
