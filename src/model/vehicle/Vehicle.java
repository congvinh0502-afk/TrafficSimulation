
package model.vehicle;

import util.Direction;
import strategy.driver.DriverBehavior;
import util.TurnType;
import util.Lane;
public abstract class Vehicle {

    protected double x;
    protected double y;

    protected double speed;

    protected double width;
    protected double height;

    protected Direction direction;
    
    protected boolean stopped;
    
    protected DriverBehavior behavior;
    
    protected TurnType turnType;
    
    protected boolean turned;
    
    protected Lane lane;
    protected boolean turning;
    protected Direction targetDirection;
    protected double targetX;
    protected double targetY;
    protected double angle;
    protected double targetAngle;
    protected boolean changingLane;
    protected Lane targetLane;
    private int laneChangeCooldown = 0;

    // Offset lách vượt xe trong cùng lane (CollisionSystem set, LaneAlignmentSystem đọc)
    // Tách riêng khỏi targetX/Y để không xung đột với TurningSystem
    private double overtakeOffsetX = 0;
    private double overtakeOffsetY = 0;
    public Vehicle(double x, double y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.lane = Lane.RIGHT;
        
        switch (direction) {
            case EAST:      angle = 0;   break;
            case SOUTH:     angle = 90;  break;
            case WEST:      angle = 180; break;
            case NORTH:     angle = -90; break;
            case NORTHEAST: angle = -18; break; // FIX: Äá»“ng bá»™ gÃ³c 342 Ä‘á»™ vá»›i Render
        }
    }

    public abstract void move();

    // GETTER

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
    public Direction getDirection() {
        return direction;
    }
    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public DriverBehavior getBehavior() {
    return behavior;
    }

    public void setBehavior(
        DriverBehavior behavior
    ) {
            this.behavior = behavior;
      }
    public TurnType getTurnType() {
    return turnType;
    }

    public void setTurnType(
        TurnType turnType
) {
    this.turnType = turnType;
    }
    public void setDirection(
        Direction direction
) {

    this.direction = direction;
}
    public boolean hasTurned() {
    return turned;
}

public void setTurned(boolean turned) {
    this.turned = turned;
}
public Lane getLane() {
    return lane;
}

public void setLane(Lane lane) {
    this.lane = lane;
}
public void setX(double x) {
    this.x = x;
}

public void setY(double y) {
    this.y = y;
}
public boolean isTurning() {
    return turning;
}

public void setTurning(boolean turning) {
    this.turning = turning;
}
public Direction getTargetDirection() {
    return targetDirection;
}

public void setTargetDirection(
        Direction targetDirection
) {
    this.targetDirection = targetDirection;
}
public double getTargetX() {
    return targetX;
}

public void setTargetX(double targetX) {
    this.targetX = targetX;
}

public double getTargetY() {
    return targetY;
}

public void setTargetY(double targetY) {
    this.targetY = targetY;
}
public double getAngle() {
    return angle;
}

public void setAngle(double angle) {
    this.angle = angle;
}

public double getTargetAngle() {
    return targetAngle;
}

public void setTargetAngle(double targetAngle) {
    this.targetAngle = targetAngle;
}
public boolean isChangingLane() {
    return changingLane;
}

public void setChangingLane(
        boolean changingLane
) {
    this.changingLane = changingLane;
}

public Lane getTargetLane() {
    return targetLane;
}

public void setTargetLane(
        Lane targetLane
) {
    this.targetLane = targetLane;
}
public int getLaneChangeCooldown() {
    return laneChangeCooldown;
}

public void setLaneChangeCooldown(
        int laneChangeCooldown
) {
    this.laneChangeCooldown = laneChangeCooldown;
}
public void setSpeed(double speed) {
    this.speed = speed;
}

public double getSpeed() {
    return speed;
}

public double getOvertakeOffsetX() { return overtakeOffsetX; }
public void   setOvertakeOffsetX(double v) { this.overtakeOffsetX = v; }

public double getOvertakeOffsetY() { return overtakeOffsetY; }
public void   setOvertakeOffsetY(double v) { this.overtakeOffsetY = v; }

}