package microsim.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;

public class ParameterRangeDomain extends ParameterDomain {

    @Setter
    @Getter
    private Double min;

    @Setter
    @Getter
    private Double max;

    @Setter
    @Getter
    private Double step;

    public ParameterRangeDomain() {
    }

    public ParameterRangeDomain(final @NonNull String name, final @NonNull Double min, final @NonNull Double max,
                                final @NonNull Double step) {
        setName(name);
        this.max = max;
        this.min = min;
        this.step = step;
    }

    @Override
    public Object[] getValues() {
        val array = new ArrayList<>();

        var currentValue = min;
        while (currentValue < max) { // improve this// fixme current jamjam deals with the number of interlvals only, we need a separate version for step size
            array.add(currentValue);
            currentValue += step;
        }

        return array.toArray();
    }

    @Override
    public void setValues(Object[] values) {
        throw new UnsupportedOperationException("Range parameters cannot be set as list");
    }
}
