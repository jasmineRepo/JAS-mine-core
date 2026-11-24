package microsim.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import microsim.annotation.GUIparameter;
import microsim.engine.SimulationManager;

/* (C) Copyright 2026, by Ross Richardson
 *
 * Records initial GUI parameters and later explicit user changes for a run.
 *
 * @author ross richardson
 *
 */
public final class GUIParameterHistory {

    public static final String FILE_NAME = "GUIparameters.csv";
    public static final String HEADER = "time,component,parameter,value";

    private GUIParameterHistory() {}

    public record ParameterValue(String component, String parameter, String value) {}

    private record ParameterKey(String component, String parameter) {}

    /** Capture every annotated parameter from each supplied component. */
    public static List<ParameterValue> capture(Object... components) throws IllegalAccessException {
        List<ParameterValue> result = new ArrayList<>();
        for (Object component : components) {
            if (component == null) continue;
            captureComponent(component, null, result);
        }
        return List.copyOf(result);
    }

    /** Capture selected annotated parameters from one component. */
    public static List<ParameterValue> capture(Object component, Collection<String> parameterNames)
            throws IllegalAccessException {
        List<ParameterValue> result = new ArrayList<>();
        if (component != null) captureComponent(component, parameterNames, result);
        return List.copyOf(result);
    }

    /** Return after-values whose component/parameter value differs from before. */
    public static List<ParameterValue> changedValues(
            List<ParameterValue> before, List<ParameterValue> after) {
        Map<ParameterKey, String> oldValues = new LinkedHashMap<>();
        for (ParameterValue value : before) {
            oldValues.put(new ParameterKey(value.component(), value.parameter()), value.value());
        }

        List<ParameterValue> changes = new ArrayList<>();
        for (ParameterValue value : after) {
            ParameterKey key = new ParameterKey(value.component(), value.parameter());
            if (!oldValues.containsKey(key) || !Objects.equals(oldValues.get(key), value.value())) {
                changes.add(value);
            }
        }
        return List.copyOf(changes);
    }

    /** Write one complete initial snapshot, replacing stale history for the run. */
    public static synchronized void writeInitial(
            File outputFolder, double simulationTime, Object... components)
            throws IOException, IllegalAccessException {
        write(outputFolder, simulationTime, capture(components), false);
    }

    /** Append only parameter values that an interface has established changed. */
    public static synchronized void appendChanges(
            File outputFolder, double simulationTime, List<ParameterValue> changes)
            throws IOException {
        if (changes.isEmpty()) return;
        write(outputFolder, simulationTime, changes, true);
    }

    private static void captureComponent(Object component, Collection<String> parameterNames,
            List<ParameterValue> result) throws IllegalAccessException {
        String componentId = componentId(component);
        for (Field field : component.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(GUIparameter.class)) continue;
            if (parameterNames != null && !parameterNames.contains(field.getName())) continue;
            field.setAccessible(true);
            Object value = field.get(component);
            result.add(new ParameterValue(componentId, field.getName(),
                    value == null ? "" : String.valueOf(value)));
        }
    }

    private static String componentId(Object component) {
        if (component instanceof SimulationManager manager) {
            String id = manager.getId();
            if (id != null && !id.isBlank()) return id;
        }
        String canonicalName = component.getClass().getCanonicalName();
        return canonicalName == null ? component.getClass().getName() : canonicalName;
    }

    private static void write(File outputFolder, double simulationTime,
            List<ParameterValue> values, boolean append) throws IOException {
        if (!outputFolder.isDirectory() && !outputFolder.mkdirs()) {
            throw new IOException("Could not create output folder: " + outputFolder);
        }

        File csvFile = new File(outputFolder, FILE_NAME);
        boolean writeHeader = !append || !csvFile.isFile() || csvFile.length() == 0;
        StandardOpenOption[] options = append
                ? new StandardOpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND }
                : new StandardOpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING };

        try (BufferedWriter writer = Files.newBufferedWriter(
                csvFile.toPath(), StandardCharsets.UTF_8, options)) {
            if (writeHeader) {
                writer.write(HEADER);
                writer.newLine();
            }
            for (ParameterValue value : values) {
                writer.write(Double.toString(simulationTime));
                writer.write(',');
                writer.write(escapeCsv(value.component()));
                writer.write(',');
                writer.write(escapeCsv(value.parameter()));
                writer.write(',');
                writer.write(escapeCsv(value.value()));
                writer.newLine();
            }
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
