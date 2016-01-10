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
	
	public static MultiKeyCoefficientMap loadCoefficientMap(String excelFileName, String sheetName, int keyColumns, int valueColumns) {
		
		MultiKeyCoefficientMap map = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(excelFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet worksheet = workbook.getSheet(sheetName);
						
			HSSFRow headerRow = worksheet.getRow(0);
			String[] keyVector = new String[keyColumns];
			for (int j = 0; j < keyColumns; j++) {
				HSSFCell cell = headerRow.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
				String val = getStringCellValue(cell);
				keyVector[j] = val;
			}	
			String[] valueVector = new String[valueColumns];
			for (int j = keyColumns; j < valueColumns + keyColumns; j++) {
				HSSFCell cell = headerRow.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
				String val = getStringCellValue(cell);
				valueVector[j - keyColumns] = val;
			}
			
			map = new MultiKeyCoefficientMap(keyVector, valueVector);
			
			for (int i = 1; i <= worksheet.getLastRowNum(); i++) {
				HSSFRow row = worksheet.getRow(i);
				Object[] keyValueVector = null;
				if (valueColumns == 1) {					
					keyValueVector = new Object[keyColumns + valueColumns];
					for (int j = 0; j < keyColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						Object val = getCellValue(cell);
						keyValueVector[j] = val;
					}				
										
					for (int j = keyColumns; j < keyColumns + valueColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						Object val = getCellValue(cell);
						
						keyValueVector[j] = val;
					}	
				} else {
					keyValueVector = new Object[keyColumns + 1];
					for (int j = 0; j < keyColumns; j++) {
						HSSFCell cell = row.getCell((short) j, Row.RETURN_BLANK_AS_NULL);
						Object val = getCellValue(cell);
						keyValueVector[j] = val;
					}				
					
					Object[] values = new Object[valueColumns];
					for (int j = 0; j < valueColumns; j++) {
						HSSFCell cell = row.getCell((short) (j + keyColumns), Row.RETURN_BLANK_AS_NULL);
						Object val = getCellValue(cell);
						
						values[j] = val;
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
