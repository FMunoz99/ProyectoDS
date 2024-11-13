package backend.events.email_event;

import backend.events.model.Mail;
import backend.objetoPerdido.domain.ObjetoPerdido;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ObjetoPerdidoStatusChangeEvent extends ApplicationEvent {

    private final ObjetoPerdido objetoPerdido;
    private final Mail mail;

    public ObjetoPerdidoStatusChangeEvent(ObjetoPerdido objetoPerdido, String recipientEmail) {
        super(objetoPerdido);
        this.objetoPerdido = objetoPerdido;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("ID del Objeto Perdido", objetoPerdido.getId());
        properties.put("Estado del Reporte", objetoPerdido.getEstadoReporte());
        properties.put("Estado de la Tarea", objetoPerdido.getEstadoTarea());
        properties.put("Descripcion", objetoPerdido.getDescription());

        Mail mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("ObjetoPerdidoStatusChangeTemplate", properties))
                .subject("Cambio de Estado del Objeto Perdido")
                .build();

        this.mail = mail;
    }
}
