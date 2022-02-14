package microsim.data;

import java.lang.reflect.Field;
import java.util.List;

import microsim.annotation.CoefficientMapping;

public class MultiKeyCoefficientMapFactory {

	public static MultiKeyCoefficientMap createMapFromAnnotatedList(List<?> list) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		
		if (list == null || list.size() == 0)
			throw new IllegalArgumentException("List must be not null and must contain at least one element");

		Class<?> clazz = list.get(0).getClass();
		if (! clazz.isAnnotationPresent(CoefficientMapping.class))
			throw new IllegalArgumentException("List must contain CoefficientMap annotated objects");
		
		CoefficientMapping anno = clazz.getAnnotation(CoefficientMapping.class);
		
		String[] keys = anno.keys();
		String[] values = anno.values();
		
		MultiKeyCoefficientMap map = new MultiKeyCoefficientMap(keys, values);
		
		for (Field field  : clazz.getDeclaredFields()) {
			field.setAccessible(true);
		}
				
		for (Object object : list) {			
			for (String value : values) {
				switch (keys.length) {
					case 1:
						map.putValue(getValue(clazz, keys[0], object), value, getValue(clazz, value, object));
						break;
					case 2:
						map.putValue(getValue(clazz, keys[0], object), getValue(clazz, keys[1], object), value, getValue(clazz, value, object));
						break;
					case 3:
						map.putValue(getValue(clazz, keys[0], object), getValue(clazz, keys[1], object), getValue(clazz, keys[2], object), value, getValue(clazz, value, object));
						break;
					case 4:
						map.putValue(getValue(clazz, keys[0], object), getValue(clazz, keys[1], object), getValue(clazz, keys[2], object), getValue(clazz, keys[3], object), value, getValue(clazz, value, object));
						break;
					case 5:
						map.putValue(getValue(clazz, keys[0], object), getValue(clazz, keys[1], object), getValue(clazz, keys[2], object), getValue(clazz, keys[3], object), getValue(clazz, keys[4], object), value, getValue(clazz, value, object));
						break;
					default:
						throw new IllegalArgumentException("Unsupported number of keys");
				}				
			}
		}
		
		return map;		
	}
	
	private static Object getValue(Class<?> clazz, String fieldName, Object object) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		final Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}
}
