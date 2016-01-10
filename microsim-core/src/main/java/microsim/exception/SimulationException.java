package microsim.exception;

public class SimulationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SimulationException() {
		super();	
	}

	public SimulationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimulationException(String message) {
		super(message);
	}

	public SimulationException(Throwable cause) {
		super(cause);
	}

	

}
