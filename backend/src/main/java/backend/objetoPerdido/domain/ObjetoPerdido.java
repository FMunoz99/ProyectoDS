package backend.objetoPerdido.domain;

import backend.empleado.domain.Empleado;
import backend.estudiante.domain.Estudiante;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ObjetoPerdido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    private String piso;

    @NotNull
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoReporte", nullable = false)
    private EstadoReporte estadoReporte;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoTarea", nullable = false)
    private EstadoTarea estadoTarea;

    @NotNull
    @Size(max = 255)
    private String detalle;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 1, max = 15)
    private String phoneNumber;

    @Size(max = 255)
    private String description;

    private LocalDate fechaReporte;

    private String fotoObjetoPerdidoUrl;

    @ManyToOne
    private Estudiante estudiante;

    @ManyToOne
    private Empleado empleado;

    @PrePersist
    public void prePersist() {
        if (this.fechaReporte == null) {
            this.fechaReporte = LocalDate.now();
        }
    }
}
