package microsim.alignment.multiple;


import lombok.NonNull;

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
    double @NonNull [] getProbability(final @NonNull T agent);

    /**
     * Aligns (corrects) probabilities of a given agent.
     *
     * @param agent              An agent for correction.
     * @param alignedProbability Probabilities that replace old values.
     */
    void align(final @NonNull T agent, final double @NonNull [] alignedProbability);

}
