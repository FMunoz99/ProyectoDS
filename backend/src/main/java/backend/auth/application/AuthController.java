package backend.auth.application;

import backend.auth.domain.AuthService;
import backend.auth.dto.AuthResponseDto;
import backend.auth.dto.LoginRequestDto;
import backend.auth.dto.RegisterRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    final private AuthService authService;
    final private JdbcTemplate jdbcTemplate;

    public AuthController(AuthService authService, JdbcTemplate jdbcTemplate) {
        this.authService = authService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        System.out.println("Logeado");
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }

    @GetMapping("/test/connection")
    public ResponseEntity<String> testConnection() {
        try{
            jdbcTemplate.execute("SELECT 1");
            return ResponseEntity.ok("La conexión a la base de datos PostgreSQL fue exitosa");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al conectarse a la base de datos PostgreSQL: " + e.getMessage());
        }
    }
}
