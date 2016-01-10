package microsim.data;

import java.util.ArrayList;
import java.util.List;

public class ParameterDomain {

	public ParameterDomain() {
		
	}
	
	public ParameterDomain(String name, Object[] values) {
		super();
		this.name = name;
		this.values = values;
	}

	private String name;
	
	private Object[] values;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
	
	public ParameterDomain addValue(Object value) {
		List<Object> array = new ArrayList<Object>();
		if (values != null)
			for (int i = 0; i < values.length; i++) 
				array.add(values[i]);
		
		array.add(value);
		
		values = array.toArray();
		
		return this;
	}
		
}
