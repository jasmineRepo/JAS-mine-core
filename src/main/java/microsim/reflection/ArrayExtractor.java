package microsim.reflection;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ArrayExtractor {

    public static double @NonNull [] extractDoubleArray(final @NonNull Collection<?> collection,
                                                        final @Nullable Class<?> clazz, final @NonNull String fieldName,
                                                        final boolean isMethod) {
        val extractor = new DoubleValueExtractor(clazz, fieldName, isMethod);
        return extractor.getCollectionValue(collection);
    }

    public static int @NonNull [] extractIntegerArray(final @NonNull Collection<?> collection,
                                                      final @Nullable Class<?> clazz, final @NonNull String fieldName,
                                                      final boolean isMethod) {
        val extractor = new IntegerValueExtractor(clazz, fieldName, isMethod);
        return extractor.getCollectionValue(collection);
    }

    public static long @NonNull [] extractLongArray(final @NonNull Collection<?> collection,
                                                    final @Nullable Class<?> clazz, final @NonNull String fieldName,
                                                    final boolean isMethod) {
        val extractor = new LongValueExtractor(clazz, fieldName, isMethod);
        return extractor.getCollectionValue(collection);
    }

    public static String @NonNull [] extractStringArray(final @NonNull Collection<?> collection,
                                                        final @Nullable Class<?> clazz, final @NonNull String fieldName,
                                                        final boolean isMethod) {
        val extractor = new StringValueExtractor(clazz, fieldName, isMethod);
        return extractor.getCollectionValue(collection);
    }
}
