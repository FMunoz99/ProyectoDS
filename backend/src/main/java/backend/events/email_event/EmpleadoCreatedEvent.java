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

        String Nombre= empleado.getFirstName() + " " + empleado.getLastName();
        String Email = empleado.getEmail();

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", Nombre);
        properties.put("Email", Email);
        properties.put("updatedAt", empleado.getUpdatedAt().toString()); // Fecha de actualización
        properties.put("Mensaje", "Bienvenido a nuestro equipo");

        this.mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("BienvenidaTemplate", properties))
                .subject("Bienvenido a la plataforma")
                .build();
    }
}
