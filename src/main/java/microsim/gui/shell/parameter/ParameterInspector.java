package microsim.gui.shell.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import microsim.annotation.GUIparameter;
import microsim.annotation.ModelParameter;

import org.metawidget.inspector.iface.Inspector;

public class ParameterInspector implements Inspector {

    // @Override
    public String inspect(Object object, String arg1, String... arg2) {
        Class<?> clazz = object.getClass();

        StringBuffer buf = new StringBuffer();
        buf.append("<inspection-result xmlns=\"http://metawidget.org/inspection-result\" ");
        buf.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        buf.append("version=\"1.0\" ");
        buf.append("xsi:schemaLocation=\"http://metawidget.org/inspection-result ");
        buf.append("http://metawidget.org/xsd/inspection-result-1.0.xsd\">");
        buf.append("<entity type=\"").append(clazz.getCanonicalName()).append("\" >");

        List<Field> fields = ParameterInspector.extractModelParameters(clazz);
        for (Field field : fields) {
            StringBuffer extra = new StringBuffer();

            if (field.getType().isEnum()) {
                String comma = "";
                extra.append("lookup=\"");
                for (Object constz : field.getType().getEnumConstants()) {
                    extra.append(comma).append(constz);
                    comma = ",";
                }
                extra.append("\"");
            }

            if (field.getType().equals(Boolean.class)) {
                buf.append("<property name=\"").append(field.getName()).append("\" ");
                buf.append("type=\"boolean\" ");
                buf.append(extra.toString());
                buf.append("/>");
            } else {
                buf.append("<property name=\"").append(field.getName()).append("\" ");
                buf.append("type=\"").append(field.getType().getCanonicalName()).append("\" ");
                buf.append(extra.toString());
                buf.append("/>");
            }
        }

        buf.append("</entity>");
        buf.append("</inspection-result>");

        return buf.toString();
    }

    public static List<Field> extractModelParameters(Class<?> clazz) {
        List<Field> collectedFields = new ArrayList<Field>();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation annos[] = field.getAnnotations();
            for (Annotation anno : annos) {
                if ((anno.annotationType().equals(GUIparameter.class))
                        || (anno.annotationType().equals(ModelParameter.class))) {
                    collectedFields.add(field);
                }
            }
        }

        return collectedFields;
    }
}
