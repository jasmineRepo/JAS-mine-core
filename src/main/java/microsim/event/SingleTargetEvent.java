package microsim.event;

import lombok.NonNull;
import microsim.exception.SimulationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The simpler implementation of SimEvent class. It represents an event to be notified only to one specific object. It
 * is often used in discrete event simulations, when an object schedule itself for a future event.
 */
public class SingleTargetEvent extends Event {

    protected Enum<?> eventType;
    protected Method methodInvoker;

    protected Object object;

    /**
     * Create new event using late binding.
     *
     * @throws SimulationException
     */
    public SingleTargetEvent(final @NonNull Object object, final @NonNull String method) throws SimulationException {
        this.object = object;
        setForObject(object, method);
    }

    /**
     * Create new event using early binding.
     */
    public SingleTargetEvent(final @NonNull Object object, final @NonNull Enum<?> actionType) {
        this.object = object;
        setForObject(object, actionType);
    }

    /**
     * Recycling method. See SimEvent for more details.
     *
     * @throws SimulationException
     */
    public void setForObject(final @NonNull Object o, final @NonNull String method) throws SimulationException {
        eventType = null;

        Class<?> cl = o.getClass();
        while (cl != null)
            try {
                methodInvoker = cl.getDeclaredMethod(method, (Class<?>) null);
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

    /**
     * Recycling method. See SimEvent for more details.
     */
    public void setForObject(final @NonNull Object o, final @NonNull Enum<?> actionType) {
        methodInvoker = null;
        eventType = actionType;
    }

    /**
     * Return a string describing event.
     */
    public @NonNull String toString() {
        if (methodInvoker != null) return "[@" + getTime() + "->" + methodInvoker.toString() + "]";
        else return "[@" + getTime() + "->" + object.toString() + "." + eventType + "]";
    }

    /**
     * Fire event calling the target object.
     */
    public void fireEvent() {
        if (methodInvoker != null) {
            try {
                methodInvoker.invoke(object, (Object) null);
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

    private void printStackTrace(final @NonNull Exception e) {
        for (int i = 0; i < e.getStackTrace().length; i++)
            System.out.println(e.getStackTrace()[i].toString());
    }
}
