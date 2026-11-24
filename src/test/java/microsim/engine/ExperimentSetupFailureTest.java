/* (C) Copyright 2026, by Ross Richardson
 * Failed experiment setup cannot produce a runnable model or mask its cause.
 * @author ross richardson
 */
package microsim.engine;

import microsim.data.ExperimentManager;
import microsim.data.db.DatabaseUtils;
import microsim.data.db.Experiment;
import microsim.exception.ExperimentSetupException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ExperimentSetupFailureTest {
    @TempDir Path directory;

    static class Fixture extends AbstractSimulationManager {
        final String id;
        final boolean failDisposal;
        boolean built, disposed;
        Fixture(String id, boolean failDisposal) { this.id=id; this.failDisposal=failDisposal; }
        public String getId() { return id; }
        public void buildObjects() { built=true; }
        public void buildSchedule() { built=true; }
        public void dispose() {
            disposed=true;
            if (failDisposal) throw new IllegalStateException("fictional cleanup failure");
            super.dispose();
        }
    }

    @Test
    void nativeSetupFailureRetainsPrivateQuotaCauseWithoutExposingItInPublicMessage() {
        String privatePath="/fictional/private/inputs/fixture.xlsx";
        var failure=new ExperimentSetupException(new IOException("Input copy failed",
                new IOException("No space left on device: " + privatePath)));
        assertFalse(failure.getMessage().contains(privatePath));
        assertFalse(failure.getMessage().contains("No space left on device"));
        var stdout=new ByteArrayOutputStream();
        var stderr=new ByteArrayOutputStream();
        PrintStream originalOut=System.out, originalErr=System.err;
        try (var out=new PrintStream(stdout, true, StandardCharsets.UTF_8);
             var err=new PrintStream(stderr, true, StandardCharsets.UTF_8)) {
            System.setOut(out);
            System.setErr(err);
            MultiRun.reportRunFailure(failure);
            assertEquals("Run failed: " + failure.getMessage() + System.lineSeparator(),
                    stdout.toString(StandardCharsets.UTF_8));
            String diagnostics=stderr.toString(StandardCharsets.UTF_8);
            assertTrue(diagnostics.contains("Caused by: java.io.IOException: Input copy failed"));
            assertTrue(diagnostics.contains("No space left on device: " + privatePath));

            stderr.reset();
            MultiRun.reportRunFailure(new IllegalStateException("ordinary failure"));
            assertEquals("", stderr.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    void failedInputCopyStopsBuildDisposesEveryManagerAndPreservesOriginalCause() throws Exception {
        var manager = ExperimentManager.getInstance();
        boolean copy=manager.copyInputFolderStructure, save=manager.saveExperimentOnDatabase;
        String input=Experiment.inputFolder, output=Experiment.testOutputFolder;
        var engine = new SimulationEngine();
        try {
            Path inputs=Files.createDirectory(directory.resolve("input"));
            Files.writeString(inputs.resolve("fixture.xlsx"), "fictional input");
            Path run=Files.createDirectory(directory.resolve("output"));
            Files.writeString(run.resolve("input"), "a file blocking the input directory");
            Experiment.inputFolder=inputs.toString();
            Experiment.testOutputFolder=run.toString();
            manager.copyInputFolderStructure=true;
            manager.saveExperimentOnDatabase=false;
            Fixture first=new Fixture("first", true), second=new Fixture("second", false);
            engine.addSimulationManager(first);
            engine.addSimulationManager(second);

            ExperimentSetupException failure=assertThrows(ExperimentSetupException.class, engine::buildModels);
            assertInstanceOf(java.io.IOException.class, failure.getCause());
            assertFalse(failure.getMessage().contains(directory.toString()));
            assertEquals(1, failure.getSuppressed().length);
            assertEquals("fictional cleanup failure", failure.getSuppressed()[0].getMessage());
            assertFalse(engine.getModelBuildStatus());
            assertFalse(engine.getRunningStatus());
            assertFalse(first.built || second.built);
            assertTrue(first.disposed && second.disposed);
            assertNull(engine.getManager("first"));
            assertNull(engine.getManager("second"));
            assertEquals(0, engine.getEventQueue().getEventArray().length);
            assertEquals("a file blocking the input directory", Files.readString(run.resolve("input")));
        } finally {
            engine.disposeModels();
            DatabaseUtils.closeEntityManagerFactories();
            manager.copyInputFolderStructure=copy;
            manager.saveExperimentOnDatabase=save;
            Experiment.inputFolder=input;
            Experiment.testOutputFolder=output;
        }
    }
}
