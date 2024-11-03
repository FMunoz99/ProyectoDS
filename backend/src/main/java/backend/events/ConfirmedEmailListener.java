package backend.events;

import backend.events.domain.EmailSenderService;
import backend.events.email_event.IncidenteCreatedEvent;
import backend.events.email_event.IncidenteStatusChangeEvent;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConfirmedEmailListener {

    final private EmailSenderService emailSenderService;

    @Autowired
    public ConfirmedEmailListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    /*---- INCIDENTE ---- */

    @Async
    @EventListener
    public void handleIncidenteStatusChangeEvent(IncidenteStatusChangeEvent event) throws MessagingException, IOException {
        emailSenderService.sendEmail(event.getMail());
    }

    @Async
    @EventListener
    public void handleIncidenteCreatedEvent(IncidenteCreatedEvent event) throws MessagingException, IOException {
        emailSenderService.sendEmail(event.getMail());
    }

    /* ------------------ */
}
