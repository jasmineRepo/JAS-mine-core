package microsim.data;

import microsim.data.db.DatabaseUtils;

import java.util.Collection;

/**
 * DataExport is a class that handles the exporting to data to an output database and/or .csv files.  Note that only numbers, enums 
 * or strings are exported to .csv files.
 * 
 * @author Ross Richardson
 *
 */
public class DataExport {

	private ExportToCSV csvExport;
	private final boolean toDatabase;
	private final boolean toCSV;
	private Object targetObject;
	private Collection<?> collectionTarget;
	
	/**
	 * Create a DataExport object to handle the exporting of a collection of objects to an output database and/or .csv files.  Note 
	 * that only numbers, enums or strings are exported to .csv files.
	 * 
	 * @param targetCollection - a collection of objects whose fields (including private and inherited) will be exported
	 * @param exportToDatabase - set to true if the user wants to export to an output database
	 * @param exportToCSVfile - set to true if the user wants to export to .csv files named after the class name of the targetCollection
	 */
	public DataExport(Collection<?> targetCollection, boolean exportToDatabase, boolean exportToCSVfile) {
		collectionTarget = targetCollection;
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		if(toCSV) {
			csvExport = new ExportToCSV(collectionTarget);
		}
	}

	/**
	 * 
	 * Create a DataExport object to handle the exporting of an object to an output database and/or .csv files.  Note 
	 * that only numbers, enums or strings are exported to .csv files.
	 * 
	 * @param targetSingleObject - an object whose fields (including private and inherited) will be exported
	 * @param exportToDatabase - set to true if the user wants to export to an output database
	 * @param exportToCSVfile - set to true if the user wants to export to .csv files named after the class name of the targetCollection
	 */
	public DataExport(Object targetSingleObject, boolean exportToDatabase, boolean exportToCSVfile) {
		targetObject = targetSingleObject; 
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		if(toCSV) {
			csvExport = new ExportToCSV(targetObject);
		}
	}
		
	/**
	 * Export the values of the fields.
	 */
	public void export() {
		if(toCSV) {
			csvExport.dumpToCSV();
		}
		
		if(toDatabase) {
			try {
				if(collectionTarget != null) {	
					DatabaseUtils.snap(collectionTarget);
				}
				else if(targetObject != null) {
					DatabaseUtils.snap(targetObject);
				}
				else throw new NullPointerException();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
