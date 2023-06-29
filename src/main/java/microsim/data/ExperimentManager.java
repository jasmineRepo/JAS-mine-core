package microsim.data;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;
import microsim.data.db.DatabaseUtils;
import microsim.data.db.Experiment;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Singleton. Utility used to create ana manage experiment setup. It makes copies of input folder into output and create
 * experiment run record into output database.
 */
@Log
public class ExperimentManager {

    private static ExperimentManager manager;

    /**
     * The flag determines if the tool must copy input resources into output folder.
     */
    public boolean copyInputFolderStructure = true;

    /**
     * The flag determines if output database must automatically be created.
     */
    public boolean saveExperimentToDatabase = true;

    /**
     * Checks {@code manager}, if {@code null}, generates a new instance of {@link ExperimentManager}.
     *
     * @return an instance of {@link ExperimentManager}.
     */
    public static @NonNull ExperimentManager getInstance() {
        manager = manager == null ? new ExperimentManager() : manager;
        return manager;
    }

    /**
     * Creates a new instance of {@link Experiment} with the provided id; uses current time as the timestamp.
     *
     * @param multiRunId The experiment id.
     * @return an instance of {@link Experiment}.
     */
    public @NonNull Experiment createExperiment(final @NonNull String multiRunId) {
        val experiment = new Experiment();
        experiment.multiRunId = multiRunId;
        experiment.timestamp = new Date(System.currentTimeMillis());

        return experiment;
    }

    /**
     * Walks recursively over all files in the directory and copies them to a new destination. It the output exists
     * overrides it. Ignores files and folders starting with dots.
     *
     * @param fileName  The source file/directory
     * @param outFolder The destination folder.
     * @throws IOException when something goes wrong (permissions / lack of space).
     */
    public void copy(final @NonNull String fileName, final @NonNull String outFolder) throws IOException {
        val source = new File(fileName).toPath();
        val output = new File(outFolder).toPath();

        Files.walkFileTree(source, new SimpleFileVisitor<>() {

            /**
             * When a directory is encountered creates the corresponding one in the target directory.
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.startsWith("."))
                    Files.createDirectories(output.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            /**
             * When a regular file is encountered copies it.
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.startsWith("."))
                    Files.copy(file, output.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public @NonNull Experiment setupExperiment(@NonNull Experiment experiment,
                                               final @NonNull Object... models) throws Exception {
        val outFolder = experiment.getOutputFolder() + File.separator + "input";

        log.log(Level.INFO, "Setting up experiment " + experiment.runId);

        if (copyInputFolderStructure) {
            log.log(Level.INFO, "Copying folder structure");
            copy(experiment.inputFolder, outFolder);
        } else DatabaseUtils.databaseInputUrl = experiment.inputFolder + File.separator + "input";

        if (saveExperimentToDatabase) {
            log.log(Level.INFO, "Creating experiment on output database");
            val dbFile = new File(experiment.getOutputFolder() + File.separator + "database");
            if (!dbFile.exists()) dbFile.mkdir();

            if (copyInputFolderStructure) DatabaseUtils.databaseInputUrl = outFolder + File.separator + "input";
            DatabaseUtils.databaseOutputUrl = experiment.getOutputFolder() + File.separator + "database" + File.separator + "out";

            experiment = DatabaseUtils.createExperiment(Objects.requireNonNull(DatabaseUtils.getOutEntityManger()),
                experiment, models);
            log.log(Level.INFO, "Created experiment with id " + experiment.id);
        }
        return experiment;
    }
}
