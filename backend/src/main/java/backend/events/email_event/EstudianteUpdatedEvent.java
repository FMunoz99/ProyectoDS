package backend.events.email_event;

import backend.estudiante.domain.Estudiante;
import backend.events.model.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EstudianteUpdatedEvent extends ApplicationEvent {

    private final Estudiante estudiante;
    private final Mail mail;

    public EstudianteUpdatedEvent(Estudiante estudiante, String recipientEmail) {
        super(estudiante);
        this.estudiante = estudiante;

        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", estudiante.getFirstName());
        properties.put("Apellido", estudiante.getLastName());
        properties.put("Email", estudiante.getEmail());
        properties.put("Teléfono", estudiante.getPhoneNumber());

        this.mail = Mail.builder()
                .from("notificaciones@miapp.com")
                .to(recipientEmail)
                .subject("Actualización de Datos de Estudiante")
                .htmlTemplate(new Mail.HtmlTemplate("EstudianteUpdatedTemplate", properties))
                .build();
    }
}
