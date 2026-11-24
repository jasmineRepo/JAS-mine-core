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

        void editValue(Experiment experiment, double time, int newValue) throws Exception {
            var before = GUIParameterHistory.capture(this);
            value = newValue;
            var changes = GUIParameterHistory.changedValues(before, GUIParameterHistory.capture(this));
            GUIParameterHistory.appendChanges(Path.of(experiment.getOutputFolder()).toFile(), time, changes);
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
        assertTrue(Files.exists(Path.of(next.getOutputFolder(), "GUIparameters.csv")));
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
        ModelFixture firstModel = new ModelFixture(1);
        first = manager.setupExperiment(first, firstModel);
        firstModel.editValue(first, 2027.0, 11);

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
        ModelFixture secondModel = new ModelFixture(2);
        second = manager.setupExperiment(second, secondModel);
        secondModel.editValue(second, 2028.0, 22);

        String component = ModelFixture.class.getCanonicalName();
        assertEquals(List.of(
                GUIParameterHistory.HEADER,
                "0.0," + component + ",value,1",
                "2027.0," + component + ",value,11"),
                Files.readAllLines(firstRunDirectory.resolve(GUIParameterHistory.FILE_NAME)));
        assertEquals(List.of(
                GUIParameterHistory.HEADER,
                "0.0," + component + ",value,2",
                "2028.0," + component + ",value,22"),
                Files.readAllLines(secondRunDirectory.resolve(GUIParameterHistory.FILE_NAME)));

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

    @Test
    void initialParameterHistoryIsWrittenWhenDatabasePersistenceIsDisabled() throws Exception {
        manager.saveExperimentOnDatabase = false;
        manager.copyInputFolderStructure = false;
        Path runDirectory = temporaryDirectory.resolve("output/no-database");

        Experiment experiment = manager.createExperiment(null);
        experiment.runId = "no-database";
        experiment.setOutputFolder(runDirectory.toString());
        experiment = manager.setupExperiment(experiment, new ModelFixture(7));

        assertNull(experiment.id);
        assertFalse(DatabaseUtils.isOutputInitialized());
        assertEquals(List.of(
                GUIParameterHistory.HEADER,
                "0.0," + ModelFixture.class.getCanonicalName() + ",value,7"),
                Files.readAllLines(runDirectory.resolve(GUIParameterHistory.FILE_NAME)));
    }
}
