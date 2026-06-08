package microsim.data.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
	
	@Column(name="`value`", length=255)
	public String value;


	/**
	 * CONSTRUCTOR
	 */
	public ExperimentParameter(){}
}
