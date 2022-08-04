package microsim.statistics.reflectors;

import microsim.reflection.ReflectionUtils;
import microsim.statistics.FloatSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Not of interest for users. It uses java reflection to call objects' methods
 * which return double values. It is used by Statistics objects.
 */
public class FloatInvoker implements FloatSource {
	private static final Logger log = Logger.getLogger(FloatInvoker.class.toString());

	protected Method method;
	protected Field field;
	protected Object target;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            It is the target object.
	 * @param fieldName
	 *            A string representing the name of the method to invoke.
	 * @param isMethod
	 *            If true the fieldName is a method, otherwise it is a property
	 *            of the object.
	 */
	public FloatInvoker(Object target, String fieldName, boolean isMethod) {
		this.target = target;
		if (isMethod)
			buildMethod(target.getClass(), fieldName);
		else
			buildField(target.getClass(), fieldName);
	}

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            It is the class of the target object.
	 * @param fieldName
	 *            A string representing the name of the method to invoke.
	 * @param isMethod
	 *            If true the fieldName is a method, otherwise it is a property
	 *            of the object.
	 */
	public FloatInvoker(Class<?> target, String fieldName, boolean isMethod) {
		this.target = null;
		if (isMethod)
			buildMethod(target, fieldName);
		else
			buildField(target, fieldName);
	}

	private void buildField(Class<?> trgClass, String fieldName) {
		method = null;
		field = ReflectionUtils.searchField(trgClass, fieldName);

		if (field == null)
			log.log(Level.SEVERE, "FloatInvoker: Field " + fieldName + " of object "
					+ target + " does not exist.");

		if (field.getType() != Float.TYPE)
			log.log(Level.SEVERE, "FloatInvoker: Field " + fieldName + " of object "
					+ target + " must return a float value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			log.log(Level.SEVERE, "FloatInvoker: Method " + methodName + " of object "
					+ target + " does not exist.");

		if (method.getReturnType() != Float.TYPE)
			log.log(Level.SEVERE, "FloatInvoker: Method " + methodName + " of object "
					+ target + " must return a float value!");
	}

	/**
	 * Invoke the method of the target object and return its double result.
	 * 
	 * @param target
	 *            Object to be invoked.
	 * @return The requested double value.
	 */
	public float getFloat(Object target) {
		if (target == null)
			return 0.0f;

		try {
			if (method == null) {
				try {
					return field.getFloat(target);
				} catch (IllegalArgumentException e) {
					return (Float) field.get(target);
				}
			} else
				return (Float) method.invoke(target, null);
		} catch (InvocationTargetException ie) {
			StringBuilder message = new StringBuilder();
			if (method == null)
				message.append("FltInvoker: Field ").append(field).append("of object ").append(target).
						append(" raised the following error:\n");
			else
				message.append("FltInvoker: Method ").append(method).append("of object ").append(target).
						append(" raised the following error:\n");
			message.append(ie.getMessage());
			ie.printStackTrace();
			log.log(Level.SEVERE, message.toString());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			log.log(Level.SEVERE, "");
		}
		return 0.0f;
	}

	/**
	 * Invoke the method of the object passed to constructor and return its
	 * double result.
	 * 
	 * @return The requested double value.
	 */
	public float getFloat() {
		return getFloat(target);
	}

	/**
	 * This is an implementation of the IDblSource interface. It calls the
	 * getDouble() method.
	 * 
	 * @param valueID
	 *            This parameter is ignored. It is put for compatibility with
	 *            the IDblSource interface.
	 * @return The requested double value.
	 */
	public float getFloatValue(Enum<?> valueID) {
		return getFloat(target);
	}

}