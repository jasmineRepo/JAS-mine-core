package microsim.matching;

import org.apache.commons.math3.util.Pair;

public class GlobalMatchingPair<T> {

    T agent1;
    T agent2;
    double score;

    public GlobalMatchingPair(T agent1, T agent2, double score) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.score = score;
    }

    public T getAgent1() {
        return agent1;
    }

    public T getAgent2() {
        return agent2;
    }

    public double getScore() {
        return score;
    }
}
