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

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("ID del Incidente", incidente.getId());
        properties.put("Piso", incidente.getPiso());
        properties.put("Detalle", incidente.getDetalle());
        properties.put("Ubicación", incidente.getUbicacion());
        properties.put("Estado del Reporte", incidente.getEstadoReporte());
        properties.put("Descripción", incidente.getDescription());

        Mail mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail) // Ahora usa un solo String
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteCreatedEmpleadoTemplate", properties))
                .subject("Nuevo Incidente Creado")
                .build();

        this.mail = mail;
    }
}
