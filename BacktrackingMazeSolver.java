import java.util.Stack;

public class BacktrackingMazeSolver {

    /**
     * Moves a rat from (x1,y1) to (x2,y2), filling in the cells as it goes, and
     * notifying a listener at each step.
     */
    public boolean solve(Maze maze, Maze.MazeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        var path = new Stack<Maze.Location>();
        var current = maze.getInitialRatPosition();

        // Solution loop. At each step, places the rat and notifies the listener.
        while (true) {
            current.place(Maze.Cell.RAT);
            listener.mazeChanged(maze);

            if (current.isAt(maze.getInitialCheesePosition())) {
                return true;
            }

            // Move to an adjacent open cell, leaving a breadcrumb. If we
            // can't move at all, backtrack. If there's nowhere to backtrack
            // to, we're totally stuck.
            if (current.above().canBeMovedTo()) {
                path.push(current);
                current.place(Maze.Cell.PATH);
                current = current.above();
            } else if (current.toTheRight().canBeMovedTo()) {
                path.push(current);
                current.place(Maze.Cell.PATH);
                current = current.toTheRight();
            } else if (current.below().canBeMovedTo()) {
                path.push(current);
                current.place(Maze.Cell.PATH);
                current = current.below();
            } else if (current.toTheLeft().canBeMovedTo()) {
                path.push(current);
                current.place(Maze.Cell.PATH);
                current = current.toTheLeft();
            } else {
                current.place(Maze.Cell.TRIED);
                if (path.isEmpty()) {
                    return false;
                }
                path.pop();
            }
        }
    }
}