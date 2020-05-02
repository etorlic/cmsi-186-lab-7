import java.util.stream.Stream;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Maze {

    // A maze is a rectangular array of cells. The reason we use arrays is that
    // the maze has a fixed size, and arrays are the fastest when indexing by
    // position, which is exactly what we do when we search a maze.
    private final Cell[][] cells;

    private Location initialRatLocation;
    private Location initialCheeseLocation;

    /**
     * Builds and returns a new maze given a description in the form of an array
     * of strings, one for each row of the maze, with each string containing o's
     * and w's and r's and c's. o=Open space, w=Wall, r=Rat, c=Cheese.
     *
     * The maze must be rectangular and contain nothing but legal characters. There
     * must be exactly one 'r' and exactly one 'c'.
     *
     * The constructor is private to force users to only construct mazes through one
     * of the factory methods fromString, fromFile, or fromScanner.
     */
    private Maze(String[] lines) {
        var isRectangle = true;
        var containsIllegalChars = false;
        var ratCounter = 0;
        var cheeseCounter = 0;
        for (var r = 0; r < lines.length; r++) {
            if (lines[0].length() != lines[r].length()){
                isRectangle = false;
            } 
            if (!lines[r].contains("o")
                || !lines[r].contains("w") 
                || !lines[r].contains("r")
                || !lines[r].contains("c")) {
                containsIllegalChars = true;
            }
            if (lines[r].contains("r")) {
                ratCounter++;
            }
            if (lines[r].contains("c")) {
                cheeseCounter++;
            }
        } if (!isRectangle) {
            throw new IllegalArgumentException("Non-rectangular maze");
        } if (containsIllegalChars) {
            throw new IllegalArgumentException("No illegal characters");
        } if (ratCounter != 1 && ratCounter != 0) {
            throw new IllegalArgumentException("Maze can only have one rat");
        } if (ratCounter == 0) {
            throw new IllegalArgumentException("Maze has no rat");
        } if (cheeseCounter != 1 && cheeseCounter != 0) {
            throw new IllegalArgumentException("Maze can only have one cheese");
        } if (cheeseCounter == 0) {
            throw new IllegalArgumentException("Maze has no cheese");
        }

        var ratRow = -1;
        var ratColumn = -1;
        var cheeseRow = -1;
        var cheeseColumn = -1;
        cells = new Cell[lines.length][lines[0].length()];
        for (var r = 0; r < cells.length; r++) {
            for (var c = 0; c < cells[r].length; c++) {
                if (lines[r].substring(c, c + 1) == "o") {
                    cells[r][c] = Maze.Cell.OPEN;
                } if (lines[r].substring(c, c + 1) == "w") {
                    cells[r][c] = Maze.Cell.WALL;
                } if (lines[r].substring(c, c + 1) == "r") {
                    cells[r][c] = Maze.Cell.RAT;
                } if (lines[r].substring(c, c + 1) == "c") {
                    cells[r][c] = Maze.Cell.CHEESE;
                }

                if(cells[r][c].toString().contains("r")) {
                    ratRow = r;
                    ratColumn = c;
                } if (cells[r][c].toString().contains("c")) {
                    cheeseRow = r;
                    cheeseColumn = c;
                }
            }
        }
        initialRatLocation = new Location(ratRow, ratColumn);
        initialCheeseLocation = new Location(ratRow, ratColumn);
    }

    public static Maze fromString(final String description) {
        return new Maze(description.trim().split("\\s+"));
    }

    public static Maze fromFile(final String filename) throws FileNotFoundException {
        return Maze.fromScanner(new Scanner(new File(filename)));
    }

    public static Maze fromScanner(final Scanner scanner) {
        String[] lines;
        while (scanner.hasNextLine()) {
            lines = scanner.nextLine().split("\\s");
        }
        return new Maze(lines);
    }

    /**
     * A nice representation of a Location, so we don't have to litter our code
     * with separate row and column variables! A location object bundles these
     * two values together. It also includes a whole bunch of nice little methods
     * so that our code reads nicely.
     */
    public class Location {
        private final int row;
        private final int column;

        Location(final int row, final int column) {
            this.row = row;
            this.column = column;
        }

        boolean isInMaze() {
            if (row > getHeight() || row < 0 || column > getWidth() || column < 0) {
                return false;
            } else {
                return true;
            }
        }

        boolean canBeMovedTo() {
            if ((contents().toString().contains("o") || hasCheese()) && isInMaze()) {
                return true;
            } else {
                return false;
            }
        }

        boolean hasCheese() {
            if (contents().toString().contains("c")) {
                return true;
            } else {
                return false;
            }
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
            return contents().equals(other);
        }
    }

    /**
     * A simple cell value. A cell can be open (meaning a rat has never visited it),
     * a wall, part of the rat's current path, or "tried" (meaning the rat found it
     * to be part of a dead end.
     */
    public static enum Cell {
        OPEN(String.valueOf(' ')), 
        WALL(String.valueOf('\u2588')), 
        TRIED(String.valueOf('x')), 
        PATH(String.valueOf('.')), 
        RAT(String.valueOf('r')), 
        CHEESE(String.valueOf('c'));

        private String cellValue;

        private Cell(String cellValue){
            this.cellValue = cellValue;
        }

        public String toString() {
            return this.cellValue;
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
