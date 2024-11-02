package backend.estudiante.exceptions;

public class UnauthorizeOperationException extends RuntimeException {
    public UnauthorizeOperationException(String message) {
        super(message);
    }
}
