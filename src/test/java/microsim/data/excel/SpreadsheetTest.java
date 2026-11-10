package microsim.data.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SpreadsheetTest {
    @Test
    void readSpreadsheet() throws IOException {
        var ss_path = Path.of("src", "test", "resources", "spreadsheet.xlsx");
        var columns = Spreadsheet.readColumns(ss_path, "regression");

        var expColNames = Set.of("variable", "coefficient");
        assertEquals(expColNames, columns.keySet());

        var expVars = List.of("age", "wealth", "height", "income");
        assertEquals(expVars, columns.get("variable"));

        var expCoeff = List.of(0.5, 18.0, -0.35, 9.5);
        var coeffs = columns.get("coefficient").stream().map(Double::valueOf).toList();
        assertEquals(expCoeff, coeffs);
    }
}
