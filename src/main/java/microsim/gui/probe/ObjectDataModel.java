package microsim.gui.probe;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;

import java.lang.reflect.*;

/**
 * Not of interest for users.
 * A data model used to contain the list of elements within a collection.
 * It is used by the Probe frame.
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
public class ObjectDataModel extends AbstractTableModel {

    private static final Logger log = LogManager.getLogger(ObjectDataModel.class);

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private static final int COLUMNS = 3;

    private static final int COL_FIELD = 0;
    private static final int COL_VALUE = 1;
    private static final int COL_TYPE = 2;

    private boolean isAnArray;
    Object targetObj;
    Object[][] data;

    public ObjectDataModel(Object objToInspect) {

        if (objToInspect.getClass().isArray() || ProbeReflectionUtils.isCollection(objToInspect.getClass())) {
            targetObj = objToInspect;
            update();
        } else
            log.error(
                    "You were trying to build an ObjectDataModel passing a wrong object type");
    }

    public void update() {
        if (isAnArray = targetObj.getClass().isArray())
            updateAnArray();
        else
            updateAList();
    }

    private void updateAnArray() {
        // Object[] objs = (Object[]) targetObj;

        data = new Object[Array.getLength(targetObj)][COLUMNS];
        for (int i = 0; i < Array.getLength(targetObj); i++) {
            Object o = Array.get(targetObj, i);
            data[i][COL_VALUE] = o.toString();
            data[i][COL_TYPE] = o.getClass().getName();
            data[i][COL_FIELD] = o;
        }
    }

    private void updateAList() {
        Collection<?> c = (Collection<?>) targetObj;

        data = new Object[c.size()][COLUMNS];
        Iterator<?> itr = c.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Object o = itr.next();
            data[i][COL_VALUE] = o.toString();
            data[i][COL_TYPE] = o.getClass().getName();
            data[i][COL_FIELD] = o;
            i++;
        }
    }

    public String getHeaderText(int column) {
        switch (column + 1) {
            case COL_VALUE:
                return "Value";
            case COL_TYPE:
                return "Type";
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
            Object f = data[row][COL_FIELD];

            if (f == null) {
                JOptionPane.showMessageDialog(null, "The variable is null.\n It is impossible to edit.",
                        "Probe editing variable", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (ProbeReflectionUtils.isEditable(f.getClass()))
                if (isAnArray)
                    setPrimitiveValueToArray(f, val, row);
                else
                    ProbeReflectionUtils.setValueToObject(f, val);
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

    private void setPrimitiveValueToArray(Object o, Object val, int row) {
        if (o instanceof String) {
            Array.set(targetObj, row, val);
            return;
        }
        if (o instanceof Integer) {
            Array.setInt(targetObj, row, Integer.parseInt(val.toString()));
            return;
        }
        if (o instanceof Double) {
            Array.setDouble(targetObj, row, Double.parseDouble(val.toString()));
            return;
        }
        if (o instanceof Boolean) {
            Array.setBoolean(targetObj, row, Boolean.valueOf(val.toString()).booleanValue());
            return;
        }
        if (o instanceof Byte) {
            Array.setByte(targetObj, row, Byte.parseByte(val.toString()));
            return;
        }
        if (o instanceof Character) {
            Array.setChar(targetObj, row, val.toString().charAt(0));
            return;
        }
        if (o instanceof Float) {
            Array.setFloat(targetObj, row, Float.parseFloat(val.toString()));
            return;
        }
        if (o instanceof Long) {
            Array.setLong(targetObj, row, Long.parseLong(val.toString()));
            return;
        }
        if (o instanceof Short) {
            Array.setShort(targetObj, row, Short.parseShort(val.toString()));
            return;
        }
    }

    public Object getObjectAtRow(int row) {
        try {
            return data[row][COL_FIELD];
        } catch (Exception e) {
            return null;
        }
    }

    public String getObjectNameAtRow(int row) {
        try {
            return data[row][COL_FIELD].getClass().getName();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isCellEditable(int row, int col) {
        // if (!isAnArray)
        // return false;

        if ((col + 1) == COL_VALUE)
            return true;
        else
            return false;

    }

    public Object getProbedObject() {
        return targetObj;
    }
}
