package microsim.space.turtle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import microsim.space.ObjectSpace;
import microsim.space.SpacePosition;

import java.io.Serial;

/**
 * An agent able to move itself upon an object grid. It has got some specific
 * instruction for movement. Each turtle has an heading expressed in degrees. It
 * can make steps, turn right or left, measure the distance from another
 * position on the grid. Each turtle has a color and it is able to draw itself
 * on a grid drawing layer.
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
@MappedSuperclass public abstract class AbstractTurtle extends SpacePosition {

	@Serial private static final long serialVersionUID = -8822828953179373104L;

	public enum Direction {
		North(0),
		NorthEast(1),
		East(2),
		SouthEast(3),
		South(4),
		SouthWest(5),
		West(6),
		NorthWest(7);

		@Getter private int numVal;

		Direction(int numVal) {
			this.numVal = numVal;
		}

		public Direction leftShift(int steps) {
			numVal -= steps;
			while (numVal < 0)
				numVal += 8;
			while (numVal > 7)
				numVal -= 8;

			return values()[numVal];
		}

		public Direction rightShift(int steps) {
			numVal += steps;
			while (numVal > 7)
				numVal -= 8;
			while (numVal < 0)
				numVal += 8;

			return values()[numVal];
		}

	}

	public enum MoveMode {
		Torus, /** Turtle moves on a toroidal grid. */
		Bounded, /** Turtle moves on a bounded grid. */
		Bounce
		/** Turtle moves on a walled grid. */
	}

	@Transient
	protected ObjectSpace grid;

	@Enumerated(EnumType.STRING)
	protected MoveMode moving;

	/**
	 * Create a turtle with a given identifier on the given grid at position
	 * (0,0).
	 *
	 * @param id
	 *            The identifier for turtle.
	 * @param grid
	 *            The grid upon the turtle moves.
	 */
	public AbstractTurtle() {
		this(null, 0, 0);
	}

	/**
	 * Create a turtle with a given identifier on the given grid at position
	 * (0,0).
	 *
	 * @param id
	 *            The identifier for turtle.
	 * @param grid
	 *            The grid upon the turtle moves.
	 */
	public AbstractTurtle(ObjectSpace grid) {
		this(grid, 0, 0);
	}

	/**
	 * Create a turtle with a given identifier on the given grid at the given
	 * position. Set its color to the given color.
	 *
	 * @param id
	 *            The identifier for turtle.
	 * @param x
	 *            The initial x coordinate of the turtle.
	 * @param y
	 *            The initial y coordinate of the turtle.
	 * @param grid
	 *            The grid upon the turtle moves.
	 */
	public AbstractTurtle(ObjectSpace grid, int x, int y) {
		super(x, y);
		this.grid = grid;
		this.moving = MoveMode.Torus;
		if (grid != null)
			grid.addGridPosition(getPosition());
	}

	public SpacePosition getPosition() {
		return this;
	}


	/**
	 * Return the behaviour of the turtle when it goes out of bounds of the
	 * grid.
	 *
	 * @return The moving type identifier.
	 */
	public MoveMode getMovingType() {
		return moving;
	}

	/**
	 * Set the behaviour of the turtle when it goes out of bounds of the grid.
	 *
	 * @param movingType
	 *            A moving type identifier.
	 */
	public void setMovingType(MoveMode movingType) {
		moving = movingType;
	}

	/**
	 * Return the y size of the grid.
	 *
	 * @return The height of the grid.
	 */
	public int getWorldHeight() {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");
		return grid.getXSize();
	}

	/**
	 * Return the x size of the grid.
	 *
	 * @return The width of the grid.
	 */
	public int getWorldWidth() {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");
		return grid.getYSize();
	}

	/**
	 * Return the current heading of the turtle.
	 *
	 * @return The current heading.
	 */
	public abstract int getHeading();

	/**
	 * Randomize the turtle's heading. It gets a random number from 0 to 359
	 * degrees. The random generator is synchronized with JAS randomizer.
	 */
	public abstract void setRandomHeading();

	/**
	 * Set the turtle's heading using a constant for cardinal points.
	 *
	 * @param directionType
	 *            One of DIR_NORTH, DIR_NORTH_EAST, ... constant.
	 */
	public abstract void setCardinalHeading(Direction directionType);

	/**
	 * Set the current heading.
	 *
	 * @param heading
	 *            The new heading.
	 */
	public abstract void setHeading(int heading);

	/**
	 * Change the current heading rotating it to the right.
	 *
	 * @param degrees
	 *            The number of degrees to rotate the heading.
	 */
	public abstract void turnRight(int degrees);

	public abstract void turnCardinalRight(int steps);

	/**
	 * Change the current heading rotating it to the left.
	 *
	 * @param degrees
	 *            The number of degrees to rotate the heading.
	 */
	public abstract void turnLeft(int degrees);

	public abstract void turnCardinalLeft(int steps);

	/** Make a step forward, according the current heading. */
	public void forward() {
		forward(1);
	}

	/**
	 * Make some steps forward, according the current heading.
	 *
	 * @param steps
	 *            The number of steps the turtle has to make.
	 */
	public abstract void forward(int steps);

	/**
	 * Make a steps forward, but moves only if the target position is empty.
	 *
	 * @return True only if the turtle has moved.
	 */
	public boolean leap() {
		return leap(1);
	}

	/**
	 * Make some steps forward, but moves only if the target position is empty.
	 *
	 * @param steps
	 *            The number of steps the turtle has to make.
	 * @return True only if the turtle has moved.
	 */
	public abstract boolean leap(int steps);

	/**
	 * Compute the cartesian distance from its position to the given position.
	 * WARNING The (xCor, yCor) must be within the grid. The bounds are not
	 * checked.
	 *
	 * @param xCor
	 *            The target x coordinate.
	 * @param yCor
	 *            The target y coordinate.
	 * @return The cartesian distance between points.
	 */
	public double getDistanceFrom(int xCor, int yCor) {
		int xC = xCor - x;
		int yC = yCor - y;
		return Math.sqrt(xC * xC + yC * yC);
	}

	/**
	 * Return the current x position.
	 *
	 * @return The x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the current y position.
	 *
	 * @return The y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set a new x position.
	 *
	 * @param x
	 *            The new x coordinate.
	 */
	public void setX(int x) {
		setXY(x, y);
	}

	/**
	 * Set a new y position.
	 *
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(int y) {
		setXY(x, y);
	}

	/**
	 * Set a new position.
	 *
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 */
	public void setXY(int x, int y) {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");

		grid.removeGridPosition(getPosition());
		this.x = x;
		this.y = y;
		grid.addGridPosition(getPosition());
	}

	/**
	 * Set a new position, only if new position is empty.
	 *
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 * @return True only if the turtle has moved.
	 */
	public boolean setIfEmptyXY(int x, int y) {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");

		if (grid.countObjectsAt(x, y) > 0)
			return false;
		setXY(x, y);
		return true;
	}

	/**
	 * Return the new x coordinate walking the current heading direction for
	 * given steps. It uses the grid bound checking methods according to the
	 * turtle's current moving type.
	 *
	 * @param steps
	 *            The number of steps to move forward.
	 * @return The candidate x coordinate.
	 */
	public abstract int getNextX(int steps);

	/**
	 * Return the new x coordinate walking the current heading direction for 1
	 * step. It uses the grid bound checking methods according to the turtle's
	 * current moving type.
	 *
	 * @return The candidate x coordinate.
	 */
	public int getNextX() {
		return getNextX(1);
	}

	/**
	 * Return the new y coordinate walking the current heading direction for
	 * given steps. It uses the grid bound checking methods according to the
	 * turtle's current moving type.
	 *
	 * @param steps
	 *            The number of steps to move forward.
	 * @return The candidate y coordinate.
	 */
	public abstract int getNextY(int steps);

	/**
	 * Return the new y coordinate walking the current heading direction for 1
	 * step. It uses the grid bound checking methods according to the turtle's
	 * current moving type.
	 *
	 * @return The candidate y coordinate.
	 */
	public int getNextY() {
		return getNextY(1);
	}

	public void setGrid(ObjectSpace grid) {
		this.grid = grid;
		if (grid != null)
			grid.addGridPosition(getPosition());
	}

	public ObjectSpace getGrid() {
		return grid;
	}

}
