/* (C) Copyright 2026, by Ross Richardson
 *
 * Tests the changes reported when the desktop parameter editor saves a value.
 *
 * @author ross richardson
 */
package microsim.gui.shell.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import microsim.annotation.GUIparameter;
import microsim.data.GUIParameterHistory.ParameterValue;
import org.junit.jupiter.api.Test;
import org.metawidget.swing.SwingMetawidget;

public class ParameterFrameHistoryTest {
    @GUIparameter
    private String label = "original";

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @Test
    void recordsAnEditOnlyWhenItIsApplied() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ParameterFrame frame = new ParameterFrame(this);
            try {
                JScrollPane scroll = (JScrollPane) frame.getContentPane().getComponent(0);
                SwingMetawidget editor = (SwingMetawidget) scroll.getViewport().getView();
                assertTrue(frame.saveAndGetChanges().isEmpty());
                editor.setValue("edited", "label");
                assertEquals("original", label);
                assertEquals(List.of(new ParameterValue(getClass().getName(), "label", "edited")),
                        frame.saveAndGetChanges());
                assertEquals("edited", label);
                assertTrue(frame.saveAndGetChanges().isEmpty());
            } catch (IllegalAccessException e) {
                throw new AssertionError("Could not read the fixture's GUI parameter", e);
            } finally {
                frame.dispose();
            }
        });
    }
}
