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

    public EstudianteUpdatedEvent(Estudiante estudiante, Map<String, String> updatedFields, String recipientEmail) {
        super(estudiante);
        this.estudiante = estudiante;

        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", estudiante.getFirstName() + " " + estudiante.getLastName());
        properties.put("updatedFields", updatedFields);
        properties.put("updatedAt", estudiante.getUpdatedAt().toString()); // Fecha de actualización

        this.mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("ActualizacionPerfilTemplate", properties))
                .subject("Actualización de Datos")
                .build();
    }
}
