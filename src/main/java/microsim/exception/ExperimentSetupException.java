/* (C) Copyright 2026, by Ross Richardson
 * Fail-closed experiment initialization error with private diagnostic cause.
 * @author ross richardson
 */
package microsim.exception;

public final class ExperimentSetupException extends SimulationRuntimeException {
    private static final long serialVersionUID = 1L;

    public ExperimentSetupException(Exception cause) {
        super("Experiment input or output initialization failed. The simulation was not built.", cause);
    }
}
