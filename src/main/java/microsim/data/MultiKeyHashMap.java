package microsim.data;

import java.util.HashMap;

public class MultiKeyHashMap extends HashMap<Integer, MultiKeyHashMap.EntryValue> {

	private static final long serialVersionUID = 4939180438185813582L;

	public class EntryValue {
		
		private Object[] keyArray;
		private Object[] valueArray;
		
		public EntryValue(Object[] keyArray, Object[] valueArray) {
			super();
			this.keyArray = keyArray;
			this.valueArray = valueArray;
		}

		public Object[] getKeyArray() {
			return keyArray;
		}

		public void setKeyArray(Object[] keyArray) {
			this.keyArray = keyArray;
		}

		public Object[] getValueArray() {
			return valueArray;
		}

		public void setValueArray(Object[] valueArray) {
			this.valueArray = valueArray;
		}

	}
	
	public int getHashKey(Object[] keyArray) {
		int hashValue = 0;
		
		for (int i = 0; i < keyArray.length; i++) {
			hashValue += keyArray[i].hashCode();
		}

		return hashValue;
	}
	
	public boolean containsKey(Object ... keyArray) {		
		return super.containsKey(getHashKey(keyArray));
	}

	public Object[] put(Object[] keyArray, Object[] valueArray) {
		return super.put(getHashKey(keyArray), new EntryValue(keyArray, valueArray)).getValueArray();
	}

	public Object[] remove(Object ... keyArray) {
		return super.remove(getHashKey(keyArray)).getValueArray();
	}

	public Object[] get(Object ... keyArray) {
		return super.get(getHashKey(keyArray)).getValueArray();
	}

}
