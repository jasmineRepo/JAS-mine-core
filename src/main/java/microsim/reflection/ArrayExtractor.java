package microsim.reflection;

import java.util.Collection;

public class ArrayExtractor {

	public static double[] extractDoubleArray(Collection<?> collection, Class<?> clazz, String fieldName, boolean isMethod) {
		final DoubleValueExtractor extractor = new DoubleValueExtractor(clazz, fieldName, isMethod);

		final double[] vector = new double[collection.size()];

		int i = 0;
		for (Object element : collection) {
			vector[i++] = extractor.getDouble(element);
		}

		return vector;
	}

	public static int[] extractIntegerArray(Collection<?> collection, Class<?> clazz, String fieldName, boolean isMethod) {
		final IntegerValueExtractor extractor = new IntegerValueExtractor(clazz, fieldName, isMethod);

		final int[] vector = new int[collection.size()];

		int i = 0;
		for (Object element : collection) {
			vector[i++] = extractor.getInt(element);
		}

		return vector;
	}

	public static long[] extractLongArray(Collection<?> collection, Class<?> clazz, String fieldName, boolean isMethod) {
		final LongValueExtractor extractor = new LongValueExtractor(clazz, fieldName, isMethod);

		final long[] vector = new long[collection.size()];

		int i = 0;
		for (Object element : collection) {
			vector[i++] = extractor.getLong(element);
		}

		return vector;
	}

	public static String[] extractStringArray(Collection<?> collection, Class<?> clazz, String fieldName, boolean isMethod) {
		final StringValueExtractor extractor = new StringValueExtractor(clazz, fieldName, isMethod);

		final String[] vector = new String[collection.size()];

		int i = 0;
		for (Object element : collection) {
			vector[i++] = extractor.getString(element);
		}

		return vector;
	}

}
