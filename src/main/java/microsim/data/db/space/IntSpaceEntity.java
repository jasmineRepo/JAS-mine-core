package microsim.data.db.space;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class IntSpaceEntity implements IIntSpaceEntity {

	private Double simulationTime;
	
	private Long simulationRun;
	
	private Integer x;
	
	private Integer y;
	
	private Integer value;

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

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
