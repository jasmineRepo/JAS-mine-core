package microsim.collection;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.FilterListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AggregateTest {

    @Test
    void testApplyToFilter2() {
        Iterable<Object> iterable = (Iterable<Object>) mock(Iterable.class);
        when(iterable.iterator()).thenReturn(
            new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(
                new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(new FilterListIterator<>()))))));
        Aggregate.applyToFilter(iterable, (Predicate<Object>) mock(Predicate.class),
            (Closure<Object>) mock(Closure.class));
        verify(iterable).iterator();
    }

    @Test
    void testApplyToFilter3() {
        Iterable<Object> iterable = (Iterable<Object>) mock(Iterable.class);
        when(iterable.iterator()).thenReturn(new CollatingIterator<>());
        Aggregate.applyToFilter(iterable, (Predicate<Object>) mock(Predicate.class),
            (Closure<Object>) mock(Closure.class));
        verify(iterable).iterator();
    }

    @Test
    void testApplyToFilter4() {
        Iterable<Object> iterable = (Iterable<Object>) mock(Iterable.class);
        when(iterable.iterator()).thenReturn(new AbstractListIteratorDecorator<>(
            new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(
                new AbstractListIteratorDecorator<>(new ObjectArrayListIterator<>("Array")))))));
        Predicate<Object> predicate = (Predicate<Object>) mock(Predicate.class);
        when(predicate.evaluate(any())).thenReturn(true);
        Closure<Object> closure = (Closure<Object>) mock(Closure.class);
        doNothing().when(closure).execute(any());
        Aggregate.applyToFilter(iterable, predicate, closure);
        verify(iterable).iterator();
        verify(predicate).evaluate(any());
        verify(closure).execute(any());
    }

    @Test
    void testApplyToFilter5() {
        Iterable<Object> iterable = (Iterable<Object>) mock(Iterable.class);
        when(iterable.iterator()).thenReturn(new AbstractListIteratorDecorator<>(
            new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(new AbstractListIteratorDecorator<>(
                new AbstractListIteratorDecorator<>(new ObjectArrayListIterator<>("Array")))))));
        Predicate<Object> predicate = (Predicate<Object>) mock(Predicate.class);
        when(predicate.evaluate(any())).thenReturn(false);
        Closure<Object> closure = (Closure<Object>) mock(Closure.class);
        doNothing().when(closure).execute(any());
        Aggregate.applyToFilter(iterable, predicate, closure);
        verify(iterable).iterator();
        verify(predicate).evaluate(any());
    }
}

