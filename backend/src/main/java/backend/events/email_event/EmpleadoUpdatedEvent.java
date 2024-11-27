package backend.events.email_event;

import backend.empleado.domain.Empleado;
import backend.events.model.Mail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EmpleadoUpdatedEvent extends ApplicationEvent {

    private final Empleado empleado;
    private final Mail mail;

    public EmpleadoUpdatedEvent(Empleado empleado, String recipientEmail) {
        super(empleado);
        this.empleado = empleado;

        Map<String, Object> properties = new HashMap<>();
        properties.put("Nombre", empleado.getFirstName());
        properties.put("Apellido", empleado.getLastName());
        properties.put("Email", empleado.getEmail());
        properties.put("Teléfono", empleado.getPhoneNumber());
        properties.put("Horario de Trabajo", empleado.getHorarioDeTrabajo().toString());

        this.mail = Mail.builder()
                .from("fernando.munoz.p@utec.edu.pe")
                .to(recipientEmail)
                .subject("Actualización de Datos de Empleado")
                .htmlTemplate(new Mail.HtmlTemplate("ActualizacionEmpleadoTemplate", properties))
                .build();
    }
}
