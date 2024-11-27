package backend.events.email_event;

import backend.events.model.Mail;
import backend.objetoPerdido.domain.ObjetoPerdido;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ObjetoPerdidoCreatedEvent extends ApplicationEvent {

    private final ObjetoPerdido objetoPerdido;
    private final Mail mail;

    public ObjetoPerdidoCreatedEvent(ObjetoPerdido objetoPerdido, String recipientEmail) {
        super(objetoPerdido);
        this.objetoPerdido = objetoPerdido;

        String nombreAlumno = objetoPerdido.getEstudiante().getFirstName() + " " + objetoPerdido.getEstudiante().getLastName();
        String idObjetoPerdido = objetoPerdido.getId().toString();
        String pisoReporte = objetoPerdido.getPiso();
        String locationReporte = objetoPerdido.getUbicacion();
        String statusReporte = String.valueOf(objetoPerdido.getEstadoReporte());
        String statusTask = String.valueOf(objetoPerdido.getEstadoTarea());
        String detallesReporte = objetoPerdido.getDetalle();
        String email = objetoPerdido.getEmpleado().getEmail();
        String phoneNumber = objetoPerdido.getPhoneNumber();

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("NombreAlumno", nombreAlumno);
        properties.put("IDObjetoPerdido", idObjetoPerdido);
        properties.put("Piso", pisoReporte);
        properties.put("Ubicacion", locationReporte);
        properties.put("EstadoReporte", statusReporte);
        properties.put("EstadoTarea", statusTask);
        properties.put("Detalle", detallesReporte);
        properties.put("EmailContacto", email);
        properties.put("TelefonoContacto", phoneNumber);

        Mail mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail) // Ahora usa un solo String
                .htmlTemplate(new Mail.HtmlTemplate("ObjetoPerdidoCreatedTemplate", properties))
                .subject("Nuevo Reporte de Objeto Perdido Creado")
                .build();

        this.mail = mail;
    }
}
