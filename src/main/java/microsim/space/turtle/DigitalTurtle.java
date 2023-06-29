package microsim.space.turtle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.NonNull;
import lombok.val;
import microsim.engine.SimulationEngine;
import microsim.space.ObjectSpace;

import java.io.Serial;

@MappedSuperclass
public class DigitalTurtle extends AbstractTurtle {
    @Serial
    private static final long serialVersionUID = -6624018914521929484L;

    @Enumerated(EnumType.STRING)
    protected Direction heading = Direction.North;

    /**
     * Create a turtle at position (0,0).
     */
    public DigitalTurtle() {
        super(null, 0, 0);
    }

    /**
     * Create a turtle on the given grid at position (0,0).
     *
     * @param grid The grid upon the turtle moves.
     */
    public DigitalTurtle(final @NonNull ObjectSpace grid) {
        super(grid, 0, 0);
    }

    /**
     * Create a turtle on the given grid at the given position.
     *
     * @param x    The initial x coordinate of the turtle.
     * @param y    The initial y coordinate of the turtle.
     * @param grid The grid upon the turtle moves.
     */
    public DigitalTurtle(final @NonNull ObjectSpace grid,final int x, final int y) {
        super(grid, x, y);
    }

    public void turnLeft(final int degrees) {
        throw new UnsupportedOperationException("The digital turtle cannot turn left.");
    }

    public void turnCardinalLeft(final int steps) {
        heading = heading.leftShift(steps);
    }

    public void setRandomHeading() {
        heading = Direction.values()[SimulationEngine.getRnd().nextInt(7)];
    }

    public int getHeading() {
        return switch (heading) {
            case North -> 90;
            case NorthEast -> 45;
            case East -> 0;
            case SouthEast -> 315;
            case South -> 270;
            case SouthWest -> 225;
            case West -> 180;
            case NorthWest -> 135;
        };
    }

    public void setHeading(final int heading) {
        if (heading < 0 || heading > 359)
            throw new IndexOutOfBoundsException("Heading must be a value within the [0, 360) interval.");

        if (heading < 45)//fixme switch?
            this.heading = Direction.East;
        else if (heading < 90)
            this.heading = Direction.NorthEast;
        else if (heading < 135)
            this.heading = Direction.North;
        else if (heading < 180)
            this.heading = Direction.NorthWest;
        else if (heading < 225)
            this.heading = Direction.West;
        else if (heading < 270)
            this.heading = Direction.SouthWest;
        else if (heading < 315)
            this.heading = Direction.South;
        else
            this.heading = Direction.SouthEast;

    }

    public void forward(final int steps) {
        val xx = getNextX(steps);
        val yy = getNextY(steps);
        setXY(xx, yy);
    }

    public boolean leap(final int steps) {
        if (grid == null) throw new IllegalStateException("Turtle is not attached to any grid!");

        val xx = getNextX(steps);
        val yy = getNextY(steps);
        if (grid.countObjectsAt(xx, yy) > 0) return false;

        setXY(xx, yy);
        return true;
    }

    public void turnRight(final int degrees) {
        throw new UnsupportedOperationException("The digital turtle cannot turn right.");
    }

    public void turnCardinalRight(final int steps) {
        heading = heading.rightShift(steps);
    }

    public int getNextX(final int steps) {
        int xx = x;

        switch (heading) {//fixme
            case NorthEast:
            case East:
            case SouthEast:
                xx += steps;
                break;
            case SouthWest:
            case West:
            case NorthWest:
                xx -= steps;
                break;
            default:
                return x;
        }
        xx = switch (moving) {
            case Bounded -> grid.boundX(xx);
            case Bounce -> grid.reflectX(xx);
            case Torus -> grid.torusX(xx);
        };

        return xx;
    }

    public void setCardinalHeading(final @NonNull Direction directionType) {
        this.heading = directionType;
    }

    public int getNextY(final int steps) {
        int yy = y;

        switch (heading) {//fixme
            case North:
            case NorthEast:
            case NorthWest:
                yy -= steps;
                break;
            case SouthEast:
            case SouthWest:
            case South:
                yy += steps;
                break;
            default:
                return y;
        }

        yy = switch (moving) {
            case Bounded -> grid.boundY(yy);
            case Bounce -> grid.reflectY(yy);
            case Torus -> grid.torusY(yy);
        };

        return yy;
    }
}
