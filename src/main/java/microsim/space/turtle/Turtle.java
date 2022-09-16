package microsim.space.turtle;

import jakarta.persistence.MappedSuperclass;
import lombok.NonNull;
import lombok.val;
import microsim.engine.SimulationEngine;
import microsim.space.ObjectSpace;

import java.io.Serial;

/**
 * An agent is able to move itself upon an object grid. It has got some specific instruction for movement. Each turtle
 * has a heading expressed in degrees. It can make steps, turn right or left, measure the distance from another position
 * on the grid. Each turtle has a color, and it is able to draw itself on a grid drawing layer.
 */
@MappedSuperclass
public class Turtle extends AbstractTurtle {

    @Serial
    private static final long serialVersionUID = 4897224750701553738L;

    private int degreeHeading;
    private double radiantHeading;

    private double realX, realY;

    public Turtle() {
        super(null, 0, 0);
        realX = realY = 0.0;
    }

    /**
     * Create a turtle on the given grid at position (0,0).
     *
     * @param grid The grid upon the turtle moves.
     */
    public Turtle(final @NonNull ObjectSpace grid) {
        super(grid, 0, 0);
        realX = realY = 0.0;

    }

    /**
     * Create a turtle on the given grid at the given position.
     *
     * @param x    The initial x coordinate of the turtle.
     * @param y    The initial y coordinate of the turtle.
     * @param grid The grid upon the turtle moves.
     */
    public Turtle(final @NonNull ObjectSpace grid, final int x, final int y) {
        super(grid, x, y);
        realX = x;
        realY = y;
    }

    public int getHeading() {
        return degreeHeading;
    }

    /**
     * Set the current heading.
     *
     * @param heading The new heading.
     */
    public void setHeading(final int heading) {
        if (heading < 0 || heading > 360)
            throw new IndexOutOfBoundsException("A heading of " + heading
                + " is out of bounds.\nHeading must be within 0-359 range.");

        degreeHeading = heading;
        radiantHeading = Math.toRadians(degreeHeading);
    }

    /**
     * Randomize the turtle's heading. It gets a random number from 0 to 359 degrees.
     */
    public void setRandomHeading() {
        degreeHeading = SimulationEngine.getRnd().nextInt(359);// fixme check the old rng behaviour. It's not supposed to include 359!
        radiantHeading = Math.toRadians(degreeHeading);
    }

    /**
     * Set the turtle's heading using a constant for cardinal points.
     *
     * @param directionType One of DIR_NORTH, DIR_NORTH_EAST, ... constant.
     */
    public void setCardinalHeading(final @NonNull Direction directionType) {
        switch (directionType) {
            case North -> degreeHeading = 90;
            case NorthEast -> degreeHeading = 45;
            case East -> degreeHeading = 0;
            case SouthEast -> degreeHeading = 315;
            case South -> degreeHeading = 270;
            case SouthWest -> degreeHeading = 225;
            case West -> degreeHeading = 180;
            case NorthWest -> degreeHeading = 135;
        }

        radiantHeading = Math.toRadians(degreeHeading);
    }

    public void turnCardinalLeft(final int steps) {
        turnLeft(steps * 45);
    }

    public void turnCardinalRight(final int steps) {
        turnRight(steps * 45);
    }

    /**
     * Change the current heading rotating it to the right.
     *
     * @param degrees The number of degrees to rotate the heading.
     */
    public void turnRight(final int degrees) {
        if (degrees < 0) throw new IndexOutOfBoundsException("Turning right of " + degrees + " degrees is illegal");
        degreeHeading += degrees;
        while (degreeHeading > 360)
            degreeHeading -= 360;

        radiantHeading = Math.toRadians(degreeHeading);
    }

    /**
     * Change the current heading rotating it to the left.
     *
     * @param degrees The number of degrees to rotate the heading.
     */
    public void turnLeft(final int degrees) {
        if (degrees < 0) throw new IndexOutOfBoundsException("Turning left of " + degrees  + " degrees is illegal");
        degreeHeading -= degrees;
        while (degreeHeading < 0)
            degreeHeading += 360;

        radiantHeading = Math.toRadians(degreeHeading);
    }

    /**
     * Make some steps forward, according the current heading.
     *
     * @param steps The number of steps the turtle has to make.
     */
    public void forward(final int steps) {
        realX = getNextRealX(steps);
        realY = getNextRealY(steps);
        moveToXY(getGridX(realX), getGridY(realY));

    }

    /**
     * Make some steps forward, but moves only if the target position is empty.
     *
     * @param steps The number of steps the turtle has to make.
     * @return True only if the turtle has moved.
     */
    public boolean leap(final int steps) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        val rX = getNextRealX(steps);
        val rY = getNextRealY(steps);
        val nX = getGridX(rX);
        val nY = getGridY(rY);
        if (grid.countObjectsAt(nX, nY) > 0) return false;
        moveToXY(nX, nY);
        realX = rX;
        realY = rY;
        return true;
    }

    /**
     * Set a new position.
     *
     * @param x The new {@code x} coordinate.
     * @param y The new {@code y} coordinate.
     */
    public void setXY(final int x, final int y) {
        super.setXY(x, y);
        realX = x;
        realY = y;
    }

    private void moveToXY(final int x, final int y) {
        super.setXY(x, y);
    }

    /**
     * Return the new {@code x} coordinate walking the current heading direction for given steps. It uses the grid bound
     * checking methods according to the turtle's current moving type.
     *
     * @param steps The number of steps to move forward.
     * @return The candidate {@code x} coordinate.
     */
    public int getNextX(final int steps) {
        return getGridX(getNextRealX(steps));
    }

    private int getGridX(final double aRealX) {
        val nX = (int) Math.round(aRealX);

        return switch (moving) {
            case Bounded -> grid.boundX(nX);
            case Bounce -> grid.reflectX(nX);
            case Torus -> grid.torusX(nX);
        };
    }

    private int getGridY(final double aRealY) {
        val nY = (int) Math.round(aRealY);

        return switch (moving) {
            case Bounded -> grid.boundY(nY);
            case Bounce -> grid.reflectY(nY);
            case Torus -> grid.torusY(nY);
        };
    }

    private double getNextRealX(final int steps) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        if (degreeHeading == 90 || degreeHeading == 270) return realX;
        double rX = realX + steps * Math.cos(radiantHeading);

        switch (moving) {
            case Bounded -> {
                if (rX < 0)
                    rX = 0.0;
                if (rX > grid.getXSize())
                    rX = grid.getXSize();
            }
            case Bounce -> {
                if (rX < 0)
                    rX = -rX;
                if (rX > grid.getXSize())
                    rX = 2 * grid.getXSize() - rX;
            }
            case Torus -> {
                while (rX < 0)
                    rX += grid.getXSize();
                while (rX > grid.getXSize())
                    rX -= grid.getXSize();
            }
        }

        return rX;
    }

    /**
     * Return the new {@code y} coordinate walking the current heading direction for given steps. It uses the grid bound
     * checking methods according to the turtle's current moving type.
     *
     * @param steps The number of steps to move forward.
     * @return The candidate {@code y} coordinate.
     */
    public int getNextY(final int steps) {
        return getGridY(getNextRealY(steps));
    }

    private double getNextRealY(final int steps) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        if (degreeHeading == 0 || degreeHeading == 180) return realY;
        double rY = realY + steps * Math.sin(radiantHeading);

        switch (moving) {
            case Bounded -> {
                if (rY < 0)
                    rY = 0.0;
                if (rY > grid.getYSize())
                    rY = grid.getYSize();
            }
            case Bounce -> {
                if (rY < 0)
                    rY = -rY;
                if (rY > grid.getYSize())
                    rY = 2 * grid.getYSize() - rY;
            }
            case Torus -> {
                while (rY < 0)
                    rY += grid.getYSize();
                while (rY > grid.getYSize())
                    rY -= grid.getYSize();
            }
        }
        return rY;
    }
}
