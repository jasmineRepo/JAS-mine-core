package microsim.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ParameterRangeDomain extends ParameterDomain {

	@Setter	@Getter private Double min;

	@Setter	@Getter private Double max;

	@Setter	@Getter private Double step;

	public ParameterRangeDomain() {
	}

	public ParameterRangeDomain(String name, Double min, Double max, Double step) {
		setName(name);
		this.max = max;
		this.min = min;
		this.step = step;
	}

	@Override
	public Object[] getValues() {
		List<Object> array = new ArrayList<>();

		Double currentValue = min;
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
