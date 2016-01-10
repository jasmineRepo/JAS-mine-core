package microsim.data;

import java.util.ArrayList;
import java.util.List;

public class ParameterRangeDomain extends ParameterDomain {
	
	private Double min;
	
	private Double max;
	
	private Double step;
	
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
		List<Object> array = new ArrayList<Object>();
		
		Double currentValue = min;
		while (currentValue < max) { 
			array.add(currentValue);
			currentValue += step;
		}
		
		return array.toArray();
	}

	@Override
	public void setValues(Object[] values) {
		throw new UnsupportedOperationException("Range parameters cannot be set as list");
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getStep() {
		return step;
	}

	public void setStep(Double step) {
		this.step = step;
	}
	
}
