package util;

public class DirectionHelper {

    public static Direction getLeftDirection(
            Direction direction
    ) {

        switch (direction) {

            case NORTH:
                return Direction.WEST;

            case SOUTH:
                return Direction.EAST;

            case EAST:
                return Direction.NORTH;

            case WEST:
                return Direction.SOUTH;
        }

        return direction;
    }

    public static Direction getRightDirection(
            Direction direction
    ) {

        switch (direction) {

            case NORTH:
                return Direction.EAST;

            case SOUTH:
                return Direction.WEST;

            case EAST:
                return Direction.SOUTH;

            case WEST:
                return Direction.NORTH;
        }

        return direction;
    }
}