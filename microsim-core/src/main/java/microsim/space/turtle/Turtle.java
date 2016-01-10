package microsim.space.turtle;

import javax.persistence.MappedSuperclass;

import microsim.engine.SimulationEngine;
import microsim.space.ObjectSpace;

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
@MappedSuperclass
public class Turtle extends AbstractTurtle {

	private static final long serialVersionUID = 4897224750701553738L;

	private int degreeHeading;
	private double radiantHeading;

	private double realX, realY;

	public Turtle() {
		super(null, 0, 0);
		realX = realY = 0.0;
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
	public Turtle(ObjectSpace grid) {
		super(grid, 0, 0);
		realX = realY = 0.0;

	}

	/**
	 * Create a turtle with a given identifier on the given grid at the given
	 * position.
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
	public Turtle(ObjectSpace grid, int x, int y) {
		super(grid, x, y);
		realX = x;
		realY = y;
	}
	
	//
	public int getHeading() {
		return degreeHeading;
	}

	/**
	 * Randomize the turtle's heading. It gets a random number from 0 to 359
	 * degrees. The random generator is synchronized with JAS randomizer.
	 */
	//
	public void setRandomHeading() {
		degreeHeading = SimulationEngine.getRnd().nextInt(359);
		radiantHeading = Math.toRadians(degreeHeading);
	}

	/**
	 * Set the turtle's heading using a constant for cardinal points.
	 * 
	 * @param directionType
	 *            One of DIR_NORTH, DIR_NORTH_EAST, ... constant.
	 */
	public void setCardinalHeading(Direction directionType) {
		switch (directionType) {
		case North:
			degreeHeading = 90;
			break;
		case NorthEast:
			degreeHeading = 45;
			break;
		case East:
			degreeHeading = 0;
			break;
		case SouthEast:
			degreeHeading = 315;
			break;
		case South:
			degreeHeading = 270;
			break;
		case SouthWest:
			degreeHeading = 225;
			break;
		case West:
			degreeHeading = 180;
			break;
		case NorthWest:
			degreeHeading = 135;
			break;
		}

		radiantHeading = Math.toRadians(degreeHeading);
	}

	/**
	 * Set the current heading.
	 * 
	 * @param heading
	 *            The new heading.
	 */
	public void setHeading(int heading) {
		if (heading < 0 || heading > 360)
			throw new IndexOutOfBoundsException("An heading of " + heading
					+ " is out of bounds.\n"
					+ "Heading must be within 0-359 range.");

		degreeHeading = heading;
		radiantHeading = Math.toRadians(degreeHeading);
	}

	//
	public void turnCardinalLeft(int steps) {
		turnLeft(steps * 45);
	}

	//
	public void turnCardinalRight(int steps) {
		turnRight(steps * 45);
	}

	/**
	 * Change the current heading rotating it to the right.
	 * 
	 * @param degrees
	 *            The number of degrees to rotate the heading.
	 */
	//
	public void turnRight(int degrees) {
		if (degrees < 0)
			throw new IndexOutOfBoundsException("Turning right of " + degrees
					+ " degrees is illegal");
		degreeHeading += degrees;
		while (degreeHeading > 360)
			degreeHeading -= 360;

		radiantHeading = Math.toRadians(degreeHeading);
	}

	/**
	 * Change the current heading rotating it to the left.
	 * 
	 * @param degrees
	 *            The number of degrees to rotate the heading.
	 */
	//
	public void turnLeft(int degrees) {
		if (degrees < 0)
			throw new IndexOutOfBoundsException("Turning left of " + degrees
					+ " degrees is illegal");
		degreeHeading -= degrees;
		while (degreeHeading < 0)
			degreeHeading += 360;

		radiantHeading = Math.toRadians(degreeHeading);
	}

	/**
	 * Make some steps forward, according the current heading.
	 * 
	 * @param steps
	 *            The number of steps the turtle has to make.
	 */
	public void forward(int steps) {
		realX = getNextRealX(steps);
		realY = getNextRealY(steps);
		moveToXY(getGridX(realX), getGridY(realY));

	}

	/**
	 * Make some steps forward, but moves only if the target position is empty.
	 * 
	 * @param steps
	 *            The number of steps the turtle has to make.
	 * @return True only if the turtle has moved.
	 */
	public boolean leap(int steps) {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");

		double rX = getNextRealX(steps);
		double rY = getNextRealY(steps);
		int nX = getGridX(rX);
		int nY = getGridY(rY);
		if (grid.countObjectsAt(nX, nY) > 0)
			return false;
		moveToXY(nX, nY);
		realX = rX;
		realY = rY;
		return true;
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
		super.setXY(x, y);
		realX = x;
		realY = y;
	}

	private void moveToXY(int x, int y) {
		super.setXY(x, y);
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
	public int getNextX(int steps) {
		return getGridX(getNextRealX(steps));
	}

	private int getGridX(double aRealX) {
		int nX = (int) Math.round(aRealX);

		switch (moving) {
		case Bounded:
			return grid.boundX(nX);
		case Bounce:
			return grid.reflectX(nX);
		case Torus:
			return grid.torusX(nX);
		default:
			return nX;
		}

		// return (int) Math.round(aRealX);
	}

	private int getGridY(double aRealY) {
		// return (int) Math.round(aRealY);

		int nY = (int) Math.round(aRealY);

		switch (moving) {
		case Bounded:
			return grid.boundY(nY);
		case Bounce:
			return grid.reflectY(nY);
		case Torus:
			return grid.torusY(nY);
		default:
			return nY;
		}

	}

	private double getNextRealX(int steps) {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");

		if (degreeHeading == 90 || degreeHeading == 270)
			return realX;
		double rX = realX + steps * Math.cos(radiantHeading);

		switch (moving) {
		case Bounded:
			if (rX < 0)
				rX = 0.0;
			if (rX > grid.getXSize())
				rX = grid.getXSize();
			break;
		case Bounce:
			if (rX < 0)
				rX = -rX;
			if (rX > grid.getXSize())
				rX = 2 * grid.getXSize() - rX;
			break;
		case Torus:
			while (rX < 0)
				rX += grid.getXSize();
			while (rX > grid.getXSize())
				rX -= grid.getXSize();
		}

		return rX;
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
	public int getNextY(int steps) {
		return getGridY(getNextRealY(steps));
	}

	private double getNextRealY(int steps) {
		if (grid == null)
			throw new IllegalStateException("Turtle is not attached to any grid!");

		if (degreeHeading == 0 || degreeHeading == 180)
			return realY;
		double rY = realY + steps * Math.sin(radiantHeading);

		switch (moving) {
		case Bounded:
			if (rY < 0)
				rY = 0.0;
			if (rY > grid.getYSize())
				rY = grid.getYSize();
			break;
		case Bounce:
			if (rY < 0)
				rY = -rY;
			if (rY > grid.getYSize())
				rY = 2 * grid.getYSize() - rY;
			break;
		case Torus:
			while (rY < 0)
				rY += grid.getYSize();
			while (rY > grid.getYSize())
				rY -= grid.getYSize();
		}

		return rY;
	}

}