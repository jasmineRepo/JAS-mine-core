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
public class LongValueExtractor {
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
	public LongValueExtractor(Object target, String fieldName, boolean isMethod) {
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
	public LongValueExtractor(Class<?> target, String fieldName,
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
			throw new SimulationRuntimeException("LongInvoker: Field "
					+ fieldName + " of object " + target + " does not exist.");

		if (field.getType() != Long.TYPE)
			throw new SimulationRuntimeException("LongInvoker: Field "
					+ fieldName + " of object " + target
					+ " must return a long value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			throw new SimulationRuntimeException("LongInvoker: Method "
					+ methodName + " of object " + target + " does not exist.");

		if (method.getReturnType() != Long.TYPE)
			throw new SimulationRuntimeException("LongInvoker: Method "
					+ methodName + " of object " + target
					+ " must return a long value!");
	}

	/**
	 * Invoke the method of the target object and return its double result.
	 * 
	 * @param target
	 *            Object to be invoked.
	 * @return The requested double value.
	 */
	public long getLong(Object target) {
		if (target == null)
			return 0L;

		try {
			if (method == null)
				return field.getLong(target);
			else
				return ((Long) method.invoke(target, null)).longValue();
		} catch (InvocationTargetException ie) {
			StringBuffer message = new StringBuffer();
			if (method == null)
				message.append("LngInvoker: Field " + field + "of object "
						+ target + " raised the following error:\n");
			else
				message.append("LngInvoker: Method " + method + "of object "
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
	public long getLong() {
		return getLong(target);
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
	public long getLongValue(int valueID) {
		return getLong(target);
	}
	
}