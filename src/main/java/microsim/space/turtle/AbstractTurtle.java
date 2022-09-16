package microsim.space.turtle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import microsim.space.ObjectSpace;
import microsim.space.SpacePosition;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

/**
 * An agent able to move itself upon an object grid. It has got some specific instruction for movement. Each turtle has
 * a heading expressed in degrees. It can make steps, turn right or left, measure the distance from another position on
 * the grid. Each turtle has a color, and it is able to draw itself on a grid drawing layer.
 */
@SuppressWarnings("unused")
@MappedSuperclass
public abstract class AbstractTurtle extends SpacePosition {

    @Serial
    private static final long serialVersionUID = -8822828953179373104L;
    @Transient
    protected ObjectSpace grid;
    @Enumerated(EnumType.STRING)
    protected MoveMode moving;

    /**
     * Creates a turtle with a given identifier on the given grid at position (0,0).
     */
    public AbstractTurtle() {
        this(null, 0, 0);
    }

    /**
     * Creates a turtle on the given grid at position(0,0).
     *
     * @param grid The grid upon the turtle moves.
     */
    public AbstractTurtle(final @NonNull ObjectSpace grid) {
        this(grid, 0, 0);
    }

    /**
     * Creates a turtle with a given identifier on the given grid at the given position. Set its color to the given
     * color.
     *
     * @param x    The initial {@code x} coordinate of the turtle.
     * @param y    The initial {@code y} coordinate of the turtle.
     * @param grid The grid upon the turtle moves.
     */
    public AbstractTurtle(final @Nullable ObjectSpace grid, final int x, final int y) {
        super(x, y);
        this.grid = grid;
        this.moving = MoveMode.Torus;
        if (grid != null) grid.addGridPosition(getPosition());
    }

    public @NonNull SpacePosition getPosition() {
        return this;
    }

    /**
     * Returns the behaviour of the turtle when it goes out of bounds of the grid.
     *
     * @return The moving type identifier.
     */
    public @NonNull MoveMode getMovingType() {
        return moving;
    }

    /**
     * Sets the behaviour of the turtle when it goes out of bounds of the grid.
     *
     * @param movingType A moving type identifier.
     */
    public void setMovingType(final @NonNull MoveMode movingType) {
        moving = movingType;
    }

    /**
     * Returns the {@code y} size of the grid.
     *
     * @return The height of the grid.
     */
    public int getWorldHeight() {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");
        return grid.getYSize();
    }

    /**
     * Returns the {@code x} size of the grid.
     *
     * @return The width of the grid.
     */
    public int getWorldWidth() {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");
        return grid.getXSize();
    }

    /**
     * Returns the current heading of the turtle.
     *
     * @return The current heading.
     */
    public abstract int getHeading();

    /**
     * Sets the current heading.
     *
     * @param heading The new heading.
     */
    public abstract void setHeading(final int heading);

    /**
     * Randomizes the turtle's heading. It gets a random number from 0 to 359 degrees. The random generator is
     * synchronized with JAS randomizer.
     */
    public abstract void setRandomHeading();

    /**
     * Sets the turtle's heading using a constant for cardinal points.
     *
     * @param directionType One of DIR_NORTH, DIR_NORTH_EAST, ... constant.
     */
    public abstract void setCardinalHeading(final @NonNull Direction directionType);

    /**
     * Changes the current heading rotating it to the right.
     *
     * @param degrees The number of degrees to rotate the heading.
     */
    public abstract void turnRight(final int degrees);

    public abstract void turnCardinalRight(final int steps);

    /**
     * Changes the current heading rotating it to the left.
     *
     * @param degrees The number of degrees to rotate the heading.
     */
    public abstract void turnLeft(final int degrees);

    public abstract void turnCardinalLeft(final int steps);

    /**
     * Makes a step forward, according the current heading.
     */
    public void forward() {
        forward(1);
    }

    /**
     * Makes some steps forward, according the current heading.
     *
     * @param steps The number of steps the turtle has to make.
     */
    public abstract void forward(final int steps);

    /**
     * Makes a step forward, but moves only if the target position is empty.
     *
     * @return True only if the turtle has moved.
     */
    public boolean leap() {
        return leap(1);
    }

    /**
     * Makes some steps forward, but moves only if the target position is empty.
     *
     * @param steps The number of steps the turtle has to make.
     * @return True only if the turtle has moved.
     */
    public abstract boolean leap(final int steps);

    /**
     * Computes the cartesian distance from its position to the given position.
     *
     * @param xCor The {@code x} coordinate.
     * @param yCor The {@code y} coordinate.
     * @return The cartesian distance between points.
     */
    public double getDistanceFrom(final int xCor, final int yCor) {
        val xC = xCor - x;
        val yC = yCor - y;
        return Math.sqrt(xC * xC + yC * yC);
    }

    /**
     * Sets a new {@code x} position.
     *
     * @param x The new {@code x} coordinate.
     */
    public void setX(final int x) {
        setXY(x, y);
    }

    /**
     * Sets a new {@code y} position.
     *
     * @param y The new {@code y} coordinate.
     */
    public void setY(final int y) {
        setXY(x, y);
    }

    /**
     * Sets a new position.
     *
     * @param x The new {@code x} coordinate.
     * @param y The new {@code y} coordinate.
     */
    public void setXY(final int x, final int y) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        grid.removeGridPosition(getPosition());
        this.x = x;
        this.y = y;
        grid.addGridPosition(getPosition());
    }

    /**
     * Sets a new position, only if new position is empty.
     *
     * @param x The new {@code x} coordinate.
     * @param y The new {@code y} coordinate.
     * @return True only if the turtle has moved.
     */
    public boolean setIfEmptyXY(final int x, final int y) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        if (grid.countObjectsAt(x, y) > 0) return false;
        setXY(x, y);
        return true;
    }

    /**
     * Returns the new {@code x} coordinate walking the current heading direction for given steps. It uses the grid
     * bound checking methods according to the turtle's current moving type.
     *
     * @param steps The number of steps to move forward.
     * @return The candidate {@code x} coordinate.
     */
    public abstract int getNextX(final int steps);

    /**
     * Returns the new {@code x} coordinate walking the current heading direction for 1 step. It uses the grid bound
     * checking methods according to the turtle's current moving type.
     *
     * @return The candidate {@code x} coordinate.
     */
    public int getNextX() {
        return getNextX(1);
    }

    /**
     * Return the new {@code y} coordinate walking the current heading direction for given steps. It uses the grid bound
     * checking methods according to the turtle's current moving type.
     *
     * @param steps The number of steps to move forward.
     * @return The candidate {@code y} coordinate.
     */
    public abstract int getNextY(final int steps);

    /**
     * Return the new {@code y} coordinate walking the current heading direction for 1 step. It uses the grid bound
     * checking methods according to the turtle's current moving type.
     *
     * @return The candidate {@code y} coordinate.
     */
    public int getNextY() {
        return getNextY(1);
    }

    public @Nullable ObjectSpace getGrid() {
        return grid;
    }

    public void setGrid(final @Nullable ObjectSpace grid) {
        this.grid = grid;
        if (grid != null) grid.addGridPosition(getPosition());
    }

    public enum Direction {
        North(0),
        NorthEast(1),
        East(2),
        SouthEast(3),
        South(4),
        SouthWest(5),
        West(6),
        NorthWest(7);

        @Getter
        private int numVal;

        Direction(final int numVal) {
            this.numVal = numVal;
        }

        public @NonNull Direction leftShift(final int steps) {
            numVal -= steps;
            while (numVal < 0)
                numVal += 8;
            while (numVal > 7)
                numVal -= 8;

            return values()[numVal];
        }

        public @NonNull Direction rightShift(final int steps) {
            numVal += steps;
            while (numVal > 7)
                numVal -= 8;
            while (numVal < 0)
                numVal += 8;

            return values()[numVal];
        }
    }

    public enum MoveMode {
        /**
         * Turtle moves on a toroidal grid.
         */
        Torus,
        /**
         * Turtle moves on a bounded grid.
         */
        Bounded,
        /**
         * Turtle moves on a walled grid.
         */
        Bounce
    }
}
