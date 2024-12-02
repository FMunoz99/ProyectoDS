package backend.noticias.domain;

import backend.admin.domain.Admin;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Noticias {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idNoticias;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1500)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false, unique = false)
    private Admin admin;
}
