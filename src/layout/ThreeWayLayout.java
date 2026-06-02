package layout;

import util.Direction;
import util.Lane;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

public class ThreeWayLayout implements IntersectionLayout {

    @Override
    public int getCenterX() { return 400; }

    @Override
    public int getCenterY() { return 400; }

    @Override
    public Rectangle getIntersectionBounds() {
        return new Rectangle(300, 300, 200, 200); 
    }

    @Override
    public Rectangle getTriggerBounds() {
        return new Rectangle(360, 360, 80, 80); 
    }

    @Override
    public Rectangle getRecoverBounds() {
        return new Rectangle(290, 290, 220, 220); 
    }

    @Override
    public int getStopLineForDirection(Direction direction) {
        switch (direction) {
            case NORTH: return 500; // Váạch dưới (chặn xe đi lên)
            case EAST:  return 300; // Vạch trái (chặn xe đi phải)
            case WEST:  return 500; // Vạch phải (chặn xe đi trái)
            default:    return getCenterY();
        }
    }

    @Override
    public int getLaneCenterX(Direction direction, Lane lane) {
        switch (direction) {
            case NORTH:
                return lane == Lane.RIGHT ? 430 : 470;
            default:
                return getCenterX();
        }
    }

    @Override
    public int getLaneCenterY(Direction direction, Lane lane) {
        switch (direction) {
            case EAST:
                return lane == Lane.RIGHT ? 430 : 470;
            case WEST:
                return lane == Lane.RIGHT ? 370 : 330;
            default:
                return getCenterY();
        }
    }

    @Override
    public List<Point> getLightPositions() {
        return Arrays.asList(
            new Point(520, 450),   // Đèn dọc cho nhánh NORTH
            new Point(200, 250)    // Đèn ngang chung cho EAST/WEST
        );
    }

    @Override
    public Rectangle getEnterCheckBounds(Direction direction) {
        return new Rectangle(320, 320, 160, 160);
    }

    @Override
    public java.awt.Point getSpawnPoint(Direction direction) {
        switch (direction) {
            case NORTH: return new java.awt.Point(450, 1000); // Từ dưới lên
            case EAST:  return new java.awt.Point(-100, 470); // Từ trái sang
            case WEST:  return new java.awt.Point(1200, 350); // Từ phải sang
            default:    return new java.awt.Point(getCenterX(), getCenterY());
        }
    }
}