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

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("ID del Incidente", incidente.getId());
        properties.put("Piso", incidente.getPiso());
        properties.put("Detalle", incidente.getDetalle());
        properties.put("Ubicación", incidente.getUbicacion());
        properties.put("Estado del Reporte", incidente.getEstadoReporte());
        properties.put("Descripción", incidente.getDescription());

        // Crear el objeto Mail con un solo destinatario
        Mail mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail) // Usar un solo correo
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteCreatedTemplate", properties))
                .subject("Nuevo Incidente Creado")
                .build();

        this.mail = mail;
    }
}
