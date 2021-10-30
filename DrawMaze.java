package cpts132.graphs;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 * A simple class to support drawing the maze.
 * @author Dan
 */
public class DrawMaze extends JPanel {

    /**
     * The width of the maze given in the constructor
     */
    public final int width;
    /**
     * The height of the maze given in the constructor
     */
    public final int height;
    /**
     * The width of the maze halls, a constant
     */
    public static final int SIZE = 35;
    /**
     * The thickness of the maze walls, a constant
     */
    public static final int WALL = 5;
    /**
     * The thickness of the border around the maze, a constant
     */
    public static final int BORDER = 10;

    // storage for the walls that are added
    private Set<Wall> walls;

    List<Point> availableList = new ArrayList();

    static final int mazeHeight = 10;
    static final int mazeWidth = 10;

    /**
     * Construct a simple maze
     *
     * @param w The width of the maze in "cells"
     * @param h The height of the maze in "cells"
     */
    public DrawMaze(int w, int h) {
        width = w;
        height = h;
        setPreferredSize(new Dimension(width * SIZE + 2 * BORDER + WALL,
                height * SIZE + 2 * BORDER + WALL));
        walls = new HashSet<Wall>();
    }

    /**
     * Add a vertical wall to the maze.
     *
     * @param x   The horizontal offset for the wall
     * @param y   The vertical offset for the wall
     * @param len The length of the wall
     * @return True, if the requested wall added to the set
     * of walls
     */
    public boolean addVerticalWall(int x, int y, int len) {
        if (x > width || y + len > height)
            throw new IllegalArgumentException("Wall exceeds maze boundary");
        boolean added = false;
        for (int i = 0; i < len; i++) {
            if (addVerticalWall(x, y + i)) added = true;
        }
        return added;
    }

    /**
     * Add a vertical wall one cell long to the maze.
     *
     * @param x The horizontal offset for the wall
     * @param y The vertical offset for the wall
     * @return True, if the requested wall added to the set
     * of walls
     */
    public boolean addVerticalWall(int x, int y) {
        if (x > width || y + 1 > height)
            throw new IllegalArgumentException("Wall exceeds maze boundary");
        return walls.add(new Wall(x, y, false));
    }

    /**
     * Add a horizontal wall to the maze.
     *
     * @param x   The horizontal offset for the wall
     * @param y   The vertical offset for the wall
     * @param len The length of the wall
     * @return True, if the requested wall added to the set
     * of walls
     */
    public boolean addHorizontalWall(int x, int y, int len) {
        if (x + len > width || y > height)
            throw new IllegalArgumentException("Wall exceeds maze boundary");
        boolean added = false;
        for (int i = 0; i < len; i++) {
            if (addHorizontalWall(x + i, y)) added = true;
        }
        return added;
    }

    /**
     * Add a horizontal wall one cell long to the maze.
     *
     * @param x The horizontal offset for the wall
     * @param y The vertical offset for the wall
     * @return True, if the requested wall added to the set
     * of walls
     */
    public boolean addHorizontalWall(int x, int y) {
        if (x + 1 > width || y > height)
            throw new IllegalArgumentException("Wall exceeds maze boundary");
        return walls.add(new Wall(x, y, true));
    }

    /**
     * Display the maze in a JFrame
     */
    public void display() {
        JFrame win = new JFrame("My Maze");
        win.setLocation(25, 25);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.add(this);
        win.pack();
        win.setVisible(true);
    }

    /**
     * Paint the maze component
     *
     * @param g The graphics object for rendering
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(WALL));
        // draw the inner walls
        for (Wall wall : walls) {
            drawWall(g2, wall);
        }
    }

    // helper method to draw a horizontal wall
    void drawWall(Graphics g, Wall wall) {
        int x = wall.x;
        int y = wall.y;
        if (wall.horz) {
            g.drawLine(BORDER + x * SIZE, BORDER + y * SIZE, BORDER + (x + 1) * SIZE, BORDER + y * SIZE);
        } else {
            g.drawLine(BORDER + x * SIZE, BORDER + y * SIZE, BORDER + x * SIZE, BORDER + (y + 1) * SIZE);
        }
    }

    /**
     * Access to the set of walls
     *
     * @return the set of walls
     */
    public Set<Wall> getWallSet() {
        return walls;
    }

    public Point getRandomWall() {
        Set<Wall> walls = getWallSet();
        int size = walls.size();
        Random random = new Random();
        int item = random.nextInt(size);
        int i = 0;
        for (Wall wall : walls) {
            if (i == item) {
                if (wall.horz) {
                    if (wall.x == 0) {
                        return new Point(wall.x + 1, wall.y);
                    } else {
                        return new Point(wall.x, wall.y);
                    }
                } else {
                    if (wall.y == 0) {
                        return new Point(wall.x, wall.y + 1);
                    } else {
                        return new Point(wall.x, wall.y);
                    }
                }
            }
            i++;
        }
        return null;
    }

    public Point getAvailablePoint(Point point) {
        Iterator<Point> pointIterator = availableList.iterator();
        while(pointIterator.hasNext()) {
            Point iterPoint = pointIterator.next();
            if(iterPoint.equals(point)) {
                return iterPoint;
            }
        }

        return null;
    }

    public boolean existingWall(int x, int y, boolean horiz) {
        Iterator<Wall> wallIterator = getWallSet().iterator();
        while(wallIterator.hasNext()) {
            Wall wall = wallIterator.next();
            if(wall.x == x && wall.y == y && wall.horz == horiz) {
                return true;
            }
        }

        return false;
    }

    public Point getRandomNeighbor(Point point) {
        List<Point> neighbors = new ArrayList<>();

        // check compass points
        // north
        Point newPoint = new Point(point.x, point.y - 1);
        if(getAvailablePoint(newPoint) != null) {
            // existing wall?
            if(!existingWall(point.x, point.y - 1, false)) {
                neighbors.add(newPoint);
            }
        }

        // east
        newPoint = new Point(point.x + 1, point.y);
        if(getAvailablePoint(newPoint) != null) {
            if(!existingWall(point.x, point.y, true)) {
                neighbors.add(newPoint);
            }
        }

        // south
        newPoint = new Point(point.x, point.y + 1);
        if(getAvailablePoint(newPoint) != null) {
            if(!existingWall(point.x, point.y, false)) {
                neighbors.add(newPoint);
            }
        }

        // west
        newPoint = new Point(point.x - 1, point.y);
        if(getAvailablePoint(newPoint) != null) {
            if(!existingWall(point.x - 1, point.y, true)) {
                neighbors.add(newPoint);
            }
        }

        // any neighbors?
        // if not, remove us from the available list
        if(neighbors.isEmpty()) {
            availableList.remove(point);
            return null;
        }

        Random random = new Random();
        Point randomNeighborPoint = getAvailablePoint(neighbors.get(random.nextInt(neighbors.size())));
        availableList.remove(randomNeighborPoint);
        return randomNeighborPoint;
    }

    public void addWall(Point pointA, Point pointB) {
        if(pointA.x == pointB.x) {
            if(pointA.y > pointB.y) {
                addVerticalWall(pointB.x, pointB.y);
            } else {
                addVerticalWall(pointA.x, pointA.y);
            }
        } else {
            if(pointA.x > pointB.x) {
                addHorizontalWall(pointB.x, pointB.y);
            } else {
                addHorizontalWall(pointA.x, pointA.y);
            }
        }
    }

    /**
     *
     * @param args The command-line arguments
     */
    public static void main(String[] args) {
        DrawMaze myMaze = new DrawMaze(mazeWidth, mazeHeight);

        for (int x = 1; x < mazeWidth; x++) {
            for (int y = 1; y < mazeHeight; y++) {
                myMaze.availableList.add(new Point(x, y));
            }
        }

        // add walls
        for (int x = 0; x < mazeWidth; x++) {
            myMaze.addHorizontalWall(x,0);
        }

        for (int x = 0; x < mazeWidth; x++) {
            myMaze.addHorizontalWall(x,mazeHeight);
        }

        for (int y = 0; y < mazeHeight; y++) {
            myMaze.addVerticalWall(0, y);
        }

        for (int y = 0; y < mazeHeight; y++) {
            myMaze.addVerticalWall(mazeWidth, y);
        }

        // build it
        while(!myMaze.availableList.isEmpty()) {
            // get a random wall
            Point wallPoint = myMaze.getRandomWall();

            // get random neighbor
            Point point = myMaze.getRandomNeighbor(wallPoint);
            if (point != null) {
                // add a wall
                myMaze.addWall(point, wallPoint);
            }
        }

        // open the doors
        Set<Wall> wallSet = myMaze.getWallSet();
        Set<Wall> wallSetCopy = new HashSet<>();

        Iterator<Wall> wallIterator = wallSet.iterator();
        while(wallIterator.hasNext()) {
            Wall wall = wallIterator.next();
            if(wall.x == 0 && wall.y == 0 && wall.horz) {
                continue;
            }

            if(wall.x == mazeWidth - 1 && wall.y == mazeHeight && wall.horz) {
                continue;
            }

            wallSetCopy.add(wall);
        }

        myMaze.walls = wallSetCopy;

        myMaze.display();
    }

    private static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }
    }
}