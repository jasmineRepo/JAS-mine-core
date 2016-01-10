package microsim.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import microsim.exception.SimulationException;

/**
 * The simpler implementation of SimEvent class. It represents an event to be
 * notified only to one specific object. It is often used in discrete event
 * simulations, when an object schedule itself for a future event.
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
public class SingleTargetEvent extends AbstractEvent {

	protected Enum<?> eventType;
	protected Method methodInvoker;

	protected Object object;

	/** Create new event using late binding. 
	 * @throws SimulationException */
	public SingleTargetEvent(Object object, String method) throws SimulationException {
		this.object = object;
		setForObject(object, method);
	}

	/** Create new event using early binding. */
	public SingleTargetEvent(Object object, Enum<?> actionType) {
		this.object = object;
		setForObject(object, actionType);
	}

	/**
	 * Recycling method. See SimEvent for more details.
	 * 
	 * @throws SimulationException
	 */
	public void setForObject(Object o, String method) throws SimulationException {
		eventType = null;

		Class<?> cl = o.getClass();
		while (cl != null)
			try {
				methodInvoker = cl.getDeclaredMethod(method, null);
				return;
			} catch (NoSuchMethodException e) {
				cl = cl.getSuperclass();
			} catch (SecurityException e) {
				System.out.println("Method: " + method);
				System.out.println("SimSimpleEvent -> SecurityException: "
						+ e.getMessage());
				printStackTrace(e);
			}

		if (methodInvoker == null)
			throw new SimulationException("SimSimpleEvent didn't find method "
					+ method);
	}

	/** Recycling method. See SimEvent for more details. */
	public void setForObject(Object o, Enum<?> actionType) {		
		methodInvoker = null;
		eventType = actionType;
	}

	/** Return a string describing event. */
	public String toString() {
		if (methodInvoker != null)
			return "[@" + getTime() + "->" + methodInvoker.toString() + "]";
		else
			return "[@" + getTime() + "->" + object.toString() + "."
					+ eventType + "]";
	}

	/** Fire event calling the target object. */
	public void fireEvent() {
		if (methodInvoker != null) {
			try {
				methodInvoker.invoke(object, null);
			} catch (InvocationTargetException e) {
				System.out.println("Object " + methodInvoker + " Method: "
						+ methodInvoker.getName());
				System.out
						.println("SimSimpleEvent.fireEvent -> InvocationTargetException: "
								+ e.getTargetException().toString());
				printStackTrace(e);
			} catch (IllegalAccessException e) {
				System.out.println("Object " + methodInvoker + " Method: "
						+ methodInvoker.getName());
				System.out
						.println("SimSimpleEvent.fireEvent -> IllegalAccessException: "
								+ e.getMessage());
				printStackTrace(e);
			}
		} else {
			EventListener evL = (EventListener) object;
			evL.onEvent(eventType);			
		}
	}

	private void printStackTrace(Exception e) {
		for (int i = 0; i < e.getStackTrace().length; i++)
			System.out.println(e.getStackTrace()[i].toString());
	}
}
