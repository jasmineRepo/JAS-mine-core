package microsim.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import microsim.exception.SimulationRuntimeException;

/**
 * Not of interest for users. It uses java reflection to call objects' methods
 * which return double values. It is used by Statistics objects.
 *
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public class FloatValueExtractor {
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
	public FloatValueExtractor(Object target, String fieldName, boolean isMethod) {
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
	 *            It is the Class<?>of the target object.
	 * @param fieldName
	 *            A string representing the name of the method to invoke.
	 * @param isMethod
	 *            If true the fieldName is a method, otherwise it is a property
	 *            of the object.
	 */
	public FloatValueExtractor(Class<?> target, String fieldName,
			boolean isMethod) {
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
			throw new SimulationRuntimeException("FloatInvoker: Field "
					+ fieldName + " of object " + target + " does not exist.");

		if (field.getType() != Float.TYPE)
			throw new SimulationRuntimeException("FloatInvoker: Field "
					+ fieldName + " of object " + target
					+ " must return a float value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			throw new SimulationRuntimeException("FloatInvoker: Method "
					+ methodName + " of object " + target + " does not exist.");

		if (method.getReturnType() != Float.TYPE)
			throw new SimulationRuntimeException("FloatInvoker: Method "
					+ methodName + " of object " + target
					+ " must return a float value!");
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
			if (method == null)
				return field.getFloat(target);
			else
				return ((Float) method.invoke(target, null)).floatValue();
		} catch (InvocationTargetException ie) {
			StringBuffer message = new StringBuffer();
			if (method == null)
				message.append("FltInvoker: Field " + field + "of object "
						+ target + " raised the following error:\n");
			else
				message.append("FltInvoker: Method " + method + "of object "
						+ target + " raised the following error:\n");
			message.append(ie.getMessage());
			ie.printStackTrace();
			throw new SimulationRuntimeException(message.toString());

		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			throw new SimulationRuntimeException("");
		}

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
	public float getFloatValue(int valueID) {
		return getFloat(target);
	}

}
