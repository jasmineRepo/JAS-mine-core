package microsim.gui.shell.parameter;

import java.awt.Component;
import java.util.Map;

import javax.swing.JComponent;

import org.metawidget.swing.SwingMetawidget;

public class DescriptiveSwingMetawidget extends SwingMetawidget {

    private static final long serialVersionUID = 1L;

    @Override
    protected void layoutWidget(Component component, String elementName,
            Map<String, String> attributes) {

        super.layoutWidget(component, elementName, attributes);

        if (component == null)
            return;

        ((JComponent) component).setToolTipText(attributes.get("tooltip"));
    }

}
