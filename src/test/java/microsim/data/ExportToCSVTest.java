package microsim.data;

import lombok.Getter;
import lombok.val;
import microsim.data.db.PanelEntityKey;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExportToCSVTest {
    @Test
    @DisplayName("Incorrect input type")
    void testConstructor() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals("Target", (new ExportToCSV("Target")).targetObject);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The object of type class java.lang.String" +
                " does not have a field of type PanelEntityKey.class, no data is written.");
        }
    }

    @Test
    @DisplayName("Empty ArrayList input")
    void testConstructor2() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertTrue((new ExportToCSV(new ArrayList<>())).targetCollection.isEmpty());
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The collection size is 0, no data is written.");
        }
    }

    @Test
    @DisplayName("ArrayList of blank Objects as input")
    void testConstructor3() {
        val stub = new ArrayList<>();
        stub.add(new Object());
        stub.add(new Object());

        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(stub)).targetCollection.size(), 2);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The collection has no usable fields" +
                ", no data is written.");
        }
    }

    @Test
    @DisplayName("Immutable empty list")
    void testConstructor4() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(List.of())).targetCollection.size(), 0);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The collection size is 0, no data is written.");
        }
    }

    @Test
    @DisplayName("Immutable list with Objects")
    void testConstructor5() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(List.of(new Object(), new Object()))).targetCollection.size(), 2);
            assertThat(logCaptor.getLogs()).hasSize(2).contains("java.lang.UnsupportedOperationException:" +
                    " Passed collection is immutable/fixed size, switching to the slow implementation.",
                "The collection has no usable fields, no data is written.");
        }
    }

    @Test
    @DisplayName("Immutable list with nulls")
    void testConstructor6() {
        val stub = Arrays.asList(null, null, null);
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(stub)).targetCollection.size(), 3);
            assertThat(logCaptor.getLogs()).hasSize(2).contains("java.lang.UnsupportedOperationException: " +
                    "remove: Passed collection is immutable/fixed size, switching to the slow implementation.",
                "All objects in the collection are null, no data is written.");
        }
    }

    @Test
    @DisplayName("Immutable list with objects of different type")
    void testConstructor7() {
        val stub = Arrays.asList(Boolean.TRUE, 1);
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(stub)).targetCollection.size(), 2);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("Objects in the collection are of different type, no data is written.");
        }
    }

    @Test
    @DisplayName("Immutable list with empty classes")
    void testConstructor8() {
        val stub = Arrays.asList(new C(), new C());
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertEquals((new ExportToCSV(stub)).targetCollection.size(), 2);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The collection has no usable fields, no data is written.");
        }
    }

    @Test
    @DisplayName("Null input")
    void testConstructor9() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            ExportToCSV actualExportToCSV = new ExportToCSV(null);
            assertNull(actualExportToCSV.targetCollection);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The object to save is null, no data is written.");

            assertNull(actualExportToCSV.bufferWriter);
            assertNull(actualExportToCSV.targetObjectIdField);
            assertNull(actualExportToCSV.targetObject);
            assertNull(actualExportToCSV.targetCollection);
            assertNull(actualExportToCSV.idFieldName);
            assertNull(actualExportToCSV.fieldsForExport);
        }
    }

    @Test
    @DisplayName("Blank class input")
    void testConstructor10() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertNull((new ExportToCSV(new C())).targetCollection);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The object has no fields, no data is written.");
        }
    }

    @Test
    @DisplayName("Blank input with fields")
    void testConstructor11() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertNull(new ExportToCSV(new B()).targetCollection);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The object of type " +
                "class microsim.data.ExportToCSVTest$B does not have a field of type PanelEntityKey.class, " +
                "no data is written.");
        }
    }

    @Test
    @DisplayName("Proper input")
    void testConstructor12() {
        assertNull(new ExportToCSV(new F()).targetCollection);
    }

    @Test
    @DisplayName("Empty list of field names as input")
    void testExtractFieldNames() {
        ExportToCSV exportToCSV = new ExportToCSV("Target");
        assertTrue(exportToCSV.extractFieldNames(true, new TreeSet<>()).isEmpty());
    }

    @Test
    @DisplayName("Empty list of field names as input, no try")
    void testExtractFieldNames2() {
        ExportToCSV exportToCSV = new ExportToCSV("Target");
        assertTrue(exportToCSV.extractFieldNames(false, new TreeSet<>()).isEmpty());
    }

    @Test
    @DisplayName("Non-empty list of field names as input, no try")
    void testExtractFieldNames3() {
        ExportToCSV exportToCSV = new ExportToCSV("Target");
        val file = new File("scratch_path");

        try {
            exportToCSV.bufferWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TreeSet<String> stringSet = new TreeSet<>();
        stringSet.add("foo");
        exportToCSV.extractFieldNames(true, stringSet);
    }

    @Test
    @DisplayName("Null Field check")
    void testGenerateFilename() {
        assertThrows(NullPointerException.class, () -> (new ExportToCSV("Target")).generateFilename("Parsed Target Object", true, null));
    }

    @Test
    @DisplayName("Null object check, pt. 2")
    void testGenerateFilename2() {
        assertThrows(NullPointerException.class, () -> (new ExportToCSV("Target")).generateFilename(null, true, null));
    }

    @Test
    @DisplayName("File name generator")
    void testGenerateFilename3() {
        assertEquals("Object", (new ExportToCSV("Target")).generateFilename(new Object(),
            true, ExportToCSVTest.B.class.getDeclaredFields()[0]));
    }

    @Test
    @DisplayName("Mock object with no required fields")
    void testGenerateFilename4() {
        val scratch = new ExportToCSV("Target");
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertNull(scratch.generateFilename(new Object(),
                false, ExportToCSVTest.B.class.getDeclaredFields()[0]));
            assertThat(logCaptor.getLogs()).hasSize(1).contains("Target object doesn't have fields of the " +
                "PanelEntityKey type, no data is written.");
        }
    }

    @Test
    @DisplayName("Private field check")
    void testGenerateFilename5() {
        val scratch = new ExportToCSV("Target");
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertNull(scratch.generateFilename(new Object(),
                false, D.class.getDeclaredFields()[0]));
            assertThat(logCaptor.getLogs()).hasSize(1).contains("Failed to append to the filename due to " +
                "no access, no data is written.");
        }
    }

    @Test
    @DisplayName("CSV dump, string test")
    void testDumpToCSV() {
        val scratch = new ExportToCSV("Target");
        val file = new File("scratch_path");
        try {
            scratch.bufferWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            scratch.dumpToCSV();
            assertThat(logCaptor.getLogs()).hasSize(1).contains("Failed to append run id/time/panel data to the buffer writer, no data is written.");
        }
    }

    @Test
    @DisplayName("Dump null to CSV")
    void testDumpToCSV2() {
        val scratch = new ExportToCSV(null);
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            scratch.dumpToCSV();
            assertThat(logCaptor.getLogs()).hasSize(1).contains("ExportToCSV's targetCollection and " +
                "targetObject fields are both null! Cannot export to CSV.");
        }
    }

    @Test
    @DisplayName("Dump actual list of data to CSV")
    void testDumpToCSV3() {
        val list = new ArrayList<>();
        list.add(new F());
        list.add(new F());
        list.add(new F());
        val scratch = new ExportToCSV(list);
        scratch.dumpToCSV();
    }

    @Test
    @DisplayName("Dump actual data object to CSV")
    void testDumpToCSV4() {
        val scratch = new ExportToCSV(new F());
        scratch.dumpToCSV();
    }

    @Test
    @DisplayName("Add strings to buffer")
    void testAddSimParametersToBuffer2() {
        val scratch = new ExportToCSV("Target");
        val file = new File("scratch_path");
        try {
            scratch.bufferWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scratch.addSimParametersToBuffer("42", "42");
    }

    @Test
    @DisplayName("PanelEntityKey is null")
    void testGenerateFilename6() {
        try (LogCaptor logCaptor = LogCaptor.forClass(ExportToCSV.class)) {
            assertNull(new ExportToCSV(new E()).targetCollection);
            assertThat(logCaptor.getLogs()).hasSize(1).contains("The field of the PanelEntityKey type is not " +
                "initialized, i.e., null, no data is written.");
        }
    }

    @Test
    @DisplayName("Match class field names with incorrect input")
    void testFindUnderlyingField() {
        assertNull(ExportToCSV.findUnderlyingField(Object.class, "Field Name"));
        assertThrows(NullPointerException.class, () -> ExportToCSV.findUnderlyingField(null, null));
        assertNull(ExportToCSV.findUnderlyingField(Boolean.class, "Field Name"));
        assertNull(ExportToCSV.findUnderlyingField(Object.class, ""));
        assertNull(ExportToCSV.findUnderlyingField(Object.class, "   "));
        assertNull(ExportToCSV.findUnderlyingField(Object.class, "Field Name"));
        assertNull(ExportToCSV.findUnderlyingField(Boolean.class, "Field Name"));
        assertNull(ExportToCSV.findUnderlyingField(Object.class, ""));
    }

    @Test
    @DisplayName("Get fields of Boolean")
    void testFindUnderlyingField3() {
        ExportToCSV.findUnderlyingField(Boolean.class, "TRUE");
    }

    @Test
    @DisplayName("Get Object fields")
    void testGetAllFields() {
        assertTrue(ExportToCSV.getAllFields(Object.class).isEmpty());
        assertTrue(ExportToCSV.getAllFields(Object.class).isEmpty());
    }

    @Test
    @DisplayName("Get fields of null")
    void testGetAllFields2() {
        assertTrue(ExportToCSV.getAllFields(null).isEmpty());
    }

    @Test
    @DisplayName("Get fields with inheritance")
    void testGetAllFields3() {
        val testValues = new ArrayList<String>();
        for (var q : ExportToCSV.getAllFields(ExportToCSVTest.B.class))
            testValues.add(q.getName());

        val referenceValues = new ArrayList<String>();
        referenceValues.add("fieldA");
        referenceValues.add("fieldB");

        assertEquals(testValues, referenceValues);
    }

    @Test
    @DisplayName("Check name sorting")
    void testGetAllFields5() {
        val scratch = ExportToCSV.getAllFields(ExportToCSVTest.G.class);
        val referenceList = new ArrayList<>();

        val actualList = new ArrayList<>();
        referenceList.add("fieldG");
        referenceList.add("stringA");
        referenceList.add("stringB");
        referenceList.add("stringC");
        referenceList.add("stringD");

        for (var value : scratch)
            actualList.add(value.getName());

        assertEquals(actualList, referenceList);
    }

    static class A {
        @Getter
        final static String fieldA = "fieldA";
    }

    static class B extends A {
        @Getter
        final static String fieldB = "fieldB";
    }

    static class C {
    }

    static class D {
        final private String fieldD = "fieldD";
    }

    static class E {
        PanelEntityKey fieldE;
    }

    static class F {
        PanelEntityKey fieldF = new PanelEntityKey();
        String string = "scratch";
    }

    static class G {
        PanelEntityKey fieldG = new PanelEntityKey();

        String stringD = "scratchD";
        String stringC = "scratchC";
        String stringB = "scratchB";
        String stringA = "scratchA";
    }
}
