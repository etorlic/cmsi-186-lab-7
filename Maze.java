import java.util.stream.Stream;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

public class Maze {
    // A maze is a rectangular array of cells.
    private final Cell[][] cells;

    private Location initialRatLocation;
    private Location initialCheeseLocation;
    
    /**
     * Builds and returns a new maze given a description in the form of an array
     * of strings, one for each row of the maze, with each string containing o's
     * and w's and r's and c's. o=Open space, w=Wall, r=Rat, c=Cheese.
     *
     * The constructor is private to force users to only construct mazes through one
     * of the factory methods fromString, fromFile, or fromScanner.
     */
    private Maze(String[] lines) {
        var isRectangle = true;
        var containsIllegalChars = false;
        for (var r = 0; r < lines.length; r++) {
            if (lines[0].length() != lines[r].length()){
                isRectangle = false;
            }
            for (var c : lines[r].toCharArray()) {
                if (c != 'o' && c != 'w' && c != 'r' && c != 'c') {
                    containsIllegalChars = true;
                }
            }
        }
        if (!isRectangle) {
            throw new IllegalArgumentException("Non-rectangular maze");
        }
        if (containsIllegalChars) {
            throw new IllegalArgumentException("No illegal characters");
        }

        //constructs the 2D Array of cells
        cells = new Cell[lines.length][lines[0].length()];
        for (var r = 0; r < cells.length; r++) {
            for (var c = 0; c < cells[r].length; c++) {
                if (lines[r].charAt(c) == 'o') {
                    cells[r][c] = Maze.Cell.OPEN;
                }
                if (lines[r].charAt(c) == 'w') {
                    cells[r][c] = Maze.Cell.WALL;
                }
                if (lines[r].charAt(c) == 'r') {
                    cells[r][c] = Maze.Cell.RAT;
                    if (initialRatLocation != null) {
                        throw new IllegalArgumentException("Maze can only have one rat");
                    }
                    initialRatLocation = new Location(r, c);
                }
                if (lines[r].charAt(c) == 'c') {
                    cells[r][c] = Maze.Cell.CHEESE;
                    if (initialCheeseLocation != null) {
                        throw new IllegalArgumentException("Maze can only have one cheese");
                    }
                    initialCheeseLocation = new Location(r, c);
                }
            }
        }
        if (initialRatLocation == null) {
            throw new IllegalArgumentException("Maze has no rat");
        }
        if (initialCheeseLocation == null) {
            throw new IllegalArgumentException("Maze has no cheese");
        }
    }

    public static Maze fromString(final String description) {
        return new Maze(description.trim().split("\\s+"));
    }

    public static Maze fromFile(final String filename) throws FileNotFoundException {
        return Maze.fromScanner(new Scanner(new File(filename)));
    }
    
    public static Maze fromScanner(final Scanner scanner) {
        final var lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        return new Maze(lines.toArray(new String[0]));
    }
    
    /**
     * The location object bundles the row and column values together. 
     * It also includes a whole bunch of nice little methods so our code reads nicely.
     */
    public class Location {
        private final int row;
        private final int column;

        Location(final int row, final int column) {
            this.row = row;
            this.column = column;
        }
        
        boolean isInMaze() {
            return (row >= 0) && (row < getHeight()) && (column >= 0) && (column < getWidth());
        }
        
        boolean canBeMovedTo() {
            return isInMaze() && (contents() == Cell.OPEN || contents() == Cell.CHEESE);
        }
        
        boolean hasCheese() {
            return isInMaze() && contents() == Cell.CHEESE;
        }
        
        Location above() {
            return new Location(row - 1, column);
        }
        
        Location below() {
            return new Location(row + 1, column);
        }
        
        Location toTheLeft() {
            return new Location(row, column - 1);
        }
        
        Location toTheRight() {
            return new Location(row, column + 1);
        }
        
        void place(Cell cell) {
            cells[row][column] = cell;
        }
        
        Cell contents() {
            return cells[row][column];
        }
        
        boolean isAt(final Location other) {
            return row == other.row && column == other.column;
        }
    }
    
    /**
     * A simple cell value. A cell can be open (meaning a rat has never visited it),
     * a wall, part of the rat's current path, or "tried" (meaning the rat found it
     * to be part of a dead end.
     */
    public static enum Cell {
        OPEN(' '), WALL('\u2588'), TRIED('x'), PATH('.'), RAT('r'), CHEESE('c');
        
        private char cellValue;
        
        private Cell(char cellValue){
            this.cellValue = cellValue;
        }
        public String toString() {
            return String.valueOf(this.cellValue);
        }
    }
    
    public interface MazeListener {
        void mazeChanged(Maze maze);
    }
    
    public int getWidth() {
        return cells[0].length;
    }
    
    public int getHeight() {
        return cells.length;
    }
    
    public Location getInitialRatPosition() {
        return initialRatLocation;
    }
    
    public Location getInitialCheesePosition() {
        return initialCheeseLocation;
    }
    
    /**
     * Returns a textual description of the maze, separating each row with a newline.
     */
    public String toString() {
        return Stream.of(cells)
            .map(row -> Stream.of(row).map(Cell::toString).collect(joining()))
            .collect(joining("\n"));
    }
}