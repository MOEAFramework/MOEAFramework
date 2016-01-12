/* Copyright 2009-2016 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.examples.gp.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * The world that the ant occupies.  The world is cyclic, so an ant walking
 * off the boundary on one side will appear on the boundary at the opposite
 * side.
 */
public class World {
	
	/**
	 * The current horizontal position of the ant.
	 */
	private int x;
	
	/**
	 * The current vertical position of the ant.
	 */
	private int y;
	
	/**
	 * The width of the world.
	 */
	private int width;
	
	/**
	 * The height of the world.
	 */
	private int height;
	
	/**
	 * The state of each cell in the world.
	 */
	private State[][] map;
	
	/**
	 * The current direction the ant is facing.
	 */
	private Direction direction;
	
	/**
	 * The number of moves required by the ant to find and eat all the food.
	 * This does not count moves expended after the last piece of food was
	 * eaten.
	 */
	private int numberOfMoves;
	
	/**
	 * The number of remaining moves the ant can perform to find food.
	 */
	private int remainingMoves;
	
	/**
	 * The maximum number of moves the ant can expend to find food.
	 */
	private int maxMoves;
	
	/**
	 * The amount of food eaten by the ant.
	 */
	private int foodEaten;
	
	/**
	 * The total amount of food available in this world.
	 */
	private int totalFood;
	
	/**
	 * Constructs a new world using the ant trail defined in the specified
	 * file.
	 * 
	 * @param file the file containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException if an I/O error occurred
	 */
	public World(File file, int maxMoves) throws FileNotFoundException,
	IOException {
		this(new FileReader(file), maxMoves);
	}
	
	/**
	 * Constructs a new world using the ant trail defined in the specified
	 * input stream.
	 * 
	 * @param stream the stream containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws IOException if an I/O error occurred
	 */
	public World(InputStream stream, int maxMoves) throws IOException {
		this(new InputStreamReader(stream), maxMoves);
	}
	
	/**
	 * Constructs a new world using the ant trail defined in the specified
	 * reader.
	 * 
	 * @param reader the reader containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws IOException if an I/O error occurred
	 */
	public World(Reader reader, int maxMoves) throws IOException {
		super();
		this.maxMoves = maxMoves;
		
		load(reader);
		reset();
	}
	
	/**
	 * Loads the ant trail.
	 * 
	 * @param reader the reader containing the ant trail
	 * @throws IOException if an I/O error occurred
	 */
	protected void load(Reader reader) throws IOException {
		BufferedReader lineReader = null;
		
		try {
			lineReader = new BufferedReader(reader);
			
			//read out the world dimension
			String line = lineReader.readLine();
			
			if (line == null) {
				throw new IOException("trail missing header line");
			}
			
			String[] tokens = line.split("\\s+");
			width = Integer.parseInt(tokens[0]);
			height = Integer.parseInt(tokens[1]);
			map = new State[width][height];
			
			//read the world state
			int i = 0;
			int j = 0;
			
			while ((line = lineReader.readLine()) != null) {
				i = 0;
				
				while (i < line.length()) {
					char c = line.charAt(i);
						
					if (c == ' ') {
						map[i][j] = State.EMPTY;
					} else if (c == '#') {
						map[i][j] = State.FOOD;
						totalFood++;
					} else if (c == '.') {
						map[i][j] = State.TRAIL;
					} else {
						throw new IllegalStateException();
					}
					
					i++;
				}
				
				// fill any remaining columns
				while (i < width) {
					map[i][j] = State.EMPTY;
					i++;
				}
				
				j++;
			}
			
			// fill any remaining rows
			while (j < height) {
				i = 0;
				
				while (i < width) {
					map[i][j] = State.EMPTY;
					i++;
				}
				
				j++;
			}
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
	}
	
	/**
	 * Resets this world, returning the ant to its starting position and
	 * resetting the state of all cells to their original states.
	 */
	public void reset() {
		x = 0;
		y = 0;
		direction = Direction.EAST;
		remainingMoves = maxMoves;
		foodEaten = 0;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (map[i][j].equals(State.EATEN)) {
					map[i][j] = State.FOOD;
				}
			}
		}
	}
	
	/**
	 * Returns the maximum number of moves the ant can expend to find food.
	 * 
	 * @return the maximum number of moves the ant can expend to find food
	 */
	public int getMaxMoves() {
		return maxMoves;
	}
	
	/**
	 * Returns the number of remaining moves the ant can perform to find food.
	 * 
	 * @return the number of remaining moves the ant can perform to find food
	 */
	public int getRemainingMoves() {
		return remainingMoves;
	}

	/**
	 * Returns the number of moves required by the ant to find and eat all the
	 * food.  This does not count moves expended after the last piece of food
	 * was eaten.
	 * 
	 * @return the number of moves required by the ant to find and eat all the
	 *         food
	 */
	public int getNumberOfMoves() {
		return numberOfMoves;
	}
	
	/**
	 * Returns the amount of food eaten by the ant.
	 * 
	 * @return the amount of food eaten by the ant
	 */
	public int getFoodEaten() {
		return foodEaten;
	}
	
	/**
	 * Returns the total amount of food available in this world.
	 * 
	 * @return the total amount of food available in this world
	 */
	public int getTotalFood() {
		return totalFood;
	}
	
	/**
	 * Returns the amount of food remaining in this world not yet eaten by the
	 * ant.
	 * 
	 * @return the amount of food remaining in this world not yet eaten by the
	 * ant
	 */
	public int getRemainingFood() {
		return totalFood - foodEaten;
	}

	/**
	 * Turns the ant right.
	 */
	public void turnRight() {
		switch (direction) {
		case NORTH:
			direction = Direction.EAST;
			break;
		case SOUTH:
			direction = Direction.WEST;
			break;
		case EAST:
			direction = Direction.SOUTH;
			break;
		case WEST:
			direction = Direction.NORTH;
			break;
		default:
			throw new IllegalStateException();
		}
		
		remainingMoves--;
	}
	
	/**
	 * Turns the ant left.
	 */
	public void turnLeft() {
		switch (direction) {
		case NORTH:
			direction = Direction.WEST;
			break;
		case SOUTH:
			direction = Direction.EAST;
			break;
		case EAST:
			direction = Direction.NORTH;
			break;
		case WEST:
			direction = Direction.SOUTH;
			break;
		default:
			throw new IllegalStateException();
		}
		
		remainingMoves--;
	}
	
	/**
	 * Moves the ant forward one position in the direction it is facing.  If
	 * the ant has already expended all available moves, the ant remains
	 * stationary.
	 */
	public void moveForward() {
		if (getRemainingMoves() <= 0) {
			return;
		}
		
		switch (direction) {
		case NORTH:
			y = (y - 1 + height) % height;
			break;
		case SOUTH:
			y = (y + 1) % height;
			break;
		case EAST:
			x = (x + 1) % width;
			break;
		case WEST:
			x = (x - 1 + width) % width;
			break;
		default:
			throw new IllegalStateException();
		}
		
		if (map[x][y].equals(State.FOOD)) {
			map[x][y] = State.EATEN;
			foodEaten++;
			numberOfMoves = maxMoves - remainingMoves;
		}
		
		remainingMoves--;
	}
	
	/**
	 * Returns {@code true} if food is located in the position directly ahead
	 * of the ant; {@code false} otherwise.
	 * 
	 * @return {@code true} if food is located in the position directly ahead
	 *         of the ant; {@code false} otherwise
	 */
	public boolean isFoodAhead() {
		switch (direction) {
		case NORTH:
			return map[x][(y - 1 + height) % height].equals(State.FOOD);
		case SOUTH:
			return map[x][(y + 1) % height].equals(State.FOOD);
		case EAST:
			return map[(x + 1) % width][y].equals(State.FOOD);
		case WEST:
			return map[(x - 1 + width) % width][y].equals(State.FOOD);
		default:
			throw new IllegalStateException();
		}
	}
	
	/**
	 * Prints a visual representation of the world to {@code System.out}.
	 */
	public void display() {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				switch (map[i][j]) {
				case FOOD:
					System.out.print('#');
					break;
				case EMPTY:
					System.out.print(' ');
					break;
				case TRAIL:
					System.out.print('.');
					break;
				case EATEN:
					System.out.print('@');
					break;
				default:
					System.out.print('?');
					break;
				}
			}
			
			System.out.println();
		}
	}

}
