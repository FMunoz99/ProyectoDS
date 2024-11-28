package backend.events.email_event;

import backend.events.model.Mail;
import backend.incidente.domain.EstadoReporte;
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
        properties.put("IDObjetoPerdido", incidente.getId());
        properties.put("NombreAlumno", incidente.getEstudiante().getFirstName() + " " + incidente.getEstudiante().getLastName());
        properties.put("EstadoReporte", incidente.getEstadoReporte());
        properties.put("EstadoTarea", incidente.getEstadoTarea());
        properties.put("Descripcion", incidente.getDescription());
        properties.put("imageUrlReporte", getImageUrlForReporte(incidente.getEstadoReporte()));

        Mail mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("IncidenteStatusChangeTemplate", properties))
                .subject("Actualización de Estado del Incidente")
                .build();

        this.mail = mail;
    }

    private String getImageUrlForReporte(EstadoReporte reporte) {
        switch (reporte) {
            case ACEPTADO:
                return "https://st.depositphotos.com/2398103/4941/v/950/depositphotos_49417305-stock-illustration-vector-accepted-stamp.jpg"; // Imagen para "ACEPTADO"
            case RECHAZADO:
                return "https://static.vecteezy.com/system/resources/previews/008/490/263/non_2x/rejected-stamp-mark-clipart-free-png.png"; // Imagen para "RECHAZADO"
            default:
                return "https://st3.depositphotos.com/2274151/13456/v/450/depositphotos_134568484-stock-illustration-pending-stamp-sign-seal.jpg"; // Imagen por defecto
        }
    }
}
