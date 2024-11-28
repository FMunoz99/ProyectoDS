package backend.events.email_event;

import backend.events.model.Mail;
import backend.incidente.domain.Incidente;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class IncidenteCreatedEvent extends ApplicationEvent {

    private final Incidente incidente;
    private final Mail mail;

    public IncidenteCreatedEvent(Incidente incidente, String recipientEmail) {
        super(incidente);
        this.incidente = incidente;

        String nombreAlumno = incidente.getEstudiante().getFirstName() + " " + incidente.getEstudiante().getLastName();
        String idObjetoPerdido = incidente.getId().toString();
        String pisoReporte = incidente.getPiso();
        String locationReporte = incidente.getUbicacion();
        String statusReporte = String.valueOf(incidente.getEstadoReporte());
        String statusTask = String.valueOf(incidente.getEstadoTarea());
        String detallesReporte = incidente.getDetalle();
        String email = incidente.getEmpleado().getEmail();
        String phoneNumber = incidente.getPhoneNumber();

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

        // Crear el objeto Mail con un solo destinatario
        Mail mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail) // Usar un solo correo
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteCreatedTemplate", properties))
                .subject("Nuevo Reporte de Incidente Registrado")
                .build();

        this.mail = mail;
    }
}
