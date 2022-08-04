package microsim.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.HashMap;
import java.util.Objects;

public class MultiKeyHashMap extends HashMap<Integer, MultiKeyHashMap.EntryValue> {

	@Serial
	private static final long serialVersionUID = 4939180438185813582L;

	public static class EntryValue {
		
		@Setter @Getter private Object[] keyArray;
		@Setter @Getter private Object[] valueArray;
		
		public EntryValue(Object[] keyArray, Object[] valueArray) {
			super();
			this.keyArray = keyArray;
			this.valueArray = valueArray;
		}
	}
	
	public int getHashKey(Object[] keyArray) {
		int hashValue = 0;

		for (Object o : keyArray)
			hashValue += o.hashCode();

		return hashValue;
	}
	
	public boolean containsKey(Object ... keyArray) {		
		return super.containsKey(getHashKey(keyArray));
	}

	public Object[] put(Object[] keyArray, Object[] valueArray) {
		return Objects.requireNonNull(super.put(getHashKey(keyArray),
				new EntryValue(keyArray, valueArray))).getValueArray();
	}

	public Object[] remove(Object ... keyArray) {
		return super.remove(getHashKey(keyArray)).getValueArray();
	}

	public Object[] get(Object ... keyArray) {
		return super.get(getHashKey(keyArray)).getValueArray();
	}

}
