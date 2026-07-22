package microsim.gui.probe;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;

import java.util.List;

/**
 * Not of interest for users.
 * A data model used to show the list of variables into the probe.
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
public class VariableDataModel extends AbstractTableModel {

    private static final Logger log = LogManager.getLogger(VariableDataModel.class);

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private static final int COLUMNS = 4;

    private static final int COL_FIELD = 0;
    private static final int COL_NAME = 1;
    private static final int COL_TYPE = 2;
    private static final int COL_VALUE = 3;

    private Object targetObj;
    private Object[][] data;
    private boolean viewPrivate;
    private List<Object> probeFields;

    private int deepLevel = 0;

    public VariableDataModel(Object objToInspect) {
        targetObj = objToInspect;
        viewPrivate = true;
        try {
            probeFields = ((IProbeFields) objToInspect).getProbeFields();
        } catch (Exception e) {
            log.error("Error creating VariableDataModel: " + e.getMessage());
        }
        updateWithFields();
    }

    public VariableDataModel(Object objToInspect, boolean privateVariables) {
        targetObj = objToInspect;
        viewPrivate = privateVariables;
        probeFields = null;
        update(viewPrivate);
    }

    private void updateWithFields() {
        Class<?> cl = targetObj.getClass();
        Field[] fields;
        int k = 0;

        // Searching for rowCount
        while (cl != null) {
            fields = cl.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            for (int i = 0; i < fields.length; i++)
                if (probeFields.contains(fields[i].getName()))
                    k++;

            cl = cl.getSuperclass();
        }

        // Now fill up the fields
        data = new Object[k][COLUMNS];
        cl = targetObj.getClass();
        k = 0;
        while (cl != null) {
            fields = cl.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);

            for (int i = 0; i < fields.length; i++)
                if (probeFields.contains(fields[i].getName())) {
                    Field f = fields[i];
                    data[k][COL_NAME] = f.getName();
                    data[k][COL_TYPE] = f.getType();

                    try {
                        Object o = f.get(targetObj);
                        if (o != null) {
                            data[k][COL_FIELD] = f;
                            if (ProbeReflectionUtils.isCollection(f.getType()) || o.getClass().isArray())
                                data[k][COL_VALUE] = "[...]";
                            else
                                data[k][COL_VALUE] = o.toString();
                        }
                    } catch (Exception e) {
                        System.out.println("Error in field :" + e.getMessage());
                    }
                    ;
                    k++;
                }

            cl = cl.getSuperclass();
        }
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

    public void setDeepLevel(int level) {
        deepLevel = level;
    }

    private void update(boolean privateVariables) {
        Class<?> cl = targetObj.getClass();
        for (int i = 0; i < deepLevel; i++)
            cl = cl.getSuperclass();

        Field[] fields;
        if (viewPrivate)
            fields = cl.getDeclaredFields();
        else
            fields = cl.getFields();
        AccessibleObject.setAccessible(fields, true);

        data = new Object[fields.length][COLUMNS];
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            data[i][COL_NAME] = f.getName();
            data[i][COL_TYPE] = f.getType();

            try {
                Object o = f.get(targetObj);
                if (o != null) {
                    data[i][COL_FIELD] = f;
                    if (ProbeReflectionUtils.isCollection(f.getType()) || o.getClass().isArray())
                        data[i][COL_VALUE] = "[...]";
                    else
                        data[i][COL_VALUE] = o.toString();
                }
            } catch (Exception e) {
                System.out.println("Error in field :" + e.getMessage());
            }
            ;
        }
    }

    public String getHeaderText(int column) {
        switch (column + 1) {
            case COL_NAME:
                return "Name";
            case COL_TYPE:
                return "Type";
            case COL_VALUE:
                return "Value";
            default:
                return "";
        }
    }

    public int getColumnCount() {
        if (data == null)
            return 0;
        else
            return COLUMNS - 1;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col + 1];
    }

    public int getRowCount() {
        if (data == null)
            return 0;
        else
            return data.length;
    }

    public void setValueAt(Object val, int row, int col) {

        try {
            Field f = (Field) data[row][COL_FIELD];

            if (f == null) {
                JOptionPane.showMessageDialog(null, "The variable is null.\n It is impossible to edit.",
                        "Probe editing variable", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (f.getType().isPrimitive() || f.getType().getName().equals("java.lang.String"))
                setPrimitiveValueToClass(f.getType(), val, f);
            else {
                JOptionPane.showMessageDialog(null, "The variable is not a primitive.\n" +
                        "To edit its value you can open a probe to it. ",
                        "Probe editing variable", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

        } catch (Exception e) {
            System.out.println("Error setting field: " + e.getMessage());
            return;
        }
        // Indicate the change has happened:
        data[row][col + 1] = val;
        fireTableDataChanged();
    }

    private void setPrimitiveValueToClass(Class<?> cl, Object val, Field f) {
        try {
            if (cl.getName().equals("java.lang.String")) {
                f.set(targetObj, val.toString());
                return;
            }
            if (cl == Integer.TYPE) {
                f.setInt(targetObj, Integer.parseInt(val.toString()));
                return;
            }
            if (cl == Double.TYPE) {
                f.setDouble(targetObj, Double.parseDouble(val.toString()));
                return;
            }
            if (cl == Boolean.TYPE) {
                f.setBoolean(targetObj, Boolean.valueOf(val.toString()).booleanValue());
                return;
            }
            if (cl == Byte.TYPE) {
                f.setByte(targetObj, Byte.parseByte(val.toString()));
                return;
            }
            if (cl == Character.TYPE) {
                f.setChar(targetObj, val.toString().charAt(0));
                return;
            }
            if (cl == Float.TYPE) {
                f.setFloat(targetObj, Float.parseFloat(val.toString()));
                return;
            }
            if (cl == Long.TYPE) {
                f.setLong(targetObj, Long.parseLong(val.toString()));
                return;
            }
            if (cl == Short.TYPE) {
                f.setShort(targetObj, Short.parseShort(val.toString()));
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    public Object getObjectAtRow(int row) {
        try {
            return ((Field) data[row][COL_FIELD]).get(targetObj);
        } catch (Exception e) {
            return null;
        }
    }

    public String getObjectNameAtRow(int row) {
        try {
            return ((Field) data[row][COL_FIELD]).getName();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isCellEditable(int row, int col) {
        if ((col + 1) == COL_VALUE)
            return true;
        else
            return false;

    }
}
