package microsim.statistics.reflectors;

import lombok.NonNull;
import lombok.extern.java.Log;
import microsim.reflection.ReflectionUtils;
import microsim.statistics.DoubleSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Employs Java reflection to call objects' methods which return double values.
 */
@Log
public class DoubleInvoker implements DoubleSource {
    protected Method method;
    protected Field field;
    protected Object target;

    /**
     * Constructor.
     *
     * @param target    It is the target object.
     * @param fieldName A string representing the name of the method to invoke.
     * @param isMethod  If true the fieldName is a method, otherwise it is a property of the object.
     */
    public DoubleInvoker(final @NonNull Object target, final @NonNull String fieldName, final boolean isMethod) {
        this.target = target;
        if (isMethod) buildMethod(target.getClass(), fieldName);
        else buildField(target.getClass(), fieldName);
    }

    /**
     * Constructor.
     *
     * @param target    It is the class of the target object.
     * @param fieldName A string representing the name of the method to invoke.
     * @param isMethod  If true the fieldName is a method, otherwise it is a property of the object.
     */
    public DoubleInvoker(final @NonNull Class<?> target, final @NonNull String fieldName, final boolean isMethod) {
        this.target = null;
        if (isMethod) buildMethod(target, fieldName);
        else buildField(target, fieldName);
    }

    private void buildField(final @NonNull Class<?> trgClass, final @NonNull String fieldName) {
        method = null;
        field = ReflectionUtils.searchField(trgClass, fieldName);

        if (field == null)
            log.log(Level.SEVERE, "DoubleInvoker: Field " + fieldName + " of object "
                + target + " does not exist.");

        if (field.getType() != Double.TYPE)
            log.log(Level.SEVERE, "DoubleInvoker: Field " + fieldName + " of object "
                + target + " must return a double value!");
    }

    private void buildMethod(final @NonNull Class<?> trgClass, final @NonNull String methodName) {
        field = null;
        method = ReflectionUtils.searchMethod(trgClass, methodName);

        if (method == null)
            log.log(Level.SEVERE, "DoubleInvoker: Method " + methodName + " of object "
                + target + " does not exist.");

        if (method.getReturnType() != Double.TYPE)
            log.log(Level.SEVERE, "DoubleInvoker: Method " + methodName + " of object "
                + target + " must return a double value!");
    }

    /**
     * Invoke the method of the target object and return its double result.
     *
     * @param target Object to be invoked.
     * @return The requested double value.
     */
    public double getDouble(final @NonNull Object target) {
        try {
            if (method == null) {
                try {
                    return field.getDouble(target);
                } catch (IllegalArgumentException e) {
                    return (Double) field.get(target);
                }
            } else
                return (Double) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            String message = "DoubleInvoker: " + (method == null ? "Field " + field : "Method " + method) +
                " of object " + target + " raised the following error:\n" + ie.getMessage();
            ie.printStackTrace();
            log.log(Level.SEVERE, message);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            log.log(Level.SEVERE, "Failed to get access.");
        }
        return 0.0;
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
     * This is an implementation of the {@link DoubleSource} interface. It calls the {@link #getDouble()}  method.
     *
     * @param valueID This parameter is ignored. It is put for compatibility with the {@link DoubleSource} interface.
     * @return The requested double value.
     */
    public double getDoubleValue(final @NonNull Enum<?> valueID) {
        return getDouble(target);
    }

    public double @NonNull [] getCollectionValue(final @NonNull Collection<?> c) {
        return c.stream().mapToDouble(this::getDouble).toArray();
    }
}
