package microsim.alignment.multiple;

import java.util.Arrays;

/**
 * This class is a non-weighted implementation of {@link microsim.alignment.multiple.AbstractLogitScalingAlignment}, for
 * full class documentation see the abstract class itself.
 * @see LogitScalingWeightedAlignment
 */
public class LogitScalingAlignment<T> extends AbstractLogitScalingAlignment<T> {

    /**
     * @inheritDoc This is a non-weighted version, all values are 1.
     */
    @Override final void extractWeights(double[] w) {
        Arrays.fill(w, 1.);
    }
}
