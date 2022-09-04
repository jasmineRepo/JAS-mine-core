package microsim.exception;

public class SimulationRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SimulationRuntimeException() {
		super();
	}

	public SimulationRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimulationRuntimeException(String message) {
		super(message);
	}

	public SimulationRuntimeException(Throwable cause) {
		super(cause);
	}



}
