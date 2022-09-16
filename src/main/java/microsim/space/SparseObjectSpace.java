package microsim.space;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A sparse bi-dimensional grid containing one object per cell.
 */
public class SparseObjectSpace extends AbstractSpace<Object> implements ObjectSpace {
    protected HashMap<HashKey, Object> m;

    /**
     * Create a copy of the given grid.
     *
     * @param grid The source grid.
     */
    public SparseObjectSpace(final @NonNull AbstractSpace<Object> grid) {
        super(grid.getXSize(), grid.getYSize());
        m = new HashMap<>();
        for (int i = 0; i < grid.getXSize(); i++) {
            for (int j = 0; j < grid.getYSize(); j++) {
                Object o = grid.get(i, j);
                this.set(i, j, o);
            }
        }
    }

    /**
     * Create a grid with given size.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the  grid.
     */
    public SparseObjectSpace(final int xSize, final int ySize) {
        super(xSize, ySize);
        m = new HashMap<>();
    }

    /**
     * Return the object stored at the given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The requested object.
     */
    public @Nullable Object get(final int x, final int y) {
        return m.get(getKey(x, y));
    }

    /**
     * Put the given object at the given position.
     *
     * @param x   The {@code x} coordinate.
     * @param y   The {@code y} coordinate.
     * @param obj The object to be stored at the {@code (x, y)} cell.
     */
    public void set(final int x, final int y, final @Nullable Object obj) {
        m.put(getKey(x, y), obj);
    }

    /**
     * Swap the content of the {@code (x1, y1)} and {@code (x2, y2)} cells of the grid.
     *
     * @param x1 The {@code x} coordinate for the first cell.
     * @param y1 The {@code y} coordinate for the first cell.
     * @param x2 The {@code x} coordinate for the second cell.
     * @param y2 The {@code y} coordinate for the second cell.
     */
    public void swapPositions(final int x1, final int y1, final int x2, final int y2) {
        val k1 = getKey(x1, y1);
        val k2 = getKey(x2, y2);

        val o1 = m.remove(k1);
        val o2 = m.remove(k2);

        m.put(k1, o2);
        m.put(k2, o1);
    }

    /**
     * Return the number of objects currently on the given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return the number of object in the cell {@code (x,y)}. Can be only 0 or 1.
     */
    public int countObjectsAt(final int x, final int y) {
        return get(x, y) == null ? 0 : 1;
    }

    /**
     * Move a {@link SpacePosition} object from its current position to the specified destination, only if the
     * destination cell is empty.
     *
     * @param destinationX The {@code x} destination coordinate.
     * @param destinationY The {@code y} destination coordinate.
     * @param position     An object implementing {@link SpacePosition} interface.
     * @return true if object has been moved, false otherwise.
     */
    public boolean moveGridPosition(final @Nullable SpacePosition position, final int destinationX,
                                    final int destinationY) {
        if (position == null) return false;

        val x = position.getX();
        val y = position.getY();
        m.remove(getKey(x, y));

        m.put(getKey(destinationX, destinationY), position);

        modCount++;

        return true;
    }

    @Contract("_, _ -> new")
    private @NonNull HashKey getKey(final int x, final int y) {
        return new HashKey(x, y);
    }

    /**
     * Return a {@link ObjectSpaceIterator} storing the position of the read value.
     *
     * @return An iterator that scrolls grid from the top-left corner to the bottom-right, reading each line from left
     * to right. It remembers the position of the last read value.
     */
    public @NonNull ObjectSpaceIterator<Object> gridIterator() {
        return new Itr();
    }

    /**
     * Return the number of objects stored into the grid.
     *
     * @return The sum of cells containing an object.
     */
    public int size() {
        return m.size();
    }

    /**
     * Test if given object is contained into the grid.
     *
     * @param o The object to be tested.
     * @return True if object is present into the grid.
     */
    public boolean contains(final @NonNull Object o) {
        return m.containsValue(o);
    }

    /**
     * @return An iterator that scrolls grid from the top-left corner to the bottom-right, reading each line from left
     * to right.
     */
    public @NonNull Iterator<Object> iterator() {
        return m.values().iterator();
    }

    /**
     * Return an array of objects stored into the grid.
     *
     * @return A vector containing only the objects stored into the grid. The empty cells are ignored.
     */
    public @NonNull Object[] toArray() {
        return m.values().toArray();
    }

    /**
     * Add an object implementing {@link SpacePosition} interface to the grid. If object implements
     * {@link SpacePosition} it stored in the right position of the grid.
     *
     * @param o The {@link SpacePosition} object to be added.
     * @return True if object was added. If o does not implement {@link SpacePosition} interface it will not be added
     * and method will return false.
     */
    public boolean add(final @NonNull Object o) {
        if (!(o instanceof SpacePosition p)) return false;
        m.put(getKey(p.getX(), p.getY()), o);
        return true;
    }

    /**
     * Remove the given object from the grid.
     *
     * @param o The object to be removed.
     * @return True if object was found and removed, false otherwise.
     * @throws ClassCastException If o does not implement {@link SpacePosition} interface.
     */
    public boolean remove(final @NonNull Object o) {
        if (!(o instanceof SpacePosition p))
            throw new ClassCastException("Method remove() accepts only GridPositionInterface objects.");
        m.remove(getKey(p.getX(), p.getY()));
        return true;
    }

    /**
     * Set all cells to the null value.
     */
    public void clear() {
        m.clear();
    }

    /**
     * Test if o is the same of this. There is no content comparing.
     *
     * @param o The object to be compared.
     * @return True o is <b>this</b> object.
     */
    public boolean equals(final @NonNull Object o) {
        return (this.getClass() == o.getClass());
    }

    /**
     * Add an object implementing {@link SpacePosition} interface to the grid. If the destination cell is already
     * occupied the method returns false and the object is not added.
     *
     * @param object The {@link SpacePosition} object to be added.
     * @return True if object has been added. False if destination cell is already occupied or if argument object is
     * null.
     */
    public boolean addGridPosition(final @Nullable SpacePosition object) {
        if (object == null) return false;

        val x = object.getX();
        val y = object.getY();

        if (get(x, y) != null) return false;

        set(x, y, object);

        return true;
    }

    /**
     * Remove the {@link SpacePosition} object from the grid.
     *
     * @param object The {@link SpacePosition} object to be removed.
     * @return true if object has been removed. False if object is null or is not present on the grid.
     */
    public boolean removeGridPosition(final @Nullable SpacePosition object) {
        if (object == null) return false;

        val x = object.getX();
        val y = object.getY();

        if (get(x, y) != null) return false;

        m.remove(getKey(x, y));
        return true;
    }

    private class HashKey {
        public int x, y;

        public HashKey(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int hashCode() {
            return (y * xSize + x);
        }

        public boolean equals(final @NonNull Object o) {
            if (this.getClass() == o.getClass()) {
                HashKey k = (HashKey) o;
                return (x == k.x && y == k.y);
            } else return false;
        }
    }

    private class Itr implements ObjectSpaceIterator<Object> {
        Iterator<Map.Entry<SparseObjectSpace.HashKey, Object>> it = m.entrySet().iterator();
        HashKey currentKey = new HashKey(0, 0);

        @Contract(value = " -> new", pure = true)
        public @NonNull SpacePosition getGridPosition() {
            return new SpacePosition(currentKey.x, currentKey.y);
        }

        public int currentX() {
            return currentKey.x;
        }

        public int currentY() {
            return currentKey.y;
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public @NonNull Object next() {
            val entry = it.next();
            currentKey = entry.getKey();
            return entry.getValue();
        }

        public void remove() {
            m.remove(currentKey);
        }

        public @NonNull SpacePosition nextGridPosition() {
            next();
            return getGridPosition();
        }

    }
}
