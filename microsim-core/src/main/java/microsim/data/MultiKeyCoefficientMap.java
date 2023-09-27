package microsim.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.AbstractHashedMap;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

public class MultiKeyCoefficientMap extends MultiKeyMap {// implements Cloneable {

	private static final long serialVersionUID = 5049597007431364596L;

	protected String[] keys;
	protected Map<String, Integer> valuesMap;
	
	/**
	 * Creates an empty new MultiKeyCoefficientMap with the names of the keys and 
	 * values categories specified by String[] keys and String[] values arguments.
	 * 
	 * @param keys - a String array listing the names of the categories of keys
	 * @param values - a String array listing the names of the categories of values
	 */
	public MultiKeyCoefficientMap(String[] keys, String[] values) {
		super();
		this.keys = keys;
		if (values != null) {
			valuesMap = new HashMap<String, Integer>();
			for (int i = 0; i < values.length; i++) {
				valuesMap.put(values[i], i);
			}
		}
		
		if (keys == null)
			throw new IllegalArgumentException("Keys array cannot be null");		
	}

	/**
	 * Creates a new MultiKeyCoefficientMap with values stored in map, and with 
	 * the names of the keys and values categories specified by String[] keys 
	 * and String[] values arguments.
	 * 
	 * @param map - contains the values of the MultiKeyCoefficientMap.
	 * @param keys - a String array listing the names of the categories of keys
	 * @param values - a String array listing the names of the categories of values
	 */
	public MultiKeyCoefficientMap(AbstractHashedMap map, String[] keys, String[] values) {
		super(map);
		this.keys = keys;
		if (values != null) {
			valuesMap = new HashMap<String, Integer>();
			for (int i = 0; i < values.length; i++) {
				valuesMap.put(values[i], i);
			}
		}
		
		if (keys == null)
			throw new IllegalArgumentException("Keys array cannot be null");
	}

	public static String toStringKey(Object value) {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Double) {
			return ((Double) value).toString();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).toString();
		} else
			return value.toString();
		
	}
	
	private Object extractValueFromVector(String key, Object[] vector) {
		if (vector == null)
			return null;
		else {
			final Integer k = valuesMap.get(key);
			if (k == null)
				return null;
			else
				return vector[k];			
		}
	}
	
	private void putValueToVector(String key, Object[] vector, Object value) {
		if (vector == null)
			return;
		else {
			final Integer k = valuesMap.get(key);
			if (k == null)
				return;			
			else
				vector[k] = value;			
		}
	}
	
	public Object getValue(Object ... key) {
		if (key.length == keys.length) {
			switch (key.length) {
				case 1:
					if (key[0] instanceof MultiKey)
						return super.get(key[0]);
					else
						return super.get(new MultiKey( new Object[] { key[0] } ));
				case 2:
					return super.get(key[0], key[1]);
				case 3:
					return super.get(key[0], key[1], key[2]);
				case 4:
					return super.get(key[0], key[1], key[2], key[3]);
				case 5:
					return super.get(key[0], key[1], key[2], key[3], key[4]);
				default:
					throw new IllegalArgumentException("Wrong number of key parameters");
			}
		} else if (key.length == keys.length + 1) {
			Object[] value = null;  
			switch (key.length) {
				case 1:
					throw new IllegalArgumentException("Wrong number of key parameters");
				case 2:		
					if (key[0] instanceof MultiKey)		//Ross: If we don't do this check, a new MultiKey of a MultiKey is created unnecessarily, which then leads to a null pointer exception as the MultKeyCoefficientMap does not have a key entry of the type MultiKey(MultiKey()). 
						value = (Object[]) super.get(key[0]);
					else value = (Object[]) super.get(new MultiKey( new Object[] { key[0] } ));
					return extractValueFromVector(toStringKey(key[1]), value);
				case 3:
					value = (Object[]) super.get(key[0], key[1]);
					return extractValueFromVector(toStringKey(key[2]), value);
				case 4:
					value = (Object[]) super.get(key[0], key[1], key[2]);
					return extractValueFromVector(toStringKey(key[3]), value);
				case 5:
					value = (Object[]) super.get(key[0], key[1], key[2], key[3]);
					return extractValueFromVector(toStringKey(key[4]), value);
				case 6:
					value = (Object[]) super.get(key[0], key[1], key[2], key[3], key[4]);
					return extractValueFromVector(toStringKey(key[5]), value);
				default:
					throw new IllegalArgumentException("Wrong number of key parameters");
			}			
		} else
			throw new IllegalArgumentException("Wrong number of key parameters");		
	}

	/**
	 * OBTAIN VALUE BY ROW AND COLUMN REFERENCE; ASSUMES 2D TABLE
	 * @param row
	 * @param col
	 * @return
	 */
	public Object getRowColumnValue(Object row, String col) {
		if (keys.length > 1)
			throw new RuntimeException("Row and column reference only coded for single key objects");
		Object[] values = (Object[]) getValue(row);
		if (values==null)
			return null;
		String[] valuesNames = getValuesNames();
		for (int ii=0; ii<valuesNames.length; ii++) {
			if (valuesNames[ii].equals(col)) {
				return values[ii];
			}
		}
		return null;
	}


	public void putValue(Object ... keyValues) {		
		if (keyValues.length == keys.length + 1) {
			switch (keyValues.length) {
				case 1:
					throw new IllegalArgumentException("Wrong number of key parameters");			
				case 2:
					if (keyValues[0] instanceof MultiKey)		//Ross: If we don't do this check, a new MultiKey of a MultiKey is created unnecessarily, which then leads to a null pointer exception as the MultKeyCoefficientMap does not have a key entry of the type MultiKey(MultiKey()). 
						super.put((MultiKey) keyValues[0], keyValues[1]);
					else {
						super.put(new MultiKey(new Object[] { keyValues[0] }), keyValues[1]);
					}
					break;
				case 3:
					super.put(keyValues[0], keyValues[1], keyValues[2]);
					break;
				case 4:
					super.put(keyValues[0], keyValues[1], keyValues[2], keyValues[3]);
					break;
				case 5:
					super.put(keyValues[0], keyValues[1], keyValues[2], keyValues[3], keyValues[4]);
					break;
				case 6:
					super.put(keyValues[0], keyValues[1], keyValues[2], keyValues[3], keyValues[4], keyValues[5]);
					break;
				default:
					throw new IllegalArgumentException("Wrong number of key parameters");
			}
		} else if (keyValues.length == keys.length + 2) {
			Object[] value;
			switch (keyValues.length) {
				case 1:
				case 2:
					throw new IllegalArgumentException("Wrong number of key parameters");
				case 3:
					value = (Object[]) super.get(keyValues[0]);
					if (value == null)
						value = new Object[valuesMap.size()];
					putValueToVector((String) keyValues[1], value, keyValues[2]);
					super.put(new MultiKey(new Object[] { keyValues[0] }), value);
					break;
				case 4:
					value = (Object[]) super.get(keyValues[0], keyValues[1]);
					if (value == null)
						value = new Object[valuesMap.size()];
					putValueToVector((String) keyValues[2], value, keyValues[3]);
					super.put(keyValues[0], keyValues[1], value);
					break;
				case 5:
					value = (Object[]) super.get(keyValues[0], keyValues[1], keyValues[2]);
					if (value == null)
						value = new Object[valuesMap.size()];
					putValueToVector((String) keyValues[3], value, keyValues[4]);
					super.put(keyValues[0], keyValues[1], keyValues[2], value);
					break;
				case 6:
					value = (Object[]) super.get(keyValues[0], keyValues[1], keyValues[2], keyValues[3]);
					if (value == null)
						value = new Object[valuesMap.size()];
					putValueToVector((String) keyValues[4], value, keyValues[5]);
					super.put(keyValues[0], keyValues[1], keyValues[2], keyValues[3], value);
					break;
				case 7:
					value = (Object[]) super.get(keyValues[0], keyValues[1], keyValues[2], keyValues[3], keyValues[4]);
					if (value == null)
						value = new Object[valuesMap.size()];
					putValueToVector((String) keyValues[5], value, keyValues[6]);					
					super.put(keyValues[0], keyValues[1], keyValues[2], keyValues[3], keyValues[4], value);
					break;
				default:
					throw new IllegalArgumentException("Wrong number of key parameters");
			}			
		} else
			throw new IllegalArgumentException("Wrong number of key parameters");
	}

	/**
	 * This method allows the instance of the MultiKeyCoefficientMap to provide a clone of the names of the keys.
	 * This is especially useful for getting the name of the variables used as keys in the Regression classes
	 *  (in package microsim.statistics.regression)
	 * 
	 * @return a String array clone of the names of the MultiKeyCoefficientMap's keys
	 * @author richardsonr
	 */
	public String[] getKeysNames() {		//The instance of a MultiKeyCoeffientMap can provide the name of the variables used as keys.
		String[] keysClone = new String[keys.length];
		for(int i = 0; i < keys.length; i++) {
			keysClone[i] = keys[i];
		}
		return keysClone;
		
	}
	
	
	/**
	 * This method allows the instance of the MultiKeyCoefficientMap to provide a clone of the names of the values.
	 * This is especially useful for cases where the size of valueColumns > 1, for instance to extract the names
	 * of the Type array (T[] events) for RegressionUtils.event(T[] events, double[] prob) method (in package
	 * microsim.statistics.regression)
	 * 
	 * @return a String array clone of the names of the MultiKeyCoefficientMap's values
	 * @author richardsonr
	 */
	public String[] getValuesNames() {
		String[] valuesClone = new String[valuesMap.size()];
		for(String name: valuesMap.keySet()) {
			valuesClone[valuesMap.get(name)] = name;
		}
		return valuesClone;
	}
	
	/**
	 * Returns a deep clone copy of the MultiKeyCoefficientMap object
	 * 
	 * @author Ross Richardson
	 */
	@Override
	public MultiKeyCoefficientMap clone() {
		HashedMap mapClone = new HashedMap(this.decorated());
		return new MultiKeyCoefficientMap(mapClone, this.getKeysNames(), this.getValuesNames());
	}
}
