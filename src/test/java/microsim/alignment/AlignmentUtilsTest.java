package microsim.alignment;

import lombok.val;
import microsim.alignment.multiple.LogitScalingAlignment;
import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AlignmentUtilsTest {
    private final static LogitScalingAlignment<Object> lsa = new LogitScalingAlignment<>();
    private static final ArrayList<Object> objectList = new ArrayList<>(List.of("42"));

    @Test @DisplayName("Generic predicate") void testExtractAgentList() {
        assertTrue(lsa.extractAgentList(new ArrayList<>(), mock(Predicate.class)).isEmpty());
        assertTrue(lsa.extractAgentList(new ArrayList<>(), null).isEmpty());
    }

    @Test @DisplayName("Filter is null") void testExtractAgentList2() {
        val scratch = lsa.extractAgentList(objectList, null);
        assertEquals(1, scratch.size());
        assertEquals("42", scratch.get(0));
    }

    @Test @DisplayName("Mock filter, always returns true") void testExtractAgentList3() {
        Predicate<Object> predicate = mock(Predicate.class);
        when(predicate.evaluate(any())).thenReturn(true);
        val scratch = lsa.extractAgentList(objectList, predicate);
        assertEquals(1, scratch.size());
        assertEquals("42", scratch.get(0));
        verify(predicate).evaluate(any());
    }

    @Test @DisplayName("Mock filter, always returns false") void testExtractAgentList4() {
        Predicate<Object> predicate = mock(Predicate.class);
        when(predicate.evaluate(any())).thenReturn(false);
        assertTrue(lsa.extractAgentList(objectList, predicate).isEmpty());
        verify(predicate).evaluate(any());
    }

    @Test @Disabled("TODO: Complete this test") void testExtractAgentList5() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalStateException: foo
        //       at org.apache.commons.collections4.CollectionUtils.select(CollectionUtils.java:958)
        //       at microsim.alignment.AlignmentUtils.extractAgentList(AlignmentUtils.java:24)
        //   In order to prevent extractAgentList(Collection, Predicate)
        //   from throwing IllegalStateException, add constructors or factory
        //   methods that make it easier to construct fully initialized objects used in
        //   extractAgentList(Collection, Predicate).
        //   See https://diff.blue/R013 to resolve this issue.

        Predicate<Object> predicate = mock(Predicate.class);
        when(predicate.evaluate(any())).thenThrow(new IllegalStateException("foo"));
        lsa.extractAgentList(objectList, predicate);
    }
}
