package backend.admin.domain;

import backend.noticias.domain.Noticias;
import backend.usuario.domain.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Admin extends Usuario {

    @OneToMany(mappedBy = "admin")
    private List<Noticias> noticias;
}
