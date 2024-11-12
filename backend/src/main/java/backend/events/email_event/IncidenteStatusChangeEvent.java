package backend.events.email_event;

import backend.events.model.Mail;
import backend.incidente.domain.Incidente;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class IncidenteStatusChangeEvent extends ApplicationEvent {

    private final Incidente incidente;
    private final Mail mail;

    public IncidenteStatusChangeEvent(Incidente incidente, String recipientEmail) {
        super(incidente);
        this.incidente = incidente;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("ID del Incidente", incidente.getId());
        properties.put("Estado del Reporte", incidente.getEstadoReporte());
        properties.put("Estado de la Tarea", incidente.getEstadoTarea());
        properties.put("Descripción", incidente.getDescription());

        Mail mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteStatusChangeTemplate", properties))
                .subject("Actualización de Estado del Incidente")
                .build();

        this.mail = mail;
    }
}
