package microsim.event;

public enum Order {
    BEFORE_ALL(Integer.MIN_VALUE),
    AFTER_ALL(Integer.MAX_VALUE);

    private final int ordering;

    Order(final int ordering) {
        this.ordering = ordering;
    }

    public int getOrdering() {
        return ordering;
    }
}
