package microsim.gui.probe;

import java.lang.reflect.*;

/**
 * A collection of static methods using the java reflection
 * to manipulate objects.
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
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 */
public class ProbeReflectionUtils {

    /**
     * Test if the given class implements the java.util.Collection interface.
     * The whole hierarchy of the class is tested.
     * 
     * @param f The class to be tested.
     * @return True if the class is a Collection, false otherwise.
     */
    public static boolean isCollection(Class<?> f) {
        Class<?>[] intf = f.getInterfaces();
        boolean flag = false;

        for (int i = 0; i < intf.length && !flag; i++) {
            if (intf[i].getName() == "java.util.Collection")
                flag = true;
            else if (intf[i].getInterfaces().length > 0)
                if (isCollection(intf[i]))
                    flag = true;
        }

        return flag;
    }

    /**
     * Test if the given class is a wrapper for a native type or a string.
     * It could be a Double, Long, Integer, Float, String, Character or Boolean.
     * 
     * @param c The class to be tested.
     * @return True if class is of a native type, false in any other case.
     */
    public static boolean isEditable(Class<?> c) {
        if (c.getSuperclass() == null)
            return false;

        return ((c.getSuperclass().getName().equals("java.lang.Number")) ||
                (c.getName().equals("java.lang.String")) ||
                (c.getName().equals("java.lang.Character")) ||
                (c.getName().equals("java.lang.Boolean")));
    }

    /**
     * Test if the given object is a wrapper for a native type or a string.
     * It could be a Byte, Double, Long, Short, Integer, Float, String,
     * Character or Boolean.
     * 
     * @param o The object to be tested.
     * @return True if class is of a native type, false in any other case.
     */
    public static boolean isEditable(Object o) {
        return isEditable(o.getClass());
    }

    /**
     * Test if the given method requires parameters of a native type.
     * In this case the method can be executed.
     * 
     * @param m The method to be tested.
     * @return True if the method requires only native-type parameters.
     *         A parameter is native type if int, long, ... or its corresponding
     *         wrapper class (Integer, Double, Long, ...).
     */
    public static boolean isAnExecutableMethod(Method m) {
        Class<?>[] cl = m.getParameterTypes();

        for (int i = 0; i < cl.length; i++)
            if (!isEditable(cl[i]) && !cl[i].isPrimitive())
                return false;

        return true;
    }

    /**
     * Set a given value wrapped by an Object into the wrapper object.
     * 
     * @param o   The object to be updated. It must be of a native wrapper class
     *            (String, Double, Long, ...).
     * @param val An object whose toString() method return a valid format for
     *            the class type of object o.
     */
    public static void setValueToObject(Object o, Object val) {
        try {

            Field f = o.getClass().getDeclaredField("value");
            f.setAccessible(true);

            if (o instanceof String) {
                char[] ch = new char[val.toString().toCharArray().length];
                System.arraycopy(val.toString().toCharArray(), 0, ch, 0, ch.length);
                f.set(o, (Object) ch);

                f = o.getClass().getDeclaredField("count");
                f.setAccessible(true);
                f.set(o, ch.length);

                return;
            }
            if (o instanceof Integer) {
                f.setInt(o, Integer.parseInt(val.toString()));
                return;
            }
            if (o instanceof Double) {
                f.setDouble(o, Double.parseDouble(val.toString()));
                return;
            }
            if (o instanceof Boolean) {
                f.setBoolean(o, Boolean.valueOf(val.toString()).booleanValue());
                return;
            }
            if (o instanceof Byte) {
                f.setByte(o, Byte.parseByte(val.toString()));
                return;
            }
            if (o instanceof Character) {
                f.setChar(o, val.toString().charAt(0));
                return;
            }
            if (o instanceof Float) {
                f.setFloat(o, Float.parseFloat(val.toString()));
                return;
            }
            if (o instanceof Long) {
                f.setLong(o, Long.parseLong(val.toString()));
                return;
            }
            if (o instanceof Short) {
                f.setShort(o, Short.parseShort(val.toString()));
                return;
            }
        } catch (Exception e) {
            System.out.println("Err:" + e.getMessage());
        }
    }
}
