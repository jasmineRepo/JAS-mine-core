package microsim.reflection;

import lombok.NonNull;
import lombok.val;
import microsim.exception.SimulationRuntimeException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Employs Java reflection to call objects' methods which return values of the corresponding type.
 */
public abstract class AbstractValueExtractor {
    protected Method method;
    protected Field field;
    protected Object target;

    /**
     * Constructor.
     *
     * @param target    The target object.
     * @param fieldName A string representing the name of the method to invoke.
     * @param isMethod  If true the fieldName is a method, otherwise it is a property of the object.
     */
    public AbstractValueExtractor(final @NonNull Object target, final @NonNull String fieldName,
                                  final boolean isMethod){
        this.target = target;
        if (isMethod) buildMethod(target.getClass(), fieldName);
        else buildField(target.getClass(), fieldName);
    }

    /**
     * Constructor.
     *
     * @param target    The class of the target object.
     * @param fieldName A string representing the name of the method to invoke.
     * @param isMethod  If true the fieldName is a method, otherwise it is a property of the object.
     */
    public AbstractValueExtractor(final @Nullable Class<?> target, final @NonNull String fieldName,
                                 final boolean isMethod) {
        this.target = null;
        if (isMethod) buildMethod(target, fieldName);
        else buildField(target, fieldName);
    }

    private void buildField(final @Nullable Class<?> targetClass, final @NonNull String fieldName) {
        method = null;
        field = ReflectionUtils.searchField(targetClass, fieldName);
        val m = "%s Invoker: Field %s of object %s ".formatted(getClass().getName(), fieldName, target);

        if (field == null) throw new SimulationRuntimeException(m + "does not exist.");
        if (field.getType() != Double.TYPE) throw new SimulationRuntimeException(m + "must return a double value!");
    }

    private void buildMethod(final @Nullable Class<?> targetClass, final @NonNull String methodName) {
        field = null;
        method = ReflectionUtils.searchMethod(targetClass, methodName);
        val m = "%s Invoker: Method %s of object %s ".formatted(getClass().getName(), methodName, target);

        if (method == null) throw new SimulationRuntimeException(m + "does not exist.");
        if (method.getReturnType() != Double.TYPE)
            throw new SimulationRuntimeException(m + "must return a double value!");
    }
}
