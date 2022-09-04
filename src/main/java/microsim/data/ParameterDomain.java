package microsim.data;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterDomain {

	public ParameterDomain() {
	}

	public ParameterDomain(String name, Object @Nullable [] values) {
		this.name = name;
		this.values = values;
	}

	@Setter	@Getter private String name;

	@Setter @Getter private Object @Nullable [] values;

	public @NotNull ParameterDomain addValue(Object value) {
		val scratchLength = 1 + (values != null ? values.length : 0);
		val scratchArray = new Object[scratchLength];
		if (values != null)
			System.arraycopy(values, 0, scratchArray, 0, values.length);
		scratchArray[scratchLength - 1] = value;
		values = scratchArray;
		return this;
	}
}
