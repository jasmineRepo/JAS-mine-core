package microsim.data;

import lombok.extern.java.Log;
import microsim.data.db.DatabaseUtils;
import microsim.data.db.Experiment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Singleton. Utility used to create ana manage experiment setup.
 * It makes copies of input folder into output and create experiment run
 * record into output database.
 *
 * @author Michele Sonnessa, edited by Ross Richardson
 *
 */
@Log public class ExperimentManager {

	/** The flag determines if the tool must copy input resources into output folder. */
	public boolean copyInputFolderStructure = true;

	/**	The flag determines if output database must automatically be created. */
	public boolean saveExperimentOnDatabase = true;

	private static ExperimentManager manager = null;

	private ExperimentManager() {

	}

	public static ExperimentManager getInstance() {
		if (manager == null)
			manager = new ExperimentManager();
		return manager;
	}

	public Experiment createExperiment(String multiRunId) {
		Experiment experiment = new Experiment();
		experiment.multiRunId = multiRunId;
		experiment.timestamp = new Date(System.currentTimeMillis());

		return experiment;
	}

	@SuppressWarnings("resource")
	public void copy(String fileName, String outFolder) throws Exception {
		File sourceFile = new File(fileName);
		File outDir = new File(outFolder);
		File destFile = new File(outFolder + File.separator + sourceFile.getName());
		if(sourceFile.isDirectory()) {
			FileUtils.copyDirectory(sourceFile, destFile);		//Now use Apache Commons IO
		}
		else {
			if (! outDir.exists())
				outDir.mkdirs();


			if (!destFile.exists())
		        destFile.createNewFile();

			try (FileChannel source = new FileInputStream(sourceFile).getChannel();
				 FileChannel destination = new FileOutputStream(destFile).getChannel()) {

				// previous code: destination.transferFrom(source, 0, source.size());
				// to avoid infinite loops, should be:
				long count = 0;
				long size = source.size();
				while ((count += destination.transferFrom(source, count, size - count)) < size) ;
			}
		}
	}

	public Experiment setupExperiment(Experiment experiment, Object... models) throws Exception {
		final String outFolder = experiment.getOutputFolder() + File.separator + "input";

		log.log(Level.INFO, "Setting up experiment " + experiment.runId);

		if (copyInputFolderStructure) {
			log.log(Level.INFO, "Copying folder structure");

			File inputDir = new File(experiment.inputFolder);
			if (inputDir.exists()) {
				String[] files = inputDir.list();
				for (String file : Objects.requireNonNull(files)) {
					if (! file.startsWith(".")) {
						log.log(Level.INFO, "Copying " + file + " to output folder");
						copy(inputDir + File.separator + file, outFolder);
					}
				}
			}
		}
		else{
			 DatabaseUtils.databaseInputUrl = experiment.inputFolder + File.separator + "input";
		}

		if (saveExperimentOnDatabase) {
			log.log(Level.INFO, "Creating experiment on output database");
			File dbFile = new File(experiment.getOutputFolder() + File.separator + "database");
			if (! dbFile.exists())
				dbFile.mkdir();

			if(copyInputFolderStructure) {
				DatabaseUtils.databaseInputUrl = outFolder + File.separator + "input";
			}
			DatabaseUtils.databaseOutputUrl = experiment.getOutputFolder() + File.separator + "database" + File.separator + "out";

			experiment = DatabaseUtils.createExperiment(DatabaseUtils.getOutEntityManger(), experiment, models);
			log.log(Level.INFO, "Created experiment with id " + experiment.id);
		}
		return experiment;
	}
}
