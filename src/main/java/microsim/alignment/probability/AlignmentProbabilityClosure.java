package microsim.alignment.probability;

import lombok.NonNull;

public interface AlignmentProbabilityClosure<T> {

    /**
     * Returns the unaligned probability of a 'positive' outcome for the agent (the user should define what the positive
     * outcome is; it could be that something happens or does not happen).
     *
     * @param agent an agent-representing object.
     * @return the probability of a 'positive' outcome for the agent.
     */
    double getProbability(final @NonNull T agent);

    /**
     * Method specifying the sampling of the aligned probability to determine the outcome for the agent.
     *
     * @param agent              an agent-representing object.
     * @param alignedProbability the corrected probability of a 'positive' outcome
     */
    void align(final @NonNull T agent, final double alignedProbability);
}
