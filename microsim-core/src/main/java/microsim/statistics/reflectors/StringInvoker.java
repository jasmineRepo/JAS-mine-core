package microsim.statistics.reflectors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import microsim.reflection.ReflectionUtils;
import microsim.statistics.IStringSource;

import org.apache.log4j.Logger;

/**
 * Not of interest for users. It uses java reflection to call objects' methods
 * which return string values. It is used by database objects.
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
public class StringInvoker implements IStringSource {
	private static Logger log = Logger.getLogger(StringInvoker.class);
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
	public StringInvoker(Object target, String fieldName, boolean isMethod) {
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
	public StringInvoker(Class<?> target, String fieldName, boolean isMethod) {
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
			log.error("StringInvoker: Field " + fieldName + " of object "
					+ target + " does not exist.");

		if (field.getType() != String.class)
			log.error("StringInvoker: Field " + fieldName + " of object "
					+ target + " must return a String value!");
	}

	private void buildMethod(Class<?> trgClass, String methodName) {
		field = null;
		method = ReflectionUtils.searchMethod(trgClass, methodName);

		if (method == null)
			log.error("StringInvoker: Method " + methodName + " of object "
					+ target + " does not exist.");

		if (method.getReturnType() != String.class)
			log.error("StringInvoker: Method " + methodName + " of object "
					+ target + " must return a double value!");
	}

	/**
	 * Invoke the method of the target object and return its string result.
	 * 
	 * @param target
	 *            Object to be invoked.
	 * @return The requested string value.
	 */
	public String getString(Object target) {
		if (target == null)
			throw new NullPointerException(
					"The target object is null. This invoker may has built on a collection.");

		try {
			if (method == null)
				return (String) field.get(target);
			else
				return (String) method.invoke(target, null);
		} catch (InvocationTargetException ie) {
			StringBuffer message = new StringBuffer();
			if (method == null)
				message.append("StringInvoker: Field " + field + " of object "
						+ target + " raised the following error:\n");
			else
				message.append("StringInvoker: Method " + method
						+ " of object " + target
						+ " raised the following error:\n");
			message.append(ie.getMessage());
			ie.printStackTrace();
			log.error(message.toString());

		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			log.error("");
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
	 * This is an implementation of the IDblSource interface. It calls the
	 * getDouble() method.
	 * 
	 * @param valueID
	 *            This parameter is ignored. It is put for compatibility with
	 *            the IDblSource interface.
	 * @return The requested double value.
	 */
	public String getStringValue(Enum<?> valueID) {
		return getString(target);
	}

	public String[] getCollectionValue(Collection<?> c) {
		String[] target = new String[c.size()];

		int i = 0;
		for (Iterator<?> it = c.iterator(); it.hasNext(); i++)
			target[i] = getString(it.next());

		return target;
	}

}