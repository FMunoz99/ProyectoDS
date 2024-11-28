package backend.events.email_event;

import backend.events.model.Mail;
import backend.incidente.domain.Incidente;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class IncidenteCreatedEmpleadoEvent extends ApplicationEvent {

    private final Incidente incidente;
    private final Mail mail;

    public IncidenteCreatedEmpleadoEvent(Incidente incidente, String recipientEmail) { // Cambiado a String
        super(incidente);
        this.incidente = incidente;

        String nombreEmpleado = incidente.getEmpleado().getFirstName() + " " + incidente.getEmpleado().getLastName();
        String idObjetoPerdido = incidente.getId().toString();
        String pisoReporte = incidente.getPiso();
        String locationReporte = incidente.getUbicacion();
        String detallesReporte = incidente.getDetalle();
        String nombreAlumno = incidente.getEstudiante().getFirstName() + " " + incidente.getEstudiante().getLastName();
        String phoneNumber = incidente.getPhoneNumber();

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
                .to(recipientEmail) // Ahora usa un solo String
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteCreatedEmpleadoTemplate", properties))
                .subject("Nuevo Reporte de Incidente Asignado")
                .build();

        this.mail = mail;
    }
}
