package microsim.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.Transient;

import org.apache.log4j.Logger;

import microsim.data.db.DatabaseUtils;
import microsim.data.db.PanelEntityKey;
import microsim.engine.SimulationEngine;

/**
 * ExportCSV class allows the exporting of data to .csv files.  This is a useful alternative to exporting to an output database, as it is faster and produces
 * separate files for each class of object.  Note that only numbers, enums or strings are exported to .csv files. 
 *  
 * @author Ross Richardson
 *
 */
public class ExportCSV {

	private static Logger log = Logger.getLogger(DatabaseUtils.class);
	public static Long autoincrementSeed = 1000000L;
	
	//Fields for exporting tables to output .csv files 
	final static String newLine = "\n";
	final static String delimiter = ","; 
	final static String directory = SimulationEngine.getInstance().getCurrentExperiment().getOutputFolder() + File.separator + "csv";
	
	
	private Set<String> fieldsForExport;
	private BufferedWriter bufferWriter;
//	private FileWriter fileWriter;// = null;
	private String idFieldName;
	
	private Collection<?> targetCollection;		//Use if target is a Collection (iterate across the collection).  Null if a single object is the target.
	
	private Object targetObject;		//Use for a single target (no iteration across a collection).  Null if the target is a collection.
	private Field targetObjectIdField;	//Only for use with targetObject, not targetCollection (as each different object in a collection will have it's own id field)

	/**
	 * Allows the exporting of all fields (including private and inherited fields) of an object to a .csv file named after the object's class name. 
	 * Note that only numbers, enums or strings are exported to .csv files.  The serialVersionUID of a class will also not be exported.
	 * 
	 * @param target - the object whose fields will be exported to a .csv file with a name equal to the object's class name.  
	 * If the target is a Collection of objects, each member of the collection will have their individual fields exported to the .csv file, labelled by their id.
	 *  
	 */
	public ExportCSV(Object target) {
        try { 
        	
        	Object obj;
        	
        	//Decide which 'mode' depending on whether the target is a single object or a collection of objects
        	final boolean collectionMode;		//If target is collection, then will be set to true, otherwise remains false.
        	
        	//Set target
        	if(target instanceof Collection<?>) {   
        		collectionMode = true;
        	}
        	else collectionMode = false;
        		
        	if(collectionMode) {
	        	targetCollection = (Collection<?>) target; 
	        	obj = targetCollection.iterator().next();
        	}
        	else {        		
        		targetObject = target;
        		obj = targetObject;
        	}
        	
        	//Find id field
        	final Field[] objFields = obj.getClass().getDeclaredFields();
        	Field idField = null;
        	for(Field fld : objFields) {
        		if(fld.getType().equals(PanelEntityKey.class)) {		//Doesn't rely on the name of the field
        			idField = fld;
        			idFieldName = fld.getName();
        			if(!collectionMode) {
        				targetObjectIdField = fld;		//Set id field when single object is being exported (NOT collection!)
        				targetObjectIdField.setAccessible(true);
        			}
        			break;
        		}
        	}
//        	final Field idField = obj.getClass().getDeclaredField("id");	//Relies on the name of the field being 'id', which is not good if the user has another naming convention.
			if (idField != null) {
				idField.setAccessible(true);
			}
			else throw new IllegalArgumentException("Object of type "
					+ target.getClass() + " cannot be exported to .csv as it does not have a field of type PanelEntityKey.class or it is null!");
 
			//Set up file and fileWriter - create new file and directory if required
        	String filename;
        	if(collectionMode) {   
        		filename = obj.getClass().getSimpleName();
        	}
        	else {		//Use id of object to enumerate the name of .csv output files if several of the same object are for export.
					filename = obj.getClass().getSimpleName() + ((PanelEntityKey)idField.get(targetObject)).getId();
        	}
	
        	File f = new File(directory + File.separator + filename + ".csv");
        	//Checks whether a file with the same filename already exists - if not, then creates one.  Useful for MultiRun case.
        	boolean fAlreadyExists = f.exists(); 
//        	if(fAlreadyExists && !collectionMode) {
//        		throw new IllegalAccessException("A .csv file to export object " + obj.toString() 
//        				+ " already exists!  Cannot create more than one ExportCSV object for the same object!");
//        	}
//        	else
        	if(!fAlreadyExists)
        	{
				File dir = new File(directory);
				dir.mkdirs();
				f.createNewFile();
        	}
    	    bufferWriter = new BufferedWriter(new FileWriter(f, true));
        	if(!fAlreadyExists)
        	{
	    	    //Create Header line for .csv file
	    	  	bufferWriter.append("run" + delimiter + "time" + delimiter + "id_" + filename + delimiter);
        	}
        	
    	    //Create alphabetically sorted (except for run, time and id key) list of fields including private and inherited fields that belong to the target class.
			List<Field> declaredFields = new ArrayList<Field>();

    	    List<Field> allFields = ExportCSV.getAllFields(declaredFields, obj.getClass());
    	    
    	    TreeSet<String> nonTransientFieldNames = new TreeSet<String>();
    	    
    	    for(Field field : allFields) {
    	    	Transient transientAnnotation = field.getAnnotation(Transient.class);
    	    	if(transientAnnotation == null) {			//Ignore the field if it has the 'Transient' annotation, just like when exporting the data to the output database
    	    		if(field.getType().isPrimitive() || Number.class.isAssignableFrom(field.getType()) || field.getType().equals(String.class)|| field.getType().equals(Boolean.class) || field.getType().isEnum() || field.getType().equals(Character.class)) {

						String name = field.getName();

						/*
    	    			// PB 22/11/2021: if variable has @Column annotation, it would be better to output the name specified in the annotation, instead of the simple name of the variable. But the variables are obtained on the basis of their name, not the annotation, so this doesn't currently work.
						Column annotation = field.getAnnotation(Column.class);
						if (annotation != null) {
							Column columnAnnotation = (Column) annotation;
							name = columnAnnotation.name();
							if (name == null) {
								name = field.getName(); // If annotation name was null, revert to the simple variable name
							}
						}
						*/


    	    			if(!name.equals("serialVersionUID")) {
    	    			//	nonTransientFieldNames.add(field.getName());	//Exclude references to general Objects, including PanelEntityKeys (handle id separately).  Also ignores serialVersionUID value.
							nonTransientFieldNames.add(name); // PB: Add name kept in "name" variable, instead of field.getName() which gets the simple name.
    	    			}
    	    		}
    	    	}
    	    }    	  	
    	    fieldsForExport = new LinkedHashSet<String>();
    	    
    	    for(String fieldNames : nonTransientFieldNames) {		//Iterated in correct order
    	    	fieldsForExport.add(fieldNames);
    	    	if(!fAlreadyExists) {
    	    		bufferWriter.append(fieldNames + delimiter);
    	    	}
    	    }
        	        	        	
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * Export data to the .csv files named after the class of the target object 
	 * (or if a collection of objects, the class of the collection's members).
	 * Note that only numbers, enums or strings are exported to .csv files. 
	 * 
	 * @author Ross Richardson
	 * 
	 */
	public void dumpToCSV() {
 
		try {
			String run = ((Integer)SimulationEngine.getInstance().getCurrentRunNumber()).toString();
			String time = ((Double)SimulationEngine.getInstance().getTime()).toString();
            	
			if(targetCollection != null) {
	        	for(Object obj : targetCollection) {
	
	                bufferWriter.append(newLine);
	        		bufferWriter.append(run + delimiter + time + delimiter);
	        		
	        		Field idField = obj.getClass().getDeclaredField(idFieldName);
	        		idField.setAccessible(true);
					bufferWriter.append(((PanelEntityKey)idField.get(obj)).getId() + delimiter);
					
	        		for(String fieldName : fieldsForExport) {
	        			Field thisField = findUnderlyingField(obj.getClass(), fieldName);	        			
	        			thisField.setAccessible(true);
	        			Object value = thisField.get(obj);
	        			if(value == null) {
	        				bufferWriter.append("null");
	        			}
	        			else {
	        				bufferWriter.append(value.toString());	
	        			}
		            	bufferWriter.append(delimiter);
	        		}
	        	}
			}
			else if(targetObject != null) {
			    bufferWriter.append(newLine);
        		bufferWriter.append(run + delimiter + time + delimiter);
        		
//        		Field idField = targetObject.getClass().getDeclaredField(idFieldName);
//        		Field idField = targetObjectIdField;
				bufferWriter.append(((PanelEntityKey)targetObjectIdField.get(targetObject)).getId() + delimiter);
				
        		for(String fieldName : fieldsForExport) {
        			Field thisField = targetObject.getClass().getDeclaredField(fieldName);
        			thisField.setAccessible(true);
        			Object value = thisField.get(targetObject).toString();
        			if(value == null) {
        				bufferWriter.append("null");	
        			}
        			else {
        				bufferWriter.append(value.toString());	
        			} 
	            	bufferWriter.append(delimiter);
        		}
			}
			else throw new NullPointerException("ExportCSV's targetCollection and targetObject fields are both null!  Cannot export to CSV");
            
        } catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        finally {
        	try { 
        		bufferWriter.flush(); 
        	} catch (IOException e) {  
        			e.printStackTrace(); 
        	} 
        }
	}	
	
	private static Field findUnderlyingField(Class<?> clazz, String fieldName) {
	    Class<?> current = clazz;
	    do {
	       try {
	           return current.getDeclaredField(fieldName);
	       } catch(Exception e) {}
	    } while((current = current.getSuperclass()) != null);
	    return null;
	}

	//Recursive method to get all fields of a class, including inherited ones
	private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    fields.addAll(Arrays.asList(type.getDeclaredFields()));

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
		
}
