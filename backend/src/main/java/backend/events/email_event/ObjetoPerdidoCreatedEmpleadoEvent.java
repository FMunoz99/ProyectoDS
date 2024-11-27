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

        String nombreEmpleado = objetoPerdido.getEmpleado().getFirstName() + " " + objetoPerdido.getEmpleado().getLastName();
        String idObjetoPerdido = objetoPerdido.getId().toString();
        String pisoReporte = objetoPerdido.getPiso();
        String locationReporte = objetoPerdido.getUbicacion();
        String detallesReporte = objetoPerdido.getDetalle();
        String nombreAlumno = objetoPerdido.getEstudiante().getFirstName() + " " + objetoPerdido.getEstudiante().getLastName();
        String phoneNumber = objetoPerdido.getPhoneNumber();

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("NombreEmpleado", nombreEmpleado);
        properties.put("IDObjetoPerdido", idObjetoPerdido);
        properties.put("Piso", pisoReporte);
        properties.put("Ubicacion", locationReporte);
        properties.put("Detalle", detallesReporte);
        properties.put("NombreAlumno",nombreAlumno);
        properties.put("TelefonoContacto", phoneNumber);

        Mail mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail) // Aquí se usa un solo String
                .htmlTemplate(new Mail.HtmlTemplate("ObjetoPerdidoCreatedEmpleadoTemplate", properties))
                .subject("Nuevo Objeto Perdido Asignado")
                .build();

        this.mail = mail;
    }
}
