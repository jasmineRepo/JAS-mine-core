package microsim.alignment.multiple;

import org.jetbrains.annotations.NotNull;

/**
 * A general interface for alignment procedures with multiple outcomes.
 *
 * @param <T>
 */

public interface AlignmentMultiProbabilityClosure<T> {

    /**
     * Returns a discrete probability distribution for a given agent.
     *
     * @param agent An agent object.
     * @return A double array with probabilities.
     */
    double @NotNull [] getProbability(final @NotNull T agent);

    /**
     * Aligns (corrects) probabilities of a given agent.
     *
     * @param agent              An agent for correction.
     * @param alignedProbability Probabilities that replace old values.
     */
    void align(final @NotNull T agent, final double @NotNull [] alignedProbability);

}
