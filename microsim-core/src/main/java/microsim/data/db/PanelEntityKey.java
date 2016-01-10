package microsim.data.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PanelEntityKey implements Serializable {

	private static final long serialVersionUID = -1264771886420608859L;

	@Column(name="id")
	private Long id;
	
	@Column(name="simulation_time")
	private Double simulationTime;
	
	@Column(name="simulation_run")
	private Long simulationRun;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(Double simulationTime) {
		this.simulationTime = simulationTime;
	}

	public Long getSimulationRun() {
		return simulationRun;
	}

	public void setSimulationRun(Long simulationRun) {
		this.simulationRun = simulationRun;
	}
		
}
