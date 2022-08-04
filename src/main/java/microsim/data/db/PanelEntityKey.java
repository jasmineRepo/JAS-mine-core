package microsim.data.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable public class PanelEntityKey implements Serializable {

	@Serial private static final long serialVersionUID = -1264771886420608859L;

	@Column(name="id") @Setter @Getter private long id = 1L;
	
	@Column(name="simulation_time") @Setter @Getter private double simulationTime;
	
	@Column(name="simulation_run") @Setter @Getter private long simulationRun;

	public PanelEntityKey() {
		super();
	}
	
	public PanelEntityKey(long id) {
		super();
		this.id = id;
	}
}
