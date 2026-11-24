/* (C) Copyright 2026, by Ross Richardson
 *
 * Experiment Manager Lifecycle Test.
 *
 * @author ross richardson
 *
 */

package microsim.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.persistence.EntityManager;
import microsim.annotation.GUIparameter;
import microsim.data.db.DatabaseUtils;
import microsim.data.db.Experiment;

class ExperimentManagerLifecycleTest {

    static class ModelFixture {
        @GUIparameter
        int value;

        ModelFixture(int value) {
            this.value = value;
        }
    }

    @TempDir
    Path temporaryDirectory;

    private final ExperimentManager manager = ExperimentManager.getInstance();
    private boolean originalCopyInputFolderStructure;
    private boolean originalSaveExperimentOnDatabase;
    private boolean originalMultiRun;
    private String originalInputFolder;
    private String originalOutputRootFolder;
    private String originalTestOutputFolder;
    private String originalInputUrl;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseUtils.closeOutputEntityManagerFactory();
        originalCopyInputFolderStructure = manager.copyInputFolderStructure;
        originalSaveExperimentOnDatabase = manager.saveExperimentOnDatabase;
        originalMultiRun = manager.isMultiRun;
        originalInputFolder = Experiment.inputFolder;
        originalOutputRootFolder = Experiment.outputRootFolder;
        originalTestOutputFolder = Experiment.testOutputFolder;
        originalInputUrl = DatabaseUtils.databaseInputUrl;

        Path inputDirectory = temporaryDirectory.resolve("input");
        Files.createDirectories(inputDirectory);
        Experiment.inputFolder = inputDirectory.toString();
        Experiment.outputRootFolder = temporaryDirectory.resolve("output").toString();
        Experiment.testOutputFolder = null;
        manager.copyInputFolderStructure = true;
        manager.saveExperimentOnDatabase = true;
        manager.isMultiRun = false;
    }

    @AfterEach
    void tearDown() {
        DatabaseUtils.closeOutputEntityManagerFactory();
        manager.copyInputFolderStructure = originalCopyInputFolderStructure;
        manager.saveExperimentOnDatabase = originalSaveExperimentOnDatabase;
        manager.isMultiRun = originalMultiRun;
        Experiment.inputFolder = originalInputFolder;
        Experiment.outputRootFolder = originalOutputRootFolder;
        Experiment.testOutputFolder = originalTestOutputFolder;
        DatabaseUtils.databaseInputUrl = originalInputUrl;
        ExportCSV.directory = null;
    }

    @Test
    void generatedRunNamesRemainCompatibleAndNeverReuseAnExistingDirectory() throws Exception {
        manager.saveExperimentOnDatabase = false;
        manager.copyInputFolderStructure = false;
        var names = new java.util.HashSet<String>();
        for (int i = 0; i < 1000; i++) {
            Experiment experiment = manager.createExperiment("606_0");
            assertTrue(names.add(experiment.getOutputFolder()));
            assertTrue(experiment.runId.matches("[0-9]{14}-[0-9]+"));
            assertTrue(experiment.getOutputFolder().endsWith("_606_0"));
        }
        Experiment collision = manager.createExperiment(null);
        Path folder = Path.of(collision.getOutputFolder());
        Files.createDirectories(folder);
        Path sentinel = folder.resolve("GUIparameters.csv");
        Files.writeString(sentinel, "earlier run must survive");
        assertThrows(java.nio.file.FileAlreadyExistsException.class,
                () -> manager.setupExperiment(collision, new ModelFixture(8)));
        assertEquals("earlier run must survive", Files.readString(sentinel));
        Experiment next = manager.createExperiment(null);
        manager.setupExperiment(next, new ModelFixture(9));
        assertTrue(Files.isDirectory(Path.of(next.getOutputFolder())));
    }

    @Test
    void repeatedBuildsShareDatabaseAndKeepPerRunOutputDirectories() throws Exception {
        Path firstRunDirectory = temporaryDirectory.resolve("output/run-a");
        Path secondRunDirectory = temporaryDirectory.resolve("output/run-b");
        Files.createDirectories(firstRunDirectory);
        Files.createDirectories(secondRunDirectory);

        Experiment first = manager.createExperiment(null);
        first.runId = "run-a";
        first.setOutputFolder(firstRunDirectory.toString());
        first = manager.setupExperiment(first, new ModelFixture(1));

        String sessionDatabaseUrl = DatabaseUtils.databaseOutputUrl;
        assertNotNull(first.id);
        assertEquals(firstRunDirectory.toString(), first.getOutputFolder());
        assertEquals(firstRunDirectory.resolve("csv").toString(), ExportCSV.directory);
        assertEquals(firstRunDirectory.resolve("input/input").toString(), DatabaseUtils.databaseInputUrl);
        assertEquals(firstRunDirectory.resolve("database/out").toString(), sessionDatabaseUrl);
        assertTrue(DatabaseUtils.isOutputInitialized());
        assertTrue(Files.exists(Path.of(sessionDatabaseUrl + ".mv.db")));

        Experiment second = manager.createExperiment(null);
        second.runId = "run-b";
        second.setOutputFolder(secondRunDirectory.toString());
        second = manager.setupExperiment(second, new ModelFixture(2));

        assertNotNull(second.id);
        assertNotEquals(first.id, second.id);
        assertEquals(secondRunDirectory.toString(), second.getOutputFolder());
        assertEquals(secondRunDirectory.resolve("csv").toString(), ExportCSV.directory);
        assertEquals(secondRunDirectory.resolve("input/input").toString(), DatabaseUtils.databaseInputUrl);
        assertEquals(sessionDatabaseUrl, DatabaseUtils.databaseOutputUrl);
        assertFalse(Files.exists(secondRunDirectory.resolve("database")));

        EntityManager entityManager = DatabaseUtils.getOutEntityManger();
        try {
            List<String> runIds = entityManager.createQuery(
                    "select experiment.runId from Experiment experiment order by experiment.id", String.class)
                    .getResultList();
            assertEquals(List.of("run-a", "run-b"), runIds);
        } finally {
            entityManager.close();
        }

        DatabaseUtils.closeEntityManagerFactories();
        DatabaseUtils.closeEntityManagerFactories();

        assertFalse(DatabaseUtils.isOutputInitialized());
        assertNull(DatabaseUtils.databaseOutputUrl);
        assertNull(DatabaseUtils.databaseInputUrl);
    }
}
