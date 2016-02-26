package microsim.data.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import microsim.data.MultiKeyCoefficientMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelAssistant {

	private static Object getCellValue(HSSFCell cell) {
		Object val = null;
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			val = cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			val = cell.getNumericCellValue();
			final Double d = (Double) val;
			if (d - d.intValue() == 0.0)
				val = d.intValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			val = cell.getBooleanCellValue();
		} else
			val = null;
		
		return val;
	}
	
	private static String getStringCellValue(HSSFCell cell) {
		Object val = null;
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			val = cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			val = getCellValue(cell) + "";
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			val = cell.getBooleanCellValue() + "";
		} else
			val = null;
		
		return MultiKeyCoefficientMap.toStringKey(val);
	}
	
	/**
	 * Load MultiKeyCoefficientMap from Excel spreadsheet data, reading from the first line of the spreadsheet
	 * @param excelFileName: the Excel workbook (.xls) that stores the data
	 * @param sheetName: the Excel worksheet name that stores the data
	 * @param keyColumns: the number of columns (stored to the left of the worksheet) that represent keys. This will equal the number of keys of the MultiKeyCoefficientMap that is returned
	 * @param valueColumns: the number of columns (stored to the right of the keys in the worksheet) that represents values, not keys.  This will equal the size of the values[] array for each MultiKey in the MultiKeyCoefficientMap
	 * @return
	 */
	public static MultiKeyCoefficientMap loadCoefficientMap(String excelFileName, String sheetName, int keyColumns, int valueColumns) {
		return loadCoefficientMap(excelFileName, sheetName, keyColumns, valueColumns, 0);		//Default start line at 0
	}

	/**
	 * Load MultiKeyCoefficientMap from Excel spreadsheet data, choosing which line to start reading from via the startLine parameter
	 * @param excelFileName: the Excel workbook (.xls) that stores the data
	 * @param sheetName: the Excel worksheet name that stores the data
	 * @param keyColumns: the number of columns (stored to the left of the worksheet) that represent keys. This will equal the number of keys of the MultiKeyCoefficientMap that is returned
	 * @param valueColumns: the number of columns (stored to the right of the keys in the worksheet) that represents values, not keys.  This will equal the size of the values[] array for each MultiKey in the MultiKeyCoefficientMap
	 * @param startLine: Parameter specifying the line number of which to start reading (0 is the first line)
	 * @return
	 */
	public static MultiKeyCoefficientMap loadCoefficientMap(String excelFileName, String sheetName, int keyColumns, int valueColumns, int startLine) {
		
		MultiKeyCoefficientMap map = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(excelFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet(sheetName);
						
			HSSFRow headerRow = worksheet.getRow(startLine);
			String[] keyVector = new String[keyColumns];
			for (int j = 0; j < keyColumns; j++) {
				HSSFCell cell = headerRow.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
				keyVector[j] = getStringCellValue(cell);
			}	
			String[] valueVector = new String[valueColumns];
			for (int j = keyColumns; j < valueColumns + keyColumns; j++) {
				HSSFCell cell = headerRow.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
				valueVector[j - keyColumns] = getStringCellValue(cell);
			}
			
			map = new MultiKeyCoefficientMap(keyVector, valueVector);
			
			for (int i = startLine + 1; i <= worksheet.getLastRowNum(); i++) {
				HSSFRow row = worksheet.getRow(i);
				Object[] keyValueVector = null;
				if (valueColumns == 1) {					
					keyValueVector = new Object[keyColumns + valueColumns];
					for (int j = 0; j < keyColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						keyValueVector[j] = getCellValue(cell);
					}				
										
					for (int j = keyColumns; j < keyColumns + valueColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						keyValueVector[j] = getCellValue(cell);						
					}	
				} else {
					keyValueVector = new Object[keyColumns + 1];
					for (int j = 0; j < keyColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						keyValueVector[j] = getCellValue(cell);
					}				
					
					Object[] values = new Object[valueColumns];
					for (int j = 0; j < valueColumns; j++) {
						HSSFCell cell = row.getCell((short) (j + keyColumns), Row.RETURN_BLANK_AS_NULL);
						values[j] = getCellValue(cell);
					}	
					keyValueVector[keyValueVector.length - 1] = values;
				}
				map.putValue(keyValueVector);
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}

}
