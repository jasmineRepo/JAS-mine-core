package microsim.data;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.Nullable;

public class ParameterDomain {

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Object[] values;

    public ParameterDomain() {
    }

    public ParameterDomain(final @NonNull String name, final @Nullable Object @Nullable [] values) {
        this.name = name;
        this.values = values;
    }

    public @NonNull ParameterDomain addValue(final @Nullable Object value) {
        val scratchLength = 1 + (values != null ? values.length : 0);
        val scratchArray = new Object[scratchLength];
        if (values != null) System.arraycopy(values, 0, scratchArray, 0, values.length);
        scratchArray[scratchLength - 1] = value;
        values = scratchArray;
        return this;
    }
}
