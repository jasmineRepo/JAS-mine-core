package microsim.data.excel;

import microsim.data.MultiKeyCoefficientMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelAssistant {

	private static Object getCellValue(Cell cell) {
		return switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> cell.getNumericCellValue() - ((Double) cell.getNumericCellValue()).intValue() == 0.0 ?
					cell.getNumericCellValue() : ((Double) cell.getNumericCellValue()).intValue();
			case BOOLEAN -> cell.getBooleanCellValue();
			default -> null;
		};
	}

	private static String getStringCellValue(Cell cell) {
		return MultiKeyCoefficientMap.toStringKey(switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> getCellValue(cell) + "";
			case BOOLEAN -> cell.getBooleanCellValue() + "";
			default -> null;
		});
	}

	/**
	 * Load MultiKeyCoefficientMap from Excel spreadsheet data, reading from the first line of the spreadsheet, and automatically finds the last line of the spreadsheet
	 * (blank lines within the data are not allowed and will result in a NullPointerException).
	 * @param excelFileName: the Excel workbook (.xls or .xlsx) that stores the data
	 * @param sheetName: the Excel worksheet name that stores the data
	 * @param keyColumns: the number of columns (stored to the left of the worksheet) that represent keys. This will equal the number of keys of the MultiKeyCoefficientMap that is returned
	 * @param valueColumns: the number of columns (stored to the right of the keys in the worksheet) that represents values, not keys.  This will equal the size of the values[] array for each MultiKey in the MultiKeyCoefficientMap
	 * @return
	 */
	public static MultiKeyCoefficientMap loadCoefficientMap(String excelFileName, String sheetName, int keyColumns,
															int valueColumns) {
		return loadCoefficientMap(excelFileName, sheetName, keyColumns, valueColumns, 1, Integer.MAX_VALUE);
		//Default startLine is first line of Excel spreadsheet.  endLine will be found automatically
	}

	/**
	 * Load MultiKeyCoefficientMap from Excel spreadsheet data, choosing which line to start reading from via the startLine parameter
	 * @param excelFileName: the Excel workbook (.xls or .xlsx) that stores the data
	 * @param sheetName: the Excel worksheet name that stores the data
	 * @param keyColumns: the number of columns (stored to the left of the worksheet) that represent keys. This will equal the number of keys of the MultiKeyCoefficientMap that is returned
	 * @param valueColumns: the number of columns (stored to the right of the keys in the worksheet) that represents values, not keys.  This will equal the size of the values[] array for each MultiKey in the MultiKeyCoefficientMap
	 * @param startLine: Parameter specifying the (physical, not logical) Excel row number at which to start reading (1 is the first line)
	 * @param endLine: Parameter specifying the (physical, not logical) Excel row number at which to finish reading (1 is the first line)
	 * @return
	 */
	public static MultiKeyCoefficientMap loadCoefficientMap(String excelFileName, String sheetName, int keyColumns,
															int valueColumns, int startLine, int endLine) {

		MultiKeyCoefficientMap map = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(excelFileName);
			Workbook workbook = WorkbookFactory.create(fileInputStream);
			Sheet worksheet = workbook.getSheet(sheetName);

			Row headerRow = worksheet.getRow(startLine - 1);		//startLine and endLine are physical (not logical) rows, therefore need to decrement by 1.
			String[] keyVector = new String[keyColumns];
			for (int j = 0; j < keyColumns; j++) {
				Cell cell = headerRow.getCell((short) j, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				keyVector[j] = getStringCellValue(cell);
			}
			String[] valueVector = new String[valueColumns];
			for (int j = keyColumns; j < valueColumns + keyColumns; j++) {
				Cell cell = headerRow.getCell((short) j, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				valueVector[j - keyColumns] = getStringCellValue(cell);
			}

			map = new MultiKeyCoefficientMap(keyVector, valueVector);

			//startLine and endLine are physical (not logical) rows, therefore need to decrement by 1 in loop index bounds.
			for (int i = startLine; i <= Math.min(worksheet.getLastRowNum(), endLine - 1); i++) {
				Row row = worksheet.getRow(i);
				if (row==null) continue;
				Object[] keyValueVector;
				if (valueColumns == 1) {
					keyValueVector = new Object[keyColumns + valueColumns];
					for (int j = 0; j < keyColumns; j++) {
						Cell cell = row.getCell((short) j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
						keyValueVector[j] = getCellValue(cell);
					}
					for (int j = keyColumns; j < keyColumns + valueColumns; j++) {
						Cell cell = row.getCell((short) j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
						keyValueVector[j] = getCellValue(cell);
					}
				} else {
					keyValueVector = new Object[keyColumns + 1];
					for (int j = 0; j < keyColumns; j++) {
						Cell cell = row.getCell((short) j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
						keyValueVector[j] = getCellValue(cell);
					}
					Object[] values = new Object[valueColumns];
					for (int j = 0; j < valueColumns; j++) {
						Cell cell = row.getCell((short) (j + keyColumns), MissingCellPolicy.CREATE_NULL_AS_BLANK);
						values[j] = getCellValue(cell);
					}
					keyValueVector[keyValueVector.length - 1] = values;
				}
				map.putValue(keyValueVector);
			}
		} catch (IOException | EncryptedDocumentException e) {
			e.printStackTrace();
		}

		return map;
	}

}
