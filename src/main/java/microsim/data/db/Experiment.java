package microsim.data.db;

import jakarta.persistence.*;
import lombok.NonNull;
import lombok.val;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jasmine_experiment")
public class Experiment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name = "time_stamp")
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date timestamp;

    @Column(name = "run_id")
    public String runId;

    @Column(name = "multi_run_id")
    public String multiRunId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "experiment", orphanRemoval = true, cascade = {CascadeType.ALL})
    @Fetch(FetchMode.SELECT)
    public List<ExperimentParameter> parameters;

    @Transient
    public String inputFolder = "input";

    @Transient
    public String outputRootFolder = "output";

    public @NonNull String getOutputFolder() {
        if (runId == null) {
            val sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            runId = sdf.format(new Date());
        }
        return outputRootFolder + File.separatorChar + runId;
    }
}
