package microsim.reflection;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static boolean isStringSource(final @Nullable Class<?> targetClass, final @NonNull String varName,
                                         final boolean isMethod) {
        if (!isMethod) {
            val fld = searchField(targetClass, varName);
            return fld != null && fld.getType() == String.class;
        } else {
            val mtd = searchMethod(targetClass, varName);
            return mtd != null && mtd.getReturnType() == String.class;
        }
    }

    public static boolean isDoubleSource(final @Nullable Class<?> targetClass, final @NonNull String varName,
                                         final boolean isMethod) {
        if (!isMethod) {
            val fld = searchField(targetClass, varName);
            return fld != null && fld.getType() == double.class; // todo what if this is Double or Integer or anything like this?
        } else {
            val mtd = searchMethod(targetClass, varName);
            return mtd != null && mtd.getReturnType() == double.class;
        }
    }

    public static boolean isLongSource(final @Nullable Class<?> targetClass, final @NonNull String varName,
                                       final boolean isMethod) {
        if (!isMethod) {
            val fld = searchField(targetClass, varName);
            return fld != null && fld.getType() == long.class;
        } else {
            val mtd = searchMethod(targetClass, varName);
            return mtd != null && mtd.getReturnType() == long.class;
        }
    }

    public static boolean isIntSource(final @Nullable Class<?> targetClass, final @NonNull String varName,
                                      final boolean isMethod) {
        if (!isMethod) {
            val fld = searchField(targetClass, varName);
            return fld != null && fld.getType() == int.class;
        } else {
            val mtd = searchMethod(targetClass, varName);
            return mtd != null && mtd.getReturnType() == int.class;
        }
    }

    public static @Nullable Field searchField(final @Nullable Class<?> targetClass, final @NonNull String fieldName) {
        var cl = targetClass;
        while (cl != null)
            try {
                val flds = cl.getDeclaredFields();
                AccessibleObject.setAccessible(flds, true);
                for (Field fld : flds) if (fld.getName().equals(fieldName)) return fld;

                cl = cl.getSuperclass();
            } catch (SecurityException e) {
                System.out.println("Field: " + fieldName);
                System.out.println("Invoker -> SecurityException: " + e.getMessage());
                e.printStackTrace();
            }

        return null;
    }

    public static @Nullable Method searchMethod(final @Nullable Class<?> targetClass, final @NonNull String methodName) {
        var cl = targetClass;

        while (cl != null)
            try {
                val mtds = cl.getDeclaredMethods();
                AccessibleObject.setAccessible(mtds, true);
                for (Method mtd : mtds) if (mtd.getName().equals(methodName)) return mtd;

                cl = cl.getSuperclass();
            } catch (SecurityException e) {
                System.out.println("Method: " + methodName);
                System.out.println("Invoker -> SecurityException: " + e.getMessage());
                e.printStackTrace();
            }

        return null;
    }

}
