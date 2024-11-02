package backend.incidente.domain;

import backend.empleado.domain.Empleado;
import backend.estudiante.domain.Estudiante;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 10)
    private String piso;

    private String detalle;

    @NotNull
    private String ubicacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoReporte estadoReporte;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoTarea estadoTarea;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 1, max = 15)
    private String phoneNumber;

    @Size(max = 255)
    private String descripcion;

    @ManyToOne
    private Estudiante estudiante;

    @ManyToOne
    private Empleado empleado;
}
