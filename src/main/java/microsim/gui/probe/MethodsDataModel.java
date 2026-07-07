package microsim.gui.probe;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.*;

/**
 * Not of interest for users.
 * A data model used to show the list of methods into the probe.
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
 *         <p>
 */
public class MethodsDataModel implements ListModel {

    private static final Logger log = LogManager.getLogger(MethodsDataModel.class);

    private List<Method> methods;
    private Object targetObj;
    private boolean viewPrivate;
    private List<Object> probeFields;

    private int deepLevel = 0;

    public MethodsDataModel(Object o) {
        methods = new ArrayList<Method>();
        targetObj = o;
        viewPrivate = true;
        try {
            probeFields = ((IProbeFields) o).getProbeFields();
        } catch (Exception e) {
            log.error(
                    "Error creating MethodsDataModel: " + e.getMessage());
        }
        updateWithFields();
    }

    public MethodsDataModel(Object o, boolean privateVariables) {
        methods = new ArrayList<Method>();
        targetObj = o;
        viewPrivate = privateVariables;
        update(viewPrivate);
    }

    public void update() {
        if (probeFields == null)
            update(viewPrivate);
        else
            updateWithFields();
    }

    public void setViewPrivate(boolean privateVariables) {
        viewPrivate = privateVariables;
    }

    private void update(boolean privateVariables) {
        viewPrivate = privateVariables;
        methods.clear();

        Class<?> cl = targetObj.getClass();
        for (int i = 0; i < deepLevel; i++)
            cl = cl.getSuperclass();

        Method[] meth = cl.getDeclaredMethods();
        AccessibleObject.setAccessible(meth, true);

        for (int i = 0; i < meth.length; i++)
            if (viewPrivate || Modifier.isPublic(meth[i].getModifiers()))
                methods.add(meth[i]);
    }

    public void setDeepLevel(int level) {
        deepLevel = level;
    }

    private void updateWithFields() {
        methods.clear();

        Class<?> cl = targetObj.getClass();

        while (cl != null) {
            Method[] meth = cl.getDeclaredMethods();
            AccessibleObject.setAccessible(meth, true);

            for (int i = 0; i < meth.length; i++)
                if (probeFields.contains(meth[i].getName()))
                    methods.add(meth[i]);

            cl = cl.getSuperclass();
        }
    }

    public int getSize() {
        return methods.size();
    }

    public Object getElementAt(int index) {
        return methods.get(index);
    }

    public void invokeMethodAt(int index) {
        Method m = (Method) methods.get(index);

        if (m.getParameterTypes().length > 0) {
            JOptionPane.showMessageDialog(null, "Method requires parameters",
                    "Method result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Object o = m.invoke(targetObj, null);

            if (o == null)
                return;

            JOptionPane.showMessageDialog(null, o.toString(),
                    "Method result", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            System.out.println("Error in method.invoke:" + e.getMessage());
        }
    }

    public void invokeMethodAt(int index, Object[] params) {
        Method m = (Method) methods.get(index);

        try {
            Object o = m.invoke(targetObj, params);

            if (o == null)
                return;

            JOptionPane.showMessageDialog(null, o.toString(),
                    "Method result", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            System.out.println("Error in method.invoke:" + e.getMessage());
        }
    }

    public void addListDataListener(ListDataListener l) {
    }

    public void removeListDataListener(ListDataListener l) {
    }
}
