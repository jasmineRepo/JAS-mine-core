package microsim.data.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="jasmine_experiment_parameter")
public class ExperimentParameter {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "experiment_id")
	public Experiment experiment;
	
	@Column(name="name", length=255)
	public String name;
	
	@Column(name="value", length=255)
	public String value;
	
}
