package backend.events.email_event;

import backend.events.model.Mail;
import backend.incidente.domain.EstadoReporte;
import backend.incidente.domain.EstadoTarea;
import backend.objetoPerdido.domain.ObjetoPerdido;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

import static backend.incidente.domain.EstadoReporte.ACEPTADO;
import static backend.incidente.domain.EstadoReporte.RECHAZADO;

@Getter
public class ObjetoPerdidoStatusChangeEvent extends ApplicationEvent {

    private final ObjetoPerdido objetoPerdido;
    private final Mail mail;

    public ObjetoPerdidoStatusChangeEvent(ObjetoPerdido objetoPerdido, String recipientEmail) {
        super(objetoPerdido);
        this.objetoPerdido = objetoPerdido;

        // Configuración del correo electrónico
        Map<String, Object> properties = new HashMap<>();
        properties.put("IDObjetoPerdido", objetoPerdido.getId());
        properties.put("EstadoReporte", objetoPerdido.getEstadoReporte());
        properties.put("EstadoTarea", objetoPerdido.getEstadoTarea());
        properties.put("Descripcion", objetoPerdido.getDescription());
        properties.put("imageUrlReporte", getImageUrlForReporte(objetoPerdido.getEstadoReporte()));

        Mail mail = Mail.builder()
                .from("Lost&Found")
                .to(recipientEmail)
                .htmlTemplate(new Mail.HtmlTemplate("ObjetoPerdidoStatusChangeTemplate", properties))
                .subject("Cambio de Estado del Objeto Perdido")
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
