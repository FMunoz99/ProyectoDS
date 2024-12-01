package backend.admin.component;

import backend.incidente.infrastructure.IncidenteRepository;
import backend.objetoPerdido.infrastructure.ObjetoPerdidoRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ReportCounter {

    @Getter
    private final ConcurrentHashMap<String, AtomicInteger> incidentesPorDia = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<String, AtomicInteger> objetosPerdidosPorDia = new ConcurrentHashMap<>();

    @Autowired
    private IncidenteRepository incidenteRepository;

    @Autowired
    private ObjetoPerdidoRepository objetoPerdidoRepository;

    @PostConstruct
    public void inicializarContadores() {
        // Contar incidentes existentes
        incidenteRepository.findAll().forEach(incidente -> {
            String fecha = incidente.getFechaReporte().toString();
            incidentesPorDia.computeIfAbsent(fecha, k -> new AtomicInteger(0)).incrementAndGet();
        });

        // Contar objetos perdidos existentes
        objetoPerdidoRepository.findAll().forEach(objetoPerdido -> {
            String fecha = objetoPerdido.getFechaReporte().toString();
            objetosPerdidosPorDia.computeIfAbsent(fecha, k -> new AtomicInteger(0)).incrementAndGet();
        });
    }

    public void incrementarIncidentes(String fecha) {
        incidentesPorDia.computeIfAbsent(fecha, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void incrementarObjetosPerdidos(String fecha) {
        objetosPerdidosPorDia.computeIfAbsent(fecha, k -> new AtomicInteger(0)).incrementAndGet();
    }

}
