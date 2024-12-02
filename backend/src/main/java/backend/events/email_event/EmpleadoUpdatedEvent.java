package backend.events.email_event;

import backend.empleado.domain.Empleado;
import backend.events.model.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EmpleadoUpdatedEvent extends ApplicationEvent {

    private final Mail mail;
    private final Empleado empleado;

    public EmpleadoUpdatedEvent(Empleado empleado, Map<String, Object> updatedFields, String recipientEmail) {
        super(empleado);
        this.empleado = empleado;

        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", empleado.getFirstName() + " " + empleado.getLastName());
        properties.put("updatedFields", updatedFields);
        properties.put("updatedAt", empleado.getUpdatedAt().toString()); // Fecha de actualización

        this.mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("ActualizacionPerfilTemplate", properties))
                .subject("Actualización de Datos de Empleado")
                .build();
    }
}
