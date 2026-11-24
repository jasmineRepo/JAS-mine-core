package microsim.data;

import java.nio.file.Path;
import java.util.*;

/* (C) Copyright 2026, by Ross Richardson
 *
 * Cleanup metadata only: never opens, closes or relocates model resources.
 * Each owner holds an independent claim, released only after successful closure.
 * Mutations and cleanup synchronize on this class to prevent protection races.
 *
 * @author ross richardson
 *
 */
public final class StorageProtection {
    public record Claim(Path base, String reason) {}
    private static final Map<String, Claim> DATABASES = new HashMap<>();
    private static final Set<Path> DIRECTORIES = new HashSet<>();
    private StorageProtection() {}
    public static synchronized void protectDatabase(String owner, Path base, String reason) {
        DATABASES.put(Objects.requireNonNull(owner), new Claim(base.toAbsolutePath().normalize(), reason));
    }
    public static synchronized void release(String owner) { DATABASES.remove(owner); }
    public static synchronized void protectDirectory(Path directory) {
        DIRECTORIES.add(directory.toAbsolutePath().normalize());
    }
    public static synchronized Set<Path> directories() { return Set.copyOf(DIRECTORIES); }
    public static synchronized List<String> reasons(Path path) {
        Path p = path.toAbsolutePath().normalize();
        List<String> reasons = new ArrayList<>();
        for (Path dir : DIRECTORIES) if (p.startsWith(dir)) reasons.add("Model-protected directory");
        for (Claim c : DATABASES.values()) {
            // H2 database base plus suffix covers main, lock, trace and temporary files.
            // Preserve possible companion directories too, without protecting neighbours.
            Path parent = c.base().getParent();
            if (parent == null || !p.startsWith(parent) || p.equals(parent)) continue;
            String first = parent.relativize(p).getName(0).toString();
            String baseName = c.base().getFileName().toString();
            if (first.equals(baseName) || first.startsWith(baseName + ".")) reasons.add(c.reason());
        }
        return reasons.stream().distinct().sorted().toList();
    }
    public static synchronized boolean protectsAncestor(Path directory) {
        Path p = directory.toAbsolutePath().normalize();
        return DATABASES.values().stream().anyMatch(c -> c.base().startsWith(p))
            || DIRECTORIES.stream().anyMatch(d -> d.startsWith(p) || p.startsWith(d));
    }
}
