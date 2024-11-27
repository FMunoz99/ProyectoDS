package backend.events.email_event;

import backend.events.model.Mail;
import backend.objetoPerdido.domain.ObjetoPerdido;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ObjetoPerdidoCreatedEmpleadoEvent extends ApplicationEvent {

    private final ObjetoPerdido objetoPerdido;
    private final Mail mail;

    public ObjetoPerdidoCreatedEmpleadoEvent(ObjetoPerdido objetoPerdido, String recipientEmail) {
        super(objetoPerdido);
        this.objetoPerdido = objetoPerdido;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("ID del Objeto Perdido", objetoPerdido.getId());
        properties.put("Piso", objetoPerdido.getPiso());
        properties.put("Ubicación", objetoPerdido.getUbicacion());
        properties.put("Estado del Reporte", objetoPerdido.getEstadoReporte());
        properties.put("Estado de la Tarea", objetoPerdido.getEstadoTarea());
        properties.put("Detalle", objetoPerdido.getDetalle());
        properties.put("Email", objetoPerdido.getEmail());
        properties.put("Número de Teléfono", objetoPerdido.getPhoneNumber());

        Mail mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail) // Aquí se usa un solo String
                .htmlTemplate(new Mail.HtmlTemplate("ObjetoPerdidoCreatedEmpleadoTemplate", properties))
                .subject("Nuevo Objeto Perdido Asignado")
                .build();

        this.mail = mail;
    }
}
