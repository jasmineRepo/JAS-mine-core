package microsim.space;

import java.util.Iterator;

/**
 * A specific iterator for grids. For each value read it is possible to know what is its position on the grid is with
 * {@link #currentX()} and {@link #currentY()} methods.
 */
public interface ObjectSpaceIterator<E> extends Iterator<E> {
    /**
     * Return the current position.
     *
     * @return A {@link SpacePosition} object containing the coordinates.
     */
    SpacePosition getGridPosition();

    SpacePosition nextGridPosition();

    /**
     * Return the current {@code x} position.
     *
     * @return The {@code x} coordinate of the last read.
     */
    int currentX();

    /**
     * Return the current {@code y} position.
     *
     * @return The {@code y} coordinate of the last read.
     */
    int currentY();
}
