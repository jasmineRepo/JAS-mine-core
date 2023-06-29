package microsim.space;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

/**
 * A bi-dimensional grid capable of containing many objects into each cell.
 */
public class MultiObjectSpace extends DenseObjectSpace {
    private static final int INITIAL_CAPACITY = 3;
    private final int initialCapacity;

    /**
     * Create a copy of the given grid with a given initial capacity.
     *
     * @param grid         The source grid.
     * @param cellCapacity The initial vector size allocated for each cell. It grows automatically.
     */
    public MultiObjectSpace(final @NonNull AbstractSpace<Object> grid, final int cellCapacity) {
        super(grid);
        initialCapacity = cellCapacity;
        if (cellCapacity < 1) throw new IllegalArgumentException("Initial cell capacity must be greater than 0");
    }

    /**
     * Create a grid with given size and a given initial capacity.
     *
     * @param xSize        The width of the grid.
     * @param ySize        The height of the grid.
     * @param cellCapacity The initial vector size allocated for each cell. It grows automatically.
     */
    public MultiObjectSpace(final int xSize, final int ySize, final int cellCapacity) {
        super(xSize, ySize);
        initialCapacity = cellCapacity;
        if (cellCapacity < 1) throw new IllegalArgumentException("Initial cell capacity must be greater than 0");
    }

    /**
     * Create a copy of the given grid with a default initial capacity.
     *
     * @param grid The source grid.
     */
    public MultiObjectSpace(final @NonNull AbstractSpace<Object> grid) {
        this(grid, INITIAL_CAPACITY);
    }

    /**
     * Create a grid with given size and a default initial capacity.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the grid.
     */
    public MultiObjectSpace(final int xSize, final int ySize) {
        this(xSize, ySize, INITIAL_CAPACITY);
    }

    /**
     * Return the number of objects stored at the given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The number of objects at {@code (x, y)}.
     */
    public int countObjectsAt(final int x, final int y) {
        if (m[x][y] == null) return 0;

        int k = 0;
        val arr = toArrayCell(m[x][y]);
        for (Object o : arr)
            if (o != null)
                k++;

        return k;
    }

    /**
     * Return the object stored at the given tri-dimensional position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @param z The index of the vector of object stored at the {@code (x, y)} cell.
     * @return The requested object.
     */
    public Object get(final int x, final int y, final int z) {
        return m[x][y] == null ? null : toArrayCell(m[x][y])[z];
    }

    /**
     * Enqueue the given object at the given position.
     *
     * @param x   The {@code x} coordinate.
     * @param y   The {@code y} coordinate.
     * @param obj The object to be stored into the vector at the {@code (x, y)} cell.
     */
    public void set(final int x, final int y, final @Nullable Object obj) {
        Object[] arr;
        if (m[x][y] == null) {
            arr = new Object[initialCapacity];
            m[x][y] = arr;
        } else
            arr = toArrayCell(m[x][y]);

        int i = 0;
        int last = arr.length - 1;
        while (arr[i] != null && i < last)
            i++;

        if (i == last && arr[i] != null) {
            arr = growArray(arr);
            m[x][y] = arr;
            i++;
        }

        objects++;
        arr[i] = obj;
        modCount++;
    }

    /**
     * Move a {@link SpacePosition} object from its current position to the specified destination.
     *
     * @param destinationX The {@code x} destination coordinate.
     * @param destinationY The {@code y} destination coordinate.
     * @param position     An object implementing {@link SpacePosition} interface.
     * @return true. Only if the argument object is null the return value will be false.
     */
    public boolean moveGridPosition(@Nullable SpacePosition position, final int destinationX, final int destinationY) {
        if (position == null) return false;

        val x = position.getX();
        val y = position.getY();

        if (removeAt(x, y, position)) {
            set(destinationX, destinationY, position);
            return true;
        } else return false;

    }

    /**
     * Put the given object at the given tri-dimensional position.
     *
     * @param x   The {@code x} coordinate.
     * @param y   The {@code y} coordinate.
     * @param z   The index of the vector of object stored at the {@code (x, y)} cell. If {@code z} is out of vector
     *            bounds this will be automatically grown.
     * @param obj The object to be stored into the vector at the {@code (x, y)} cell.
     */
    public void set(final int x, final int y, final int z, final @Nullable Object obj) {
        Object[] arr;
        if (m[x][y] == null) {
            arr = new Object[initialCapacity];
            m[x][y] = arr;
        } else
            arr = toArrayCell(m[x][y]);

        while (z >= arr.length) {
            arr = growArray(arr);
            m[x][y] = arr;
        }

        if (obj != null)
            objects++;
        else
            objects--;

        arr[z] = obj;
        modCount++;
    }

    private @NonNull Object @NonNull [] growArray(final @NonNull Object @NonNull [] arr) {
        val a = new Object[arr.length + initialCapacity];
        System.arraycopy(arr, 0, a, 0, arr.length);
        return a;
    }

    private @Nullable Object[] toArrayCell(final @Nullable Object o) {
        return (Object[]) o;
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
        if (o instanceof SpacePosition p) {
            set(p.getX(), p.getY(), o);
            return true;
        } else return false;
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

        set(x, y, position);

        return true;
    }

    /**
     * Remove the given object from the grid.
     *
     * @param o The object to be removed.
     * @return True if object was found and removed, false otherwise.
     */
    public boolean remove(final @NonNull Object o) {
        Object[] arr;
        for (int x = 0; x < xSize; x++)
            for (int y = 0; y < ySize; y++)
                if (m[x][y] != null) {
                    arr = toArrayCell(m[x][y]);
                    for (int z = 0; z < arr.length; z++)
                        if (arr[z] == o) {
                            set(x, y, z, null);
                            return true;
                        }
                }
        return false;
    }

    /**
     * Remove the {@link SpacePosition} object from the grid.
     *
     * @param position The {@link SpacePosition} object to be removed.
     * @return true if object has been removed. False if object is null or is not present on the grid.
     */
    public boolean removeGridPosition(@Nullable SpacePosition position) {
        if (position == null) return false;

        val x = position.getX();
        val y = position.getY();

        return removeAt(x, y, position);
    }

    /**
     * Remove the given object from the grid at given position. If the object position is known this method is better
     * than {@link #remove(Object)} because it works much faster.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @param o The object to be removed.
     * @return True if object was found and removed, false otherwise.
     */
    public boolean removeAt(final int x, final int y, final @NonNull Object o) {
        val arr = toArrayCell(m[x][y]);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++)
            if (arr[i] == o) {
                set(x, y, i, null);
                modCount++;
                return true;
            }

        return false;
    }

}
