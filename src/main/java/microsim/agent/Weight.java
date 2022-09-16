package microsim.agent;

/**
 * Interface to implement when using JAS-mine classes that depend on agents that have weights.
 */
public interface Weight {

    /**
     * @return the weight of an object.
     */
    double getWeight();
}
