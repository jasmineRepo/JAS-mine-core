package microsim.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

	public static boolean isStringSource(Class<?> trgClass, String varName,
			boolean isMethod) {
		if (!isMethod) {
			Field fld = searchField(trgClass, varName);
			if (fld == null)
				return false;
			else
				return (fld.getType() == String.class);
		} else {
			Method mtd = searchMethod(trgClass, varName);

			if (mtd == null)
				return false;
			else
				return (mtd.getReturnType() == String.class);
		}
	}

	public static boolean isDoubleSource(Class<?> trgClass, String varName,
			boolean isMethod) {
		if (!isMethod) {
			Field fld = searchField(trgClass, varName);
			if (fld == null)
				return false;
			else
				return (fld.getType() == double.class);
		} else {
			Method mtd = searchMethod(trgClass, varName);

			if (mtd == null)
				return false;
			else
				return (mtd.getReturnType() == double.class);
		}
	}

	public static boolean isLongSource(Class<?> trgClass, String varName,
			boolean isMethod) {
		if (!isMethod) {
			Field fld = searchField(trgClass, varName);

			if (fld == null)
				return false;
			else
				return (fld.getType() == long.class);
		} else {
			Method mtd = searchMethod(trgClass, varName);

			if (mtd == null)
				return false;
			else
				return (mtd.getReturnType() == long.class);
		}
	}

	public static boolean isIntSource(Class<?> trgClass, String varName,
			boolean isMethod) {
		if (!isMethod) {
			Field fld = searchField(trgClass, varName);

			if (fld == null)
				return false;
			else
				return (fld.getType() == int.class);
		} else {
			Method mtd = searchMethod(trgClass, varName);

			if (mtd == null)
				return false;
			else
				return (mtd.getReturnType() == int.class);
		}
	}

	public static Field searchField(Class<?> trgClass, String fieldName) {
		Class<?> cl = trgClass;
		while (cl != null)
			try {
				Field[] flds = cl.getDeclaredFields();
				AccessibleObject.setAccessible(flds, true);
                for (Field fld : flds)
                    if (fld.getName().equals(fieldName))
                        return fld;

				cl = cl.getSuperclass();
			} catch (SecurityException e) {
				System.out.println("Field: " + fieldName);
				System.out.println("DoubleInvoker -> SecurityException: "
						+ e.getMessage());
				e.printStackTrace();
			}

		return null;
	}

	public static Method searchMethod(Class<?> trgClass, String methodName) {
		Class<?> cl = trgClass;

		while (cl != null)
			try {
				Method[] mtds = cl.getDeclaredMethods();
				AccessibleObject.setAccessible(mtds, true);
                for (Method mtd : mtds)
                    if (mtd.getName().equals(methodName))
                        return mtd;

				cl = cl.getSuperclass();
			} catch (SecurityException e) {
				System.out.println("Method: " + methodName);
				System.out.println("DoubleInvoker -> SecurityException: "
						+ e.getMessage());
				e.printStackTrace();
			}

		return null;
	}

}
