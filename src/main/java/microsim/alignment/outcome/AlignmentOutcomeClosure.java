package microsim.alignment.outcome;

public interface AlignmentOutcomeClosure<T> {

    boolean getOutcome(T agent);

    void resample(T agent);

}
