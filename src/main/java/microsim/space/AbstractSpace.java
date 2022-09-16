package microsim.space;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

/**
 * An abstract class representing bi-dimensional grid container. It extends the standard JDK
 * {@link java.util.AbstractCollection} to let every implementation behave like a collection.
 */
public abstract class AbstractSpace<E> {

    @Getter
    protected int xSize;

    @Getter
    protected int ySize;
    protected transient int modCount = 0;

    /**
     * Creates a grid of given size.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the grid.
     */
    public AbstractSpace(final int xSize, final int ySize) {
        if (xSize <= 0 || ySize <= 0)
            throw new IllegalArgumentException("Grid must have positive dimensions");

        this.xSize = xSize;
        this.ySize = ySize;
    }

    /**
     * Return the object contained at a given position. The type depends on the specific implementation of the Grid.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return a generic object.
     */
    public abstract E get(final int x, final int y);

    /**
     * Store an object at a given position.
     *
     * @param x   The x coordinate.
     * @param y   The y coordinate.
     * @param obj The object to be stored.
     */
    public abstract void set(final int x, final int y, final @NonNull Object obj);

    /**
     * Swap the content of the {@code (x1, y1)} and {@code (x2, y2)} cells of the grid.
     *
     * @param x1 The x coordinate for the first cell.
     * @param y1 The y coordinate for the first cell.
     * @param x2 The x coordinate for the second cell.
     * @param y2 The y coordinate for the second cell.
     */
    public abstract void swapPositions(final int x1, final int y1, final int x2, final int y2);

    /**
     * Check the given {@code x} coordinate. If it is out of grid bounds the returning value is truncated to stay within
     * the borders. For instance, if the grid is {@code (100, 100)} the instruction <i>boundX(130)</i> returns 99.
     *
     * @param x The {@code x} coordinate to be tested.
     * @return A safe value representing the {@code x} value if inbound, the bound otherwise.
     */
    public int boundX(final int x) {
        return x < 0 ? 0 : x >= xSize ? xSize - 1 : x;
    }

    /**
     * Check the given {@code y} coordinate. If it is out of grid bounds the returning value is truncated to stay within
     * the borders. For instance, if the grid is {@code (100, 100)} the instruction <i>boundY(130)</i> returns 99.
     *
     * @param y The {@code y} coordinate to be tested.
     * @return A safe value representing the {@code y} value if inbound, the bound otherwise.
     */
    public int boundY(final int y) {
        return y < 0 ? 0 : y >= ySize ? ySize - 1 : y;
    }

    /**
     * Gets the extended Moore neighbors of the specified coordinate. Points are returned by row starting with the
     * "NW corner" and ending with the "SE corner." The point at {@code x,y} is not returned.
     *
     * @param x The x coordinate of the object.
     * @param y The y coordinate of the object.
     * @return an array of points ordered by row starting with the "NW corner" and ending with the "SE corner."
     */
    public @NonNull SpacePosition[] getMooreNeighborsPositions(final int x, final int y) {
        val array = new SpacePosition[8];
        int index = 0;

        for (int j = y - 1; j <= y + 1; j++)
            for (int i = x - 1; i <= x + 1; i++)
                if (!(j == y && i == x))
                    array[index++] = new SpacePosition(torusX(i), torusY(j));

        return array;
    }

    /**
     * Gets the extended von Neumann neighbors of the specified coordinate. Points are returned in west, east, north,
     * south order with the most distant object first. The point at {@code x,y} is not returned.
     *
     * @param x The x coordinate of the object
     * @param y The y coordinate of the object
     * @return an array of points in west, east, north, south order with the most distant object first.
     */
    public @NonNull SpacePosition[] getVonNeumannNeighborsPositions(final int x, final int y) {
        val array = new SpacePosition[4];
        int index = 0;

        val normX = torusX(x);
        val normY = torusY(y);

        for (int i = x - 1; i < x; i++) array[index++] = new SpacePosition(torusX(i), normY);
        for (int i = x + 1; i > x; i--) array[index++] = new SpacePosition(torusX(i), normY);
        for (int i = y - 1; i < y; i++) array[index++] = new SpacePosition(normX, torusY(i));
        for (int i = y + 1; i > y; i--) array[index++] = new SpacePosition(normX, torusY(i));

        return array;
    }

    /**
     * Returns the number of objects allocated in cell {@code (x,y)}.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return the number of "entities" contained in the specified cell.
     */
    public abstract int countObjectsAt(final int x, final int y);

    /**
     * Check the given {@code x} coordinate considering the grid as a toroid. If {@code x} is out of grid bounds the
     * returning coordinate is computed starting from the opposite bound. For instance, if the grid is
     * {@code (100, 100)} the instruction {@code torusX(130)} returns 30, because over 100 the counter starts again from
     * 0.
     *
     * @param x The {@code x} coordinate to be tested.
     * @return A safe value representing the {@code x} value on the toroid.
     */
    public int torusX(final int x) {
        return x >= 0 ? x % xSize : xSize + (x % xSize);
    }

    /**
     * Check the given {@code y} coordinate considering the grid as a toroid. If {@code y} is out of grid bounds the
     * returning coordinate is computed starting from the opposite bound. For instance, if the grid is
     * {@code (100, 100)} the instruction {@code torusY(150)} returns 50, because over 100 the counter starts again from
     * 0.
     *
     * @param y The {@code y} coordinate to be tested.
     * @return A safe value representing the {@code y} value on the toroid.
     */
    public int torusY(final int y) {
        return y >= 0 ? y % ySize : ySize + (y % ySize);
    }

    /**
     * Check the given {@code x} coordinate considering the grid as a walled space. If {@code x} goes out of grid bounds
     * the returning coordinate is computed mirroring the path in front of the bound. For instance, if the grid is
     * {@code (100, 100)} the instruction {@code reflectX(130)} returns 70, because over 100 the path bounce on the wall
     * and comes back.
     *
     * @param x The {@code x} coordinate to be tested.
     * @return A safe value representing the {@code x} value on the walled grid.
     */
    public int reflectX(final int x) {
        return x < 0 ? -x % xSize : x % xSize;
    }

    /**
     * Check the given {@code y} coordinate considering the grid as a walled space. If {@code y} goes out of grid bounds
     * the returning coordinate is computed mirroring the path in front of the bound. For instance, if the grid is
     * {@code (100, 100)} the instruction {@code reflectY(140)} returns 60, because over 100 the path bounce on the
     * wall and comes back.
     *
     * @param y The {@code y} coordinate to be tested.
     * @return A safe value representing the {@code y} value on the walled grid.
     */
    public int reflectY(final int y) { // fixme docs describe a completely different function.
        return y < 0 ? -y % ySize : y % ySize;
    }

    /**
     * Return the total size of the grid, i.e., {@code width * height}.
     *
     * @return The number of cells in the grid.
     */
    public abstract int size();

    /**
     * Empty the content of the grid.
     */
    public abstract void clear();

    /**
     * Test if the passed object is equal. The comparison is made at pointer level.
     *
     * @param o The object to be compared.
     * @return true if objects are the same, false otherwise.
     * @implNote If grids contains exactly the same values they are not equals. Only the same implementation of the grid
     * equals to itself.
     */
    public boolean equals(final @NonNull Object o) {
        return (this.getClass() == o.getClass());
    }

    /**
     * Return a string representing the content of the grid.
     *
     * @return The string representation of the grid like [elem1, elem2, .. ].
     */
    public @NonNull String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");

        E o;
        for (int i = 0; i < getXSize(); i++) {
            for (int j = 0; j < getYSize(); j++) {
                o = get(i, j);
                buf.append(o == this ? "(this Collection)" : String.valueOf(o)); // todo this comparison is extremely confusing
                if (i != getXSize() && j != getYSize()) buf.append(", ");
            }
        }

        buf.append("]");
        return buf.toString();
    }
}
