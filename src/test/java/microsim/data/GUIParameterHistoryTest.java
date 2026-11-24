/* (C) Copyright 2026, by Ross Richardson
 *
 * GUIParameter History Test.
 *
 * @author ross richardson
 *
 */

package microsim.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import microsim.annotation.GUIparameter;
import microsim.engine.AbstractSimulationManager;

class GUIParameterHistoryTest {

    static class ModelFixture extends AbstractSimulationManager {
        @GUIparameter
        int shared = 1;

        @GUIparameter
        String label = "hello, world";

        int internal = 9;

        @Override
        public String getId() { return "model"; }

        @Override
        public void buildObjects() {}

        @Override
        public void buildSchedule() {}
    }

    static class CollectorFixture extends AbstractSimulationManager {
        @GUIparameter
        int shared = 2;

        @Override
        public String getId() { return "collector"; }

        @Override
        public void buildObjects() {}

        @Override
        public void buildSchedule() {}
    }

    @TempDir
    Path temporaryDirectory;

    @Test
    void initialSnapshotIncludesComponentsAndPreservesDuplicateParameterNames() throws Exception {
        Path output = temporaryDirectory.resolve("run-a");
        Files.createDirectories(output);
        Files.writeString(output.resolve(GUIParameterHistory.FILE_NAME), "stale history");

        GUIParameterHistory.writeInitial(
                output.toFile(), 0.0, new ModelFixture(), null, new CollectorFixture());

        List<String> lines = Files.readAllLines(output.resolve(GUIParameterHistory.FILE_NAME));
        assertEquals(GUIParameterHistory.HEADER, lines.get(0));
        assertEquals(Set.of(
                "0.0,model,shared,1",
                "0.0,model,label,\"hello, world\"",
                "0.0,collector,shared,2"),
                Set.copyOf(lines.subList(1, lines.size())));
    }

    @Test
    void changedValuesReturnsOnlySelectedParametersWhoseValuesChanged() throws Exception {
        ModelFixture model = new ModelFixture();
        List<GUIParameterHistory.ParameterValue> before =
                GUIParameterHistory.capture(model, Set.of("shared", "label"));

        model.shared = 3;

        List<GUIParameterHistory.ParameterValue> changes = GUIParameterHistory.changedValues(
                before, GUIParameterHistory.capture(model, Set.of("shared", "label")));

        assertEquals(List.of(
                new GUIParameterHistory.ParameterValue("model", "shared", "3")), changes);
    }

    @Test
    void runtimeChangesAppendWithoutRepeatingTheInitialSnapshot() throws Exception {
        Path output = temporaryDirectory.resolve("run-b");
        GUIParameterHistory.writeInitial(output.toFile(), 0.0, new ModelFixture());

        GUIParameterHistory.appendChanges(output.toFile(), 2028.0, List.of(
                new GUIParameterHistory.ParameterValue("model", "shared", "4")));
        GUIParameterHistory.appendChanges(output.toFile(), 2030.0, List.of());

        List<String> lines = Files.readAllLines(output.resolve(GUIParameterHistory.FILE_NAME));
        assertEquals(4, lines.size());
        assertEquals(GUIParameterHistory.HEADER, lines.getFirst());
        assertEquals(Set.of(
                "0.0,model,shared,1",
                "0.0,model,label,\"hello, world\""), Set.copyOf(lines.subList(1, 3)));
        assertEquals("2028.0,model,shared,4", lines.getLast());
        assertEquals(1, lines.stream().filter(GUIParameterHistory.HEADER::equals).count());
        assertTrue(lines.stream().noneMatch(line -> line.startsWith("2030.0,")));
    }

    @Test
    void escapesQuotesAndNewlinesInTextValues() throws Exception {
        ModelFixture model = new ModelFixture();
        model.label = "é, \"quoted\"\r\nsecond line";
        GUIParameterHistory.writeInitial(temporaryDirectory.toFile(), 0.0, model);
        String csv = Files.readString(temporaryDirectory.resolve(GUIParameterHistory.FILE_NAME));
        assertTrue(csv.contains("0.0,model,label,\"é, \"\"quoted\"\"\r\nsecond line\""));
    }
}
