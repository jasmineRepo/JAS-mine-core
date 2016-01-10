package microsim.data.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="jas_experiment")
public class Experiment {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(name="time_stamp")
	@Temporal(value=TemporalType.TIMESTAMP)
	public Date timestamp;
	
	@Column(name="run_id")
	public String runId;
	
	@Column(name="multi_run_id")
	public String multiRunId;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "experiment", orphanRemoval = true, cascade={CascadeType.ALL})
	@Fetch(FetchMode.SELECT)
	public List<ExperimentParameter> parameters;
	
	@Transient
	public String inputFolder = "input/";
	
	@Transient
	public String outputRootFolder = "output/";	
	
	public String getOutputFolder() {
		if (runId == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			runId = sdf.format(new Date());
		}
		return outputRootFolder + File.separatorChar + runId;		
	}
	
}
