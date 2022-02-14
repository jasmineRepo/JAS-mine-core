package microsim.statistics.reflectors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.logging.Level;
import java.util.logging.Logger;

import microsim.reflection.ReflectionUtils;
import microsim.statistics.IIntSource;

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
public class IntegerInvoker implements IIntSource {
	private static Logger log = Logger.getLogger(IntegerInvoker.class.toString());

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
	public IntegerInvoker(Object target, String fieldName, boolean isMethod) {
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
	public IntegerInvoker(Class<?> target, String fieldName, boolean isMethod) {
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
			log.log(Level.SEVERE, "IntegerInvoker: Field " + fieldName + " of object "
					+ target + " does not exist.");

		if (field.getType() != Integer.TYPE)
			log.log(Level.SEVERE, "IntegerInvoker: Field " + fieldName + " of object "
					+ target + " must return an int value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			log.log(Level.SEVERE, "IntegerInvoker: Method " + methodName + " of object "
					+ target + " does not exist.");

		if (method.getReturnType() != Integer.TYPE)
			log.log(Level.SEVERE, "IntegerInvoker: Method " + methodName + " of object "
					+ target + " must return an integer value!");
	}

	/**
	 * Invoke the method of the target object and return its double result.
	 * 
	 * @param target
	 *            Object to be invoked.
	 * @return The requested double value.
	 */
	public int getInt(Object target) {
		if (target == null)
			return 0;

		try {
			if (method == null) {
				try {
					return field.getInt(target);
				} catch (IllegalArgumentException e) {
					return ((Integer) field.get(target)).intValue();
				}
			} else
				return ((Integer) method.invoke(target, null)).intValue();
		} catch (InvocationTargetException ie) {
			StringBuffer message = new StringBuffer();
			if (method == null)
				message.append("IntInvoker: Field " + field + "of object "
						+ target + " raised the following error:\n");
			else
				message.append("IntInvoker: Method " + method + "of object "
						+ target + " raised the following error:\n");
			message.append(ie.getMessage());
			ie.printStackTrace();
			log.log(Level.SEVERE, message.toString());
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			log.log(Level.SEVERE, "");
		}
		return 0;
	}

	/**
	 * Invoke the method of the object passed to constructor and return its
	 * double result.
	 * 
	 * @return The requested double value.
	 */
	public int getInt() {
		return getInt(target);
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
	public int getIntValue(Enum<?> valueID) {
		return getInt(target);
	}

}