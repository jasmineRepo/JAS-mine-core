package microsim.data;

import java.util.Collection;

import microsim.data.db.DatabaseUtils;

public class DataExport {

	private ExportCSV csvExport;
	private boolean toDatabase;
	private boolean toCSV;
	private Object sourceObject;
	private Collection<?> collectionSource;
	
	public DataExport(Collection<?> sourceCollection, boolean exportToDatabase, boolean exportToCSVfile) {
		this.collectionSource = sourceCollection;
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		if(toCSV) {
			csvExport = new ExportCSV(collectionSource); 
		}
	}
	
	public DataExport(Object sourceSingleObject, boolean exportToDatabase, boolean exportToCSVfile) {
		sourceObject = sourceSingleObject; 
		toDatabase = exportToDatabase;
		toCSV = exportToCSVfile;
		
		if(toCSV) {
			csvExport = new ExportCSV(sourceObject); 
		}
	}
		
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
