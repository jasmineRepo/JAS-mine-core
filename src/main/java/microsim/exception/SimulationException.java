package microsim.exception;

import lombok.NonNull;

import java.io.Serial;

public class SimulationException extends Exception {

    @Serial
    private static final long serialVersionUID = -3795292486757853837L;

    public SimulationException() {
        super();
    }

    public SimulationException(final @NonNull String message, final @NonNull Throwable cause) {
        super(message, cause);
    }

    public SimulationException(final @NonNull String message) {
        super(message);
    }

    public SimulationException(final @NonNull Throwable cause) {
        super(cause);
    }


}
