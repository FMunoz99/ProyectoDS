package backend.admin.domain;

import backend.noticias.domain.Noticias;
import backend.usuario.domain.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admin")
public class Admin extends Usuario {

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Noticias> noticias;
}
