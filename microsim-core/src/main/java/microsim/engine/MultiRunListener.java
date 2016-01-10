package microsim.engine;

public interface MultiRunListener {
	
	void beforeSimulationStart(SimulationEngine engine);
	
	void afterSimulationCompleted(SimulationEngine engine);
	
}
