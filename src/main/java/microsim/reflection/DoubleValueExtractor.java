package microsim.reflection;

import lombok.NonNull;
import microsim.exception.SimulationRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Employs Java reflection to call objects' methods which return double values.
 */
public class DoubleValueExtractor extends AbstractValueExtractor {

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Object, String, boolean)
     */
    public DoubleValueExtractor(final @NonNull Object target, final @NonNull String fieldName, final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Class, String, boolean)
     */
    public DoubleValueExtractor(final @Nullable Class<?> target, final @NonNull String fieldName,
                                final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * Invoke the method of the target object and return its double result.
     *
     * @param target Object to be invoked.
     * @return The requested double value.
     */
    public double getDouble(final @NonNull Object target) {
        try {
            return method == null ? field.getDouble(target) : (Double) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            String m = "DoubleInvoker: " + (method == null ? "Field " : "Method ") + (method == null ? field : method) +
                " of object " + target + " raised the following error:\n" + ie.getMessage();
            ie.printStackTrace();
            throw new SimulationRuntimeException(m);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new SimulationRuntimeException("Failed to get access to the method.");
        }
    }

    /**
     * Invoke the method of the object passed to constructor and return its double result.
     *
     * @return The requested double value.
     */
    public double getDouble() {
        return getDouble(target);
    }

    /**
     * This is an implementation of the {@link microsim.statistics.DoubleSource} interface. It calls the
     * {@link #getDouble()} method.
     *
     * @param ignoredValueID This parameter is ignored. It is put for compatibility with the
     *                       {@link microsim.statistics.DoubleSource} interface.
     * @return The requested double value.
     */
    public double getDoubleValue(int ignoredValueID) {
        return getDouble(target);
    }

    public double @NonNull [] getCollectionValue(final @NonNull Collection<?> c) {
        return c.stream().mapToDouble(this::getDouble).toArray();
    }
}
