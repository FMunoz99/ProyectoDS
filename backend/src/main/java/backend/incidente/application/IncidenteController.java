package backend.incidente.application;

import backend.incidente.domain.IncidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidente")
public class IncidenteController {

    final private IncidenteService incidenteService;

    @Autowired
    public IncidenteController(IncidenteService incidenteService) {
        this.incidenteService = incidenteService;
    }
}
