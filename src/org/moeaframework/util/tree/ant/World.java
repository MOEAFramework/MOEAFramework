package org.moeaframework.util.tree.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.moeaframework.util.io.CommentedLineReader;

public class World {
	
	private int x;
	
	private int y;
	
	private int width;
	
	private int height;
	
	private State[][] map;
	
	private Direction direction;
	
	private int numberOfMoves;
	
	private int remainingMoves;
	
	private int maxMoves;
	
	private int foodEaten;
	
	private int totalFood;
	
	public World(File file, int maxMoves) throws FileNotFoundException, IOException {
		this(new FileReader(file), maxMoves);
	}
	
	public World(InputStream stream, int maxMoves) throws IOException {
		this(new InputStreamReader(stream), maxMoves);
	}
	
	public World(Reader reader, int maxMoves) throws IOException {
		super();
		this.maxMoves = maxMoves;
		
		load(reader);
		reset();
	}
	
	protected void load(Reader reader) throws IOException {
		CommentedLineReader lineReader = null;
		
		try {
			lineReader = new CommentedLineReader(reader);
			
			//read out the world dimension
			String[] tokens = lineReader.readLine().split("\\s+");
			width = Integer.parseInt(tokens[0]);
			height = Integer.parseInt(tokens[1]);
			map = new State[width][height];
			
			//read the world state
			int i = 0;
			int j = 0;
			String line = null;
			
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
				
				while (i < width) {
					map[i][j] = State.EMPTY;
					i++;
				}
				
				j++;
			}
			
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
	
	public int getMaxMoves() {
		return maxMoves;
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}

	public int getNumberOfMoves() {
		return numberOfMoves;
	}
	
	public int getFoodEaten() {
		return foodEaten;
	}
	
	public int getTotalFood() {
		return totalFood;
	}
	
	public int getRemainingFood() {
		return totalFood - foodEaten;
	}

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
					System.out.print("@");
					break;
				default:
					System.out.print("?");
					break;
				}
			}
			
			System.out.println();
		}
	}

}
