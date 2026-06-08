package microsim.data.db;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CoefficientEntityKey implements Serializable {

	private static final long serialVersionUID = 7602166749723270873L;

	@Column(name="id")
	private Long id;
		
	@Column(name="simulation_run")
	private Long simulationRun;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSimulationRun() {
		return simulationRun;
	}

	public void setSimulationRun(Long simulationRun) {
		this.simulationRun = simulationRun;
	}
		
}
