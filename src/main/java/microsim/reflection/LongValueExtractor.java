package microsim.reflection;

import lombok.NonNull;
import microsim.exception.SimulationRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Employs Java reflection to call objects' methods which return long values.
 */
public class LongValueExtractor extends AbstractValueExtractor {

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Object, String, boolean)
     */
    public LongValueExtractor(final @NonNull Object target, final @NonNull String fieldName, final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Class, String, boolean)
     */
    public LongValueExtractor(final @Nullable Class<?> target, final @NonNull String fieldName,
                              final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * Invoke the method of the target object and return its long result.
     *
     * @param target Object to be invoked.
     * @return The requested long value.
     */
    public long getLong(final @NonNull Object target) {
        try {
            return method == null ? field.getLong(target) : (Long) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            String m = "LongInvoker: " + (method == null ? "Field " : "Method ") +
                (method == null ? field : method) +
                " of object " + target + " raised the following error:\n" + ie.getMessage();
            ie.printStackTrace();
            throw new SimulationRuntimeException(m);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new SimulationRuntimeException("Failed to get access to the method.");
        }
    }

    /**
     * Invoke the method of the object passed to constructor and return its long result.
     *
     * @return The requested long value.
     */
    public long getLong() {
        return getLong(target);
    }

    /**
     * This is an implementation of the {@link microsim.statistics.LongSource} interface. It calls the
     * {@link #getLong()} method.
     *
     * @param ignoredValueID This parameter is ignored. It is put for compatibility with the
     *                       {@link microsim.statistics.LongSource} interface.
     * @return The requested long value.
     */
    public long getLongValue(int ignoredValueID) {
        return getLong(target);
    }

    public long @NonNull [] getCollectionValue(final @NonNull Collection<?> c) {
        return c.stream().mapToLong(this::getLong).toArray();
    }
}
