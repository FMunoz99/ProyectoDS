package backend.events.email_event;

import backend.empleado.domain.Empleado;
import backend.events.model.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EmpleadoCreatedEvent extends ApplicationEvent {

    private final Empleado empleado;
    private final Mail mail;

    public EmpleadoCreatedEvent(Empleado empleado, String recipientEmail) {
        super(empleado);
        this.empleado = empleado;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", empleado.getFirstName() + " " + empleado.getLastName());
        properties.put("Email", empleado.getEmail());
        properties.put("Mensaje", "Bienvenido a nuestra plataforma");

        this.mail = Mail.builder()
                .from("notificaciones@miapp.com")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("EmpleadoCreatedTemplate", properties))
                .subject("Bienvenido a la plataforma")
                .build();
    }
}
