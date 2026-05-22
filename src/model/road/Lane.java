package model.road;

import util.Direction;

public class Lane {

    private int x;
    private int y;

    private int width;
    private int height;

    private Direction direction;

    public Lane(int x, int y, int width, int height, Direction direction) {

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;

        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Direction getDirection() {
        return direction;
    }
}