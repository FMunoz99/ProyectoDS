package backend.events.email_event;

import backend.estudiante.domain.Estudiante;
import backend.events.model.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EstudianteCreatedEvent extends ApplicationEvent {

    private final Estudiante estudiante;
    private final Mail mail;

    public EstudianteCreatedEvent(Estudiante estudiante, String recipientEmail) {
        super(estudiante);
        this.estudiante = estudiante;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", estudiante.getFirstName() + " " + estudiante.getLastName());
        properties.put("Email", estudiante.getEmail());
        properties.put("Mensaje", "Bienvenido a nuestra plataforma");

        this.mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("BienvenidaTemplate", properties))
                .subject("Bienvenido a la plataforma")
                .build();
    }
}
