package microsim.space.turtle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import microsim.engine.SimulationEngine;
import microsim.space.ObjectSpace;

/**
 * TO DO Documentation.
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 * 
 * @author Michele Sonnessa
 *         <p>
 */
@MappedSuperclass
public class DigitalTurtle extends AbstractTurtle {
	private static final long serialVersionUID = -6624018914521929484L;

	@Enumerated(EnumType.STRING)
	protected Direction heading = Direction.North;

	/**
	 * Create a turtle with a given identifier on the given grid at position
	 * (0,0).
	 */
	public DigitalTurtle() {
		super(null, 0, 0);
	}

	/**
	 * Create a turtle with a given identifier on the given grid at position
	 * (0,0).
	 * @param grid
	 *            The grid upon the turtle moves.
	 */
	public DigitalTurtle(ObjectSpace grid) {
		super(grid, 0, 0);
	}

	/**
	 * Create a turtle with a given identifier on the given grid at the given
	 * position.
	 * @param x
	 *            The initial x coordinate of the turtle.
	 * @param y
	 *            The initial y coordinate of the turtle.
	 * @param grid
	 *            The grid upon the turtle moves.
	 */
	public DigitalTurtle(ObjectSpace grid, int x, int y) {
		super(grid, x, y);
	}

	public void turnLeft(int degrees) {
		throw new UnsupportedOperationException(
				"The digital turtle cannot turn left.");
	}

	public void turnCardinalLeft(int steps) {
		heading = heading.leftShift(steps);
	}

	public void setRandomHeading() {
		heading = Direction.values()[SimulationEngine.getRnd().nextInt(7)];
	}

	public int getHeading() {
		switch (heading) {
		case North:
			return 90;
		case NorthEast:
			return 45;
		case East:
			return 0;
		case SouthEast:
			return 315;
		case South:
			return 270;
		case SouthWest:
			return 225;
		case West:
			return 180;
		case NorthWest:
			return 135;
		default:
			throw new IndexOutOfBoundsException(
					"The current heading is not a valid heading: " + heading);
		}
	}

	public void forward(int steps) {
		int xx = getNextX(steps);
		int yy = getNextY(steps);
		setXY(xx, yy);
	}

	public boolean leap(int steps) {
		if (grid == null)
			throw new IllegalStateException(
					"Turtle is not attached to any grid!");

		int xx = getNextX(steps);
		int yy = getNextY(steps);
		if (grid.countObjectsAt(xx, yy) > 0)
			return false;

		setXY(xx, yy);
		return true;
	}

	public void turnRight(int degrees) {
		throw new UnsupportedOperationException(
				"The digital turtle cannot turn right.");
	}

	public void turnCardinalRight(int steps) {
		heading = heading.rightShift(steps);
	}

	public int getNextX(int steps) {
		int xx = x;

		switch (heading) {
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
		switch (moving) {
		case Bounded:
			xx = grid.boundX(xx);
			break;
		case Bounce:
			xx = grid.reflectX(xx);
			break;
		case Torus:
			xx = grid.torusX(xx);
			break;
		}

		return xx;
	}

	public void setHeading(int heading) {
		if (heading < 0 || heading > 359)
			throw new IndexOutOfBoundsException(
					"heading must be a value within the [0, 360) interval.");

		if (heading < 45)
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

	public void setCardinalHeading(Direction directionType) {
		this.heading = directionType;
	}

	public int getNextY(int steps) {
		int yy = y;

		switch (heading) {
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

		switch (moving) {
		case Bounded:
			yy = grid.boundY(yy);
			break;
		case Bounce:
			yy = grid.reflectY(yy);
			break;
		case Torus:
			yy = grid.torusY(yy);
			break;
		}

		return yy;
	}

}