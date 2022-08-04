package microsim.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

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
public class DoubleValueExtractor {
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
	public DoubleValueExtractor(Object target, String fieldName, boolean isMethod) {
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
	public DoubleValueExtractor(Class<?> target, String fieldName, boolean isMethod) {
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
			throw new SimulationRuntimeException("DoubleInvoker: Field " + fieldName
					+ " of object " + target + " does not exist.");

		if (field.getType() != Double.TYPE)
			throw new SimulationRuntimeException("DoubleInvoker: Field " + fieldName
					+ " of object " + target + " must return a double value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			throw new SimulationRuntimeException("DoubleInvoker: Method "
					+ methodName + " of object " + target + " does not exist.");

		if (method.getReturnType() != Double.TYPE)
			throw new SimulationRuntimeException("DoubleInvoker: Method "
					+ methodName + " of object " + target
					+ " must return a double value!");
	}

	/**
	 * Invoke the method of the target object and return its double result.
	 * 
	 * @param target
	 *            Object to be invoked.
	 * @return The requested double value.
	 */
	public double getDouble(Object target) {
		if (target == null)
			throw new NullPointerException(
					"The target object is null. This invoker may has built on a collection.");

		try {
			if (method == null)
				return field.getDouble(target);
			else
				return ((Double) method.invoke(target, null)).doubleValue();
		} catch (InvocationTargetException ie) {
			StringBuffer message = new StringBuffer();
			if (method == null)
				message.append("DblInvoker: Field " + field + " of object "
						+ target + " raised the following error:\n");
			else
				message.append("DblInvoker: Method " + method + " of object "
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
	public double getDouble() {
		return getDouble(target);
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
	public double getDoubleValue(int valueID) {
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