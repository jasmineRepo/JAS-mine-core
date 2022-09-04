package microsim.statistics.reflectors;

import lombok.extern.java.Log;
import microsim.reflection.ReflectionUtils;
import microsim.statistics.DoubleSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * It uses java reflection to call objects' methods
 * which return double values. It is used by Statistics objects.
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
     * @param isMethod  If true the fieldName is a method, otherwise it is a property
     *                  of the object.
     */
    public DoubleInvoker(Object target, String fieldName, boolean isMethod) {
        this.target = target;
        if (isMethod) buildMethod(target.getClass(), fieldName);
        else buildField(target.getClass(), fieldName);
    }

    /**
     * Constructor.
     *
     * @param target    It is the class of the target object.
     * @param fieldName A string representing the name of the method to invoke.
     * @param isMethod  If true the fieldName is a method, otherwise it is a property
     *                  of the object.
     */
    public DoubleInvoker(Class<?> target, String fieldName, boolean isMethod) {
        this.target = null;
        if (isMethod) buildMethod(target, fieldName);
        else buildField(target, fieldName);
    }

    private void buildField(Class<?> trgClass, String fieldName) {
        method = null;
        field = ReflectionUtils.searchField(trgClass, fieldName);

        if (field == null)
            log.log(Level.SEVERE, "DoubleInvoker: Field " + fieldName + " of object "
                    + target + " does not exist.");

        if (field.getType() != Double.TYPE)
            log.log(Level.SEVERE, "DoubleInvoker: Field " + fieldName + " of object "
                    + target + " must return a double value!");
    }

    private void buildMethod(Class<?> trgClass, String methodName) {
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
    public double getDouble(Object target) {
        if (target == null)
            throw new NullPointerException(
                    "The target object is null. This invoker may has built on a collection.");

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
            StringBuilder message = new StringBuilder();
            if (method == null)// fixme replace
                message.append("DblInvoker: Field ").append(field).append(" of object ").append(target).
                        append(" raised the following error:\n");
            else
                message.append("DblInvoker: Method ").append(method).append(" of object ").append(target).
                        append(" raised the following error:\n");
            message.append(ie.getMessage());
            ie.printStackTrace();
            log.log(Level.SEVERE, message.toString());
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            log.log(Level.SEVERE, "");
        }
        return 0.0;
    }

    /**
     * Invoke the method of the object passed to constructor and return its
     * double result.
     *
     * @return The requested double value.
     */
    public double getDouble() {
        return getDouble(target);
    }

    /**
     * This is an implementation of the IDblSource interface. It calls the
     * getDouble() method.
     *
     * @param valueID This parameter is ignored. It is put for compatibility with
     *                the IDblSource interface.
     * @return The requested double value.
     */
    public double getDoubleValue(Enum<?> valueID) {
        return getDouble(target);
    }

    public double[] getCollectionValue(Collection<?> c) {
        double[] target = new double[c.size()];

        int i = 0;
        for (Iterator<?> it = c.iterator(); it.hasNext(); i++)
            target[i] = getDouble(it.next());

        return target;
    }

}
