package microsim.exception;

import lombok.NonNull;

import java.io.Serial;

public class SimulationRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -645494861634821526L;

    public SimulationRuntimeException() {
        super();
    }

    public SimulationRuntimeException(final @NonNull String message, final @NonNull Throwable cause) {
        super(message, cause);
    }

    public SimulationRuntimeException(final @NonNull String message) {
        super(message);
    }

    public SimulationRuntimeException(final @NonNull Throwable cause) {
        super(cause);
    }

}
