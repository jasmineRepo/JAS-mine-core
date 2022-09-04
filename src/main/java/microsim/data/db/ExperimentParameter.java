package microsim.data.db;

import jakarta.persistence.*;

@Entity @Table(name="jasmine_experiment_parameter") public class ExperimentParameter {

	@Id @Column(name="id") @GeneratedValue(strategy=GenerationType.AUTO) public Long id;

	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name = "experiment_id") public Experiment experiment;

	@Column(name="name") public String name;

	@Column(name="value") public String value;
}
