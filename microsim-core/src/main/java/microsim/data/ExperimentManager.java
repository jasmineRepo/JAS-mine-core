package microsim.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;

import microsim.data.db.DatabaseUtils;
import microsim.data.db.Experiment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * Singleton. Utility used to create ana manage experiment setup.
 * It makes copies of input folder into output and create experiment run
 * record into output database.
 * 
 * @author Michele Sonnessa, edited by Ross Richardson
 *
 */
public class ExperimentManager {

	private static final Logger log = Logger.getLogger(ExperimentManager.class);
	
	/** The flag determines if the tool must copy input resources into output folder. */
	public boolean copyInputFolderStructure = true;
	
	/**	The flag determines if output database must automatically be created. */
	public boolean saveExperimentOnDatabase = true;
	
//	public String inputDatabaseName = "input.odb";

	private static ExperimentManager manager = null;

	/** The flag determines whether the input folder should remain constant for multiruns */
	public boolean isMultiRun = false;

	private ExperimentManager() {
		
	}
	
	public static ExperimentManager getInstance() {
		if (manager == null)
			manager = new ExperimentManager();
		return manager;
	}
	
	public Experiment createExperiment(String multiRunId) {
		Experiment experiment = new Experiment(multiRunId);
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
			
			
			if (!destFile.exists()) {
		        destFile.createNewFile();
		    }
	
		    FileChannel source = null;
		    FileChannel destination = null;
		    try {
		        source = new FileInputStream(sourceFile).getChannel();
		        destination = new FileOutputStream(destFile).getChannel();
	
		        // previous code: destination.transferFrom(source, 0, source.size());
		        // to avoid infinite loops, should be:
		        long count = 0;
		        long size = source.size();              
		        while((count += destination.transferFrom(source, count, size-count))<size);
		    }
		    finally {
		        if (source != null) {
		            source.close();
		        }
		        if (destination != null) {
		            destination.close();
		        }
		    }
		}		
	}

	
//	public static void expandODB(String odbFileName, String destinationFolder) {
//
//		byte[] buffer = new byte[1024];
//
//		try {
//
//			// create output directory is not exists
//			File folder = new File(destinationFolder);
//			if (!folder.exists()) {
//				folder.mkdir();
//			}
//
//			// get the zip file content
//			ZipInputStream zis = new ZipInputStream(new FileInputStream(odbFileName));
//			// get the zipped file list entry
//			ZipEntry ze = zis.getNextEntry();
//
//			while (ze != null) {
//
//				String fileName = ze.getName();
//				File newFile = new File(destinationFolder + File.separator + fileName);
//				if (newFile.getPath().startsWith(folder.getPath() + File.separator + "database")) {
//					log.debug("file unzip : " + newFile.getAbsoluteFile());
//
//					// create all non exists folders
//					// else you will hit FileNotFoundException for compressed folder
//					new File(newFile.getParent()).mkdirs();
//
//					FileOutputStream fos = new FileOutputStream(newFile.getParent() + File.separator + "database." + newFile.getName());
//
//					int len;
//					while ((len = zis.read(buffer)) > 0) {
//						fos.write(buffer, 0, len);
//					}
//
//					fos.close();					
//				}
//				
//				ze = zis.getNextEntry();
//			}
//
//			zis.closeEntry();
//			zis.close();
//
//			System.out.println("Done");
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}	
	
	public Experiment setupExperiment(Experiment experiment, Object... models) throws Exception {
		final String outFolder = experiment.getOutputFolder() + File.separator + "input";
		
		log.debug("Setting up experiment " + experiment.runId);
		
		if (copyInputFolderStructure) {
			log.debug("Copying folder structure");
			
//			File inputDBFile = new File(experiment.inputFolder + File.separator + inputDatabaseName);
//			if (inputDBFile.exists())
//				expandODB(experiment.inputFolder + File.separator + inputDatabaseName, outFolder);
			
			File inputDir = new File(experiment.inputFolder);
			if (inputDir.exists()) {
				String[] files = inputDir.list();
				for (String file : files) {
//					if (! file.equals("input.odb") && ! file.startsWith("."))
					//if (! file.startsWith(".")) {
					if (file.endsWith(".xlsx") || file.endsWith(".xls") || file.endsWith(".db")) {
						log.debug("Copying " + file + " to output folder");
						copy(inputDir + File.separator + file, outFolder);
					}
				}
			}
		}
		else if (isMultiRun) {
			log.info("Persisting database connection at: " + DatabaseUtils.databaseInputUrl);
		} else {
			 DatabaseUtils.databaseInputUrl = experiment.inputFolder + File.separator + "input";
		}
		
		if (saveExperimentOnDatabase) {
			log.debug("Creating experiment on output database");
			File dbFile = new File(experiment.getOutputFolder() + File.separator + "database");
			if (! dbFile.exists())
				dbFile.mkdir();
			
			if(copyInputFolderStructure) {
				DatabaseUtils.databaseInputUrl = outFolder + File.separator + "input";
			}
			DatabaseUtils.databaseOutputUrl = experiment.getOutputFolder() + File.separator + "database" + File.separator + "out";

			experiment = DatabaseUtils.createExperiment(DatabaseUtils.getOutEntityManger(), experiment, models);
			log.debug("Created experiment with id " + experiment.id);
		}
		return experiment;
	}
}
