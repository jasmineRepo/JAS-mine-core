package microsim.data.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;

record Column(String name, int position) {
}

public class Spreadsheet {
    private static String cellAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case CellType.STRING -> cell.getStringCellValue();
            case CellType.NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case CellType.BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case CellType.BLANK -> "";
            default -> throw new RuntimeException("unrecognised cell type " + cell.getCellType());
        };
    }

    /// Read a sheet column by column, with column names in the first row.
    public static Map<String, List<String>> readColumns(Path path, String sheet) throws IOException {
        var wb = WorkbookFactory.create(path.toFile());
        var sh = wb.getSheet(sheet);
        var rows = sh.rowIterator();
        var firstRow = rows.next();

        var columns = new ArrayList<Column>();
        firstRow.forEach(row -> {
            var col = new Column(cellAsString(row), row.getColumnIndex());
            columns.add(col);
        });

        var data = new HashMap<String, List<String>>();
        columns.forEach(c -> {
            var old = data.put(c.name(), new ArrayList<>());
            if (Objects.nonNull(old)) {
                throw new RuntimeException("non-unique column name " + c.name());
            }
        });

        while (rows.hasNext()) {
            var row = rows.next();
            for (var col : columns) {
                var cell = row.getCell(col.position());
                data.get(col.name()).add(cellAsString(cell));
            }
        }

        wb.close();
        return data;
    }
}
