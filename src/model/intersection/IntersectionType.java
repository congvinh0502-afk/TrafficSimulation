package model.intersection;

import util.Direction;

import java.util.ArrayList;
import java.util.List;

public enum IntersectionType {

    THREE_WAY,
    FOUR_WAY,
    FIVE_WAY;

    public List<Direction> getDirections() {

        List<Direction> directions =
                new ArrayList<>();

        switch (this) {

            case THREE_WAY:

                directions.add(Direction.NORTH);
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);

                break;

            case FIVE_WAY:
    directions.add(Direction.NORTH);
    directions.add(Direction.SOUTH);
    directions.add(Direction.EAST);
    directions.add(Direction.WEST);
    directions.add(Direction.NORTHEAST); // â† hÆ°á»›ng thá»© 5 thá»±c sá»±
    break;

            default:

                directions.add(Direction.NORTH);
                directions.add(Direction.SOUTH);
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);
        }

        return directions;
    }
}