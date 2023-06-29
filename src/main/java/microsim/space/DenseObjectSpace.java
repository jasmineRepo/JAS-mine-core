package microsim.space;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

/**
 * A bi-dimensional grid containing one object per cell only.
 */
public class DenseObjectSpace extends AbstractSpace<Object> implements ObjectSpace {
    protected Object[][] m;
    protected int objects = 0;

    /**
     * Creates a copy of the given grid.
     *
     * @param grid The source grid.
     */
    public DenseObjectSpace(final @NonNull AbstractSpace<Object> grid) {
        super(grid.getXSize(), grid.getYSize());
        m = new Object[xSize][ySize];
        for (int i = 0; i < grid.getXSize(); i++) {
            for (int j = 0; j < grid.getYSize(); j++) {
                this.set(i, j, grid.get(i, j));
            }
        }
    }

    /**
     * Creates an empty grid with the given size.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the  grid.
     */
    public DenseObjectSpace(final int xSize, final int ySize) {
        super(xSize, ySize);
        m = new Object[xSize][ySize];
    }

    /**
     * Returns the object stored at the given position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The requested object.
     */
    public @Nullable Object get(int x, int y) {
        return m[x][y];
    }

    /**
     * Adds an object implementing {@link SpacePosition} interface to the grid. If object implements
     * {@link SpacePosition} it stored in the right position of the grid.
     *
     * @param o The {@link SpacePosition} object to be added.
     * @return True if object was added. If {@code o} does not implement {@link SpacePosition} interface it will not be
     * added and method will return false.
     */
    public boolean add(final @NonNull Object o) {
        if (o instanceof SpacePosition p) {
            set(p.getX(), p.getY(), o);
            modCount++;
            return true;
        } else return false;
    }

    /**
     * Swap the content of the {@code (x1, y1)} and {@code (x2, y2)} cells of the grid.
     *
     * @param x1 The x coordinate for the first cell.
     * @param y1 The y coordinate for the first cell.
     * @param x2 The x coordinate for the second cell.
     * @param y2 The y coordinate for the second cell.
     */
    public void swapPositions(final int x1, final int y1, final int x2, final int y2) {
        val o1 = get(x1, y1);
        val o2 = get(x2, y2);
        set(x1, y1, o2);
        set(x2, y2, o1);

        modCount++;
    }

    /**
     * Put the given object at the given position.
     *
     * @param x   The {@code x} coordinate.
     * @param y   The {@code y} coordinate.
     * @param obj The object to be stored at the {@code (x, y)} cell.
     */
    public void set(final int x, final int y, final @Nullable Object obj) {
        if (m[x][y] == null && obj != null) objects++;
        m[x][y] = obj;
        modCount++;
    }

    /**
     * Gets the Moore neighbors of the specified coordinate. Objects are returned by row starting with the "NW corner"
     * and ending with the "SE corner." The object at {@code (x,y)} is not returned.
     *
     * @param x The {@code x} coordinate of the object
     * @param y The {@code y} coordinate of the object
     * @return an array of objects ordered by row starting with the "NW corner" and ending with the "SE corner."
     */
    public @NonNull Object[] getMooreNeighbors(final int x, final int y) {
        val array = new Object[8];
        int index = 0;

        for (int j = y - 1; j <= y + 1; j++)
            for (int i = x - 1; i <= x + 1; i++)
                if (!(j == y && i == x))
                    array[index++] = get(torusX(i), torusY(j));

        return array;
    }

    /**
     * Gets the extended Moore neighbors of the specified coordinate. The extensions in the {@code x} and {@code y}
     * directions are specified by {@code xExtent} and {@code yExtent}. Objects are returned by row starting with the
     * "NW corner" and ending with the "SE corner." The object at {@code x,y} is not returned.
     *
     * @param x       The {@code x} coordinate of the object
     * @param y       The {@code y} coordinate of the object
     * @param xExtent The extension of the neighborhood in the {@code x} direction
     * @param yExtent The extension of the neighborhood in the {@code y} direction
     * @return an array of doubles ordered by row starting with the "NW corner" and ending with the "SE corner."
     */
    public @NonNull Object[] getMooreNeighbors(final int x, final int y, final int xExtent, final int yExtent) {
        val array = new Object[xExtent * yExtent * 4 + (xExtent * 2) + (yExtent * 2)];
        int index = 0;

        for (int j = y - yExtent; j <= y + yExtent; j++)
            for (int i = x - xExtent; i <= x + xExtent; i++)
                if (!(j == y && i == x))
                    array[index++] = get(boundX(i), boundY(j));

        return array;
    }

    /**
     * Gets the von Neumann neighbors of the specified coordinate. Objects are returned in west, east, north, south
     * order. The double at {@code x,y} is not returned.
     *
     * @param x The {@code x} coordinate of the object.
     * @param y The {@code y} coordinate of the object.
     * @return an array of doubles in west, east, north, south order.
     */
    public @NonNull Object[] getVonNeumannNeighbors(final int x, final int y) {
        val array = new Object[4];
        int index = 0;

        val normX = torusX(x);
        val normY = torusY(y);

        for (int i = x - 1; i < x; i++) array[index++] = get(torusX(i), normY);
        for (int i = x + 1; i > x; i--) array[index++] = get(torusX(i), normY);
        for (int i = y - 1; i < y; i++) array[index++] = get(normX, torusY(i));
        for (int i = y + 1; i > y; i--) array[index++] = get(normX, torusY(i));

        return array;
    }

    /**
     * Gets the extended von Neumann neighbors of the specified coordinate. The extensions in the {@code x} and
     * {@code y} directions are specified by {@code xExtent} and {@code yExtent}. Objects are returned in west, east,
     * north, south order with the most distant object first. The object at {@code x,y} is not returned.
     *
     * @param x       The x coordinate of the object.
     * @param y       The y coordinate of the object.
     * @param xExtent The extension of the neighborhood in the x direction.
     * @param yExtent The extension of the neighborhood in the y direction.
     * @return an array of doubles in west, east, north, south order with the most distant object first.
     */
    public @NonNull Object[] getVonNeumannNeighbors(final int x, final int y, final int xExtent, final int yExtent) {
        val array = new Object[(xExtent * 2) + (yExtent * 2)];
        int index = 0;

        val normX = boundX(x);
        val normY = boundY(y);

        for (int i = x - xExtent; i < x; i++) array[index++] = get(i, normY);
        for (int i = x + xExtent; i > x; i--) array[index++] = get(i, normY);
        for (int i = y - yExtent; i < y; i++) array[index++] = get(normX, i);
        for (int i = y + yExtent; i > y; i--) array[index++] = get(normX, i);

        return array;
    }

    /**
     * Move a {@link SpacePosition} object from its current position to the specified destination, only if the
     * destination cell is empty.
     *
     * @param destinationX The x destination coordinate.
     * @param destinationY The y destination coordinate.
     * @param position     An object implementing {@link SpacePosition} interface.
     * @return true if object has been moved, false otherwise.
     */
    public boolean moveGridPosition(final @Nullable SpacePosition position, final int destinationX,
                                    final int destinationY) {
        if (m[destinationX][destinationY] != null || position == null) return false;

        val x = position.getX();
        val y = position.getY();
        m[x][y] = null;
        m[destinationX][destinationY] = new SpacePosition(destinationX, destinationY);

        modCount++;

        return true;
    }

    /**
     * Return the matrix of objects representing the grid.
     *
     * @return A matrix of {@code Object} with the same dimensions of the grid.
     */
    public @NonNull Object[][] getMatrix() {
        return m;
    }

    /**
     * Return the number of objects stored into the grid.
     *
     * @return The sum of cells containing an object.
     */
    public int size() {
        return objects;
    }

    /**
     * Set all cells to the null value.
     */
    public void clear() {
        for (int i = 0; i < xSize; i++) for (int j = 0; j < ySize; j++) m[i][j] = null;
        objects = 0;
        modCount++;
    }

    /**
     * Add an object implementing {@link SpacePosition} interface to the grid. If the destination cell is already
     * occupied the method return false and the object is not added.
     *
     * @param position The {@link SpacePosition} object to be added.
     * @return True if object has been added. False if destination cell is already occupied or if argument object is
     * null.
     */
    public boolean addGridPosition(final @Nullable SpacePosition position) {
        if (position == null) return false;

        val x = position.getX();
        val y = position.getY();

        if (m[x][y] != null) return false;

        set(x, y, position);
        modCount++;
        return true;
    }

    /**
     * Remove the {@link SpacePosition} object from the grid.
     *
     * @param position The {@link SpacePosition} object to be removed.
     * @return true if object has been removed. False if object is null or is not present on the grid.
     */
    public boolean removeGridPosition(final @Nullable SpacePosition position) {
        if (position == null) return false;

        val x = position.getX();
        val y = position.getY();

        if (m[x][y] == null) return false;

        if (!m[x][y].equals(position)) return false;

        m[x][y] = null;
        objects--;
        modCount++;
        return true;
    }

    /**
     * Return the number of objects currently on the given position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return the number of object in the cell(x,y). Must be only 0 or 1.
     */
    public int countObjectsAt(final int x, final int y) {
        return get(x, y) == null ? 0 : 1;
    }
}
