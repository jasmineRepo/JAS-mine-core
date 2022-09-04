package microsim.data.db;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
public class CoefficientEntityKey implements Serializable {

	@Serial private static final long serialVersionUID = 7602166749723270873L;

	@Column(name="id") @Setter @Getter private Long id;

	@Column(name="simulation_run") @Setter @Getter private Long simulationRun;
}
