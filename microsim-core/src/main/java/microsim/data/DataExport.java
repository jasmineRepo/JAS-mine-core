package microsim.data;

import java.util.Collection;

import microsim.data.db.DatabaseUtils;

/**
 * DataExport is a class that handles the exporting to data to an output database and/or .csv files.  Note that only numbers, enums 
 * or strings are exported to .csv files.
 * 
 * @author Ross Richardson
 *
 */
public class DataExport {

	private ExportCSV csvExport;
	private boolean toDatabase;
	private boolean toCSV;
	private Object sourceObject;
	private Collection<?> collectionSource;
	
	/**
	 * Create a DataExport object to handle the exporting of a collection of objects to an output database and/or .csv files.  Note 
	 * that only numbers, enums or strings are exported to .csv files.
	 * 
	 * @param sourceCollection - a collection of objects whose fields (including private and inherited) will be exported
	 * @param exportToDatabase - set to true if the user wants to export to an output database
	 * @param exportToCSVfile - set to true if the user wants to export to .csv files named after the class name of the sourceCollection
	 */
	public DataExport(Collection<?> sourceCollection, boolean exportToDatabase, boolean exportToCSVfile) {
		this.collectionSource = sourceCollection;
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		if(toCSV) {
			csvExport = new ExportCSV(collectionSource); 
		}
	}

	/**
	 * 
	 * Create a DataExport object to handle the exporting of an object to an output database and/or .csv files.  Note 
	 * that only numbers, enums or strings are exported to .csv files.
	 * 
	 * @param sourceSingleObject - an object whose fields (including private and inherited) will be exported
	 * @param exportToDatabase - set to true if the user wants to export to an output database
	 * @param exportToCSVfile - set to true if the user wants to export to .csv files named after the class name of the sourceCollection
	 */
	public DataExport(Object sourceSingleObject, boolean exportToDatabase, boolean exportToCSVfile) {
		sourceObject = sourceSingleObject; 
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		
		if(toCSV) {
			csvExport = new ExportCSV(sourceObject); 
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
				if(collectionSource != null) {	
					DatabaseUtils.snap(collectionSource);
				}
				else if(sourceObject != null) {
					DatabaseUtils.snap(sourceObject);
				}
				else throw new NullPointerException();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
