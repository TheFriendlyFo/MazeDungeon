import java.util.ArrayList;
import java.util.Arrays;

public class MazeBuilder {
    private static final Point[] directions = new Point[]{
            new Point(0, 1),
            new Point(1, 0),
            new Point(0, -1),
            new Point(-1, 0)};
    private static int cellsAccessed = 0;
    private record Vector(int inX, int inY, int wall) {
    }

    public static Tile[][] buildMaze(int cellSize, int mazeSize) {
        Cell[][] cellMaze = initializeCells(cellSize, mazeSize);
        cellMaze[0][0].setAccessed();
        mazeAlgorithm(cellMaze, mazeSize, 0, 0);

        return convertToTileArray(cellMaze, cellSize, mazeSize);
    }

    private static boolean mazeAlgorithm(Cell[][] maze, int mazeSize, int x, int y) {

        for (Tile[] tiles : convertToTileArray(maze, maze[0][0].getRow(0).length, mazeSize)) {
            for (Tile tile : tiles) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
        System.out.println();


        if (cellsAccessed > mazeSize * mazeSize) return true;

        ArrayList<Vector> possibilities = new ArrayList<>();
        boolean checkComplete = false;

        while (!checkComplete) {
            possibilities.clear();
            for (int i = 0; i < directions.length; i++) {
                int newX = x + directions[i].x();
                int newY = y + directions[i].y();

                if (validCell(maze, newX, newY)) possibilities.add(new Vector(newX, newY, i));
            }

            if (possibilities.size() > 0) {
                Vector vector = possibilities.get((int) (Math.random() * possibilities.size()));

                int wall = vector.wall;
                if (wall == 1 || wall == 0) {
                    maze[y][x].lowerWall(wall);
                } else {
                    maze[vector.inY][vector.inX].lowerWall(wall - 2);
                }

                maze[vector.inY][vector.inX].setAccessed();

                cellsAccessed++;
                checkComplete = mazeAlgorithm(maze, mazeSize, vector.inX, vector.inY);
            } else {
                return false;
            }
        }
        return false;
    }

    private static Cell[][] initializeCells(int cellSize, int mazeSize) {
        Cell[][] cells = new Cell[mazeSize][mazeSize];

        for (int y = 0; y < mazeSize; y++) {
            for (int x = 0; x < mazeSize; x++) {
                cells[y][x] = new Cell(cellSize);
            }
        }

        return cells;
    }

    private static boolean validCell(Cell[][] maze, int x, int y) {
        return (0 <= x && x < maze.length) && (0 <= y && y < maze.length) && !maze[y][x].isAccessed();
    }

    private static Tile[][] convertToTileArray(Cell[][] cellMaze, int cellSize, int mazeSize) {
        Tile[][] tileMaze = new Tile[1 + cellSize * mazeSize][1 + cellSize * mazeSize];
        Arrays.fill(tileMaze[0], Tile.BARRIER);

        for (int y = 0; y < mazeSize; y++) {
            for (int cellRow = 0; cellRow < cellSize; cellRow++) {
                tileMaze[y * cellSize + cellRow][0] = Tile.BARRIER;
                for (int cellNum = 0; cellNum < mazeSize; cellNum++) {
                    for (int tileNum = 0; tileNum < cellSize; tileNum++)  {
                        tileMaze[y * cellSize + cellRow + 1][cellNum * cellSize + tileNum + 1] = cellMaze[y][cellNum].getRow(cellRow)[tileNum];
                    }
                }
            }
        }

        tileMaze[tileMaze.length - 1][0] = Tile.BARRIER;

        return tileMaze;
    }
}
