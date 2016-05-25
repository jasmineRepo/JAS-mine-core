package microsim.data.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PanelEntityKey implements Serializable {

	private static final long serialVersionUID = -1264771886420608859L;

	@Column(name="id")
	private long id = 1L;
	
	@Column(name="simulation_time")
	private double simulationTime;
	
	@Column(name="simulation_run")
	private long simulationRun;

	public PanelEntityKey() {
		super();
	}
	
	public PanelEntityKey(long id) {
		super();
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(Double simulationTime) {
		this.simulationTime = simulationTime;
	}

	public long getSimulationRun() {
		return simulationRun;
	}

	public void setSimulationRun(Long simulationRun) {
		this.simulationRun = simulationRun;
	}
		
}
