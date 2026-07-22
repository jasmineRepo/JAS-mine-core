package microsim.gui.probe;

import java.net.URL;
import java.util.ArrayList;

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
public class ParameterManager {
    URL fileName;
    Object targetObject;
    ArrayList<String> fields;

    public ParameterManager(Object target, URL filePath) {
        targetObject = target;
        fileName = filePath;
        fields = new ArrayList<String>();
    }

    public void addParameterField(String fieldName) {
        boolean flag = true;
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i).toString().equals(fieldName))
                flag = false;

        if (flag)
            fields.add(fieldName);
    }

    public void readParamsFromFile() {
    }

    public void readParamsFromObject() {
    }

    public void updateParameters() {
    }

    public void saveParameters() {
    }

}
