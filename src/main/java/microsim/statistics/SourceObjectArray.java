package microsim.statistics;

import lombok.NonNull;

/**
 * Used by jas.statistics.db package objects to obtain the reference of the sources of a CrossSection.
 */
public interface SourceObjectArray {
    @NonNull Object[] getSourceArray();
}
