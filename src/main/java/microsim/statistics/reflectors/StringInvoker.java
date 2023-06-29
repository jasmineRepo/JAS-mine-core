package microsim.statistics.reflectors;

import lombok.NonNull;
import lombok.extern.java.Log;
import microsim.reflection.ReflectionUtils;
import microsim.statistics.StringSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Employs Java reflection to call objects' methods which return string values.
 */
@Log
public class StringInvoker implements StringSource {
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
    public StringInvoker(final @NonNull Object target, final @NonNull String fieldName, final boolean isMethod) {
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
    public StringInvoker(final @NonNull Class<?> target, final @NonNull String fieldName, final boolean isMethod) {
        this.target = null;
        if (isMethod) buildMethod(target, fieldName);
        else buildField(target, fieldName);
    }

    private void buildField(final @NonNull Class<?> trgClass, final @NonNull String fieldName) {
        method = null;
        field = ReflectionUtils.searchField(trgClass, fieldName);

        if (field == null)
            log.log(Level.SEVERE, "StringInvoker: Field " + fieldName + " of object "
                + target + " does not exist.");

        if (field.getType() != String.class)
            log.log(Level.SEVERE, "StringInvoker: Field " + fieldName + " of object "
                + target + " must return a String value!");
    }

    private void buildMethod(final @NonNull Class<?> trgClass,final @NonNull String methodName) {
        field = null;
        method = ReflectionUtils.searchMethod(trgClass, methodName);

        if (method == null)
            log.log(Level.SEVERE, "StringInvoker: Method " + methodName + " of object "
                + target + " does not exist.");

        if (method.getReturnType() != String.class)
            log.log(Level.SEVERE, "StringInvoker: Method " + methodName + " of object "
                + target + " must return a double value!");
    }

    /**
     * Invoke the method of the target object and return its string result.
     *
     * @param target Object to be invoked.
     * @return The requested string value.
     */
    public String getString(final @NonNull Object target) {
        try {
            return method == null ? (String) field.get(target) : (String) method.invoke(target, (Object) null);
        } catch (InvocationTargetException ie) {
            StringBuilder message = new StringBuilder();
            if (method == null)
                message.append("StringInvoker: Field ").append(field).append(" of object ").append(target).
                    append(" raised the following error:\n");
            else
                message.append("StringInvoker: Method ").append(method).append(" of object ").append(target).
                    append(" raised the following error:\n");
            message.append(ie.getMessage());
            ie.printStackTrace();
            log.log(Level.SEVERE, message.toString());
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            log.log(Level.SEVERE, "");
        }
        return "";
    }

    /**
     * Invoke the method of the object passed to constructor and return its
     * double result.
     *
     * @return The requested double value.
     */
    public String getString() {
        return getString(target);
    }

    /**
     * This is an implementation of the {@link StringSource} interface. It calls the {@link #getString(Object)} method.
     *
     * @param valueID This parameter is ignored. It is put for compatibility with the {@link StringSource} interface.
     * @return The requested double value.
     */
    public @NonNull String getStringValue(final @NonNull Enum<?> valueID) {
        return getString(target);
    }

    public @NonNull String[] getCollectionValue(final @NonNull Collection<?> c) {
        return (String[]) c.stream().map(this::getString).toArray();
    }
}
