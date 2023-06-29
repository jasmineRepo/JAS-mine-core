package microsim.reflection;

import lombok.NonNull;
import microsim.exception.SimulationRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Employs Java reflection to call objects' methods which return string values.
 */
public class StringValueExtractor extends AbstractValueExtractor {

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Object, String, boolean)
     */
    public StringValueExtractor(final @NonNull Object target, final @NonNull String fieldName,
                                final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * @see AbstractValueExtractor#AbstractValueExtractor(Class, String, boolean)
     */
    public StringValueExtractor(final @Nullable Class<?> target, final @NonNull String fieldName,
                                final boolean isMethod) {
        super(target, fieldName, isMethod);
    }

    /**
     * Invoke the method of the target object and return its string result.
     *
     * @param target Object to be invoked.
     * @return The requested string value.
     */
    public @Nullable String getString(final @NonNull Object target) {
        try {
            return method == null ? (String) field.get(target) : (String) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            String m = "StringInvoker: " + (method == null ? "Field " : "Method ") + (method == null ? field : method) +
                " of object " + target + " raised the following error:\n" + ie.getMessage();
            ie.printStackTrace();
            throw new SimulationRuntimeException(m);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new SimulationRuntimeException("Failed to get access to the method.");
        }
    }

    /**
     * Invoke the method of the object passed to constructor and return its string result.
     *
     * @return The requested string value.
     */
    public @Nullable String getString() {
        return getString(target);
    }

    /**
     * This is an implementation of the {@link microsim.statistics.StringSource} interface. It calls the
     * {@link #getString()} method.
     *
     * @param ignoredValueID This parameter is ignored. It is put for compatibility with the
     *                       {@link microsim.statistics.StringSource} interface.
     * @return The requested string value.
     */
    public @Nullable String getStringValue(int ignoredValueID) {
        return getString(target);
    }

    public @NonNull String[] getCollectionValue(final @NonNull Collection<?> c) {
        return (String[]) c.stream().map(this::getString).toArray();
    }
}
