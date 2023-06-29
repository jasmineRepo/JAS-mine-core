package microsim.reflection;

import lombok.NonNull;
import microsim.exception.SimulationRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Employs Java reflection to call objects' methods which return integer values.
 */
public class IntegerValueExtractor extends AbstractValueExtractor {

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Object, String, boolean)
     */
    public IntegerValueExtractor(final @NonNull Object target, final @NonNull String fieldName,
                                 final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Class, String, boolean)
     */
    public IntegerValueExtractor(final @Nullable Class<?> target, final @NonNull String fieldName,
                                 final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * Invoke the method of the target object and return its integer result.
     *
     * @param target Object to be invoked.
     * @return The requested integer value.
     */
    public int getInt(final @NonNull Object target) {
        try {
            return method == null ? field.getInt(target) : (Integer) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            String m = "IntegerInvoker: " + (method == null ? "Field " : "Method ") +
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
     * Invoke the method of the object passed to constructor and return its integer result.
     *
     * @return The requested integer value.
     */
    public int getInt() {
        return getInt(target);
    }

    /**
     * This is an implementation of the {@link microsim.statistics.IntSource} interface. It calls the {@link #getInt()}
     * method.
     *
     * @param ignoredValueID This parameter is ignored. It is put for compatibility with the
     *                       {@link microsim.statistics.IntSource} interface.
     * @return The requested integer value.
     */
    public int getIntValue(int ignoredValueID) {
        return getInt(target);
    }

    public int @NonNull [] getCollectionValue(final @NonNull Collection<?> c) {
        return c.stream().mapToInt(this::getInt).toArray();
    }
}
