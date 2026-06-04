package model.vehicle;

import config.Constants;
import math.Vector2D;
import model.network.NetworkLayout;
import strategy.driver.DriverBehavior;
import util.Direction;
import util.Lane;
import util.TurnType;

/**
 * Lớp cơ sở phương tiện — hỗ trợ vectơ hướng, gia tốc, homeIntersectionX.
 */
public abstract class Vehicle {

    // Vị trí và kích thước
    protected double x, y, width, height;

    // Vật lý chuyển động
    protected double    speed;
    protected double    maxSpeed;
    protected double    acceleration;
    protected Direction direction;
    protected Vector2D  directionVector;
    protected boolean   stopped;

    // Góc hiển thị
    protected double angle;
    protected double targetAngle;

    // Rẽ
    protected boolean   turning;
    protected boolean   turned;
    protected Direction targetDirection;
    protected TurnType  turnType;
    protected boolean   postTurnAligning;

    // Làn đường
    protected Lane    lane;
    protected boolean changingLane;
    protected Lane    targetLane;
    private   int     laneChangeCooldown;

    // Vị trí đích (alignment)
    protected double targetX, targetY;

    // Giao lộ gần nhất mà xe N/S-bound này thuộc về (world X của giao lộ)
    protected int homeIntersectionX;

    // Chiến lược lái
    protected DriverBehavior behavior;

    protected Vehicle(double x, double y, Direction direction) {
        this.x               = x;
        this.y               = y;
        this.direction       = direction;
        this.directionVector = direction.toVector();
        this.lane            = Lane.RIGHT;
        this.angle           = direction.toAngleDeg();
        this.acceleration    = Constants.DEFAULT_ACCELERATION;
        // homeIntersectionX sẽ được set bởi VehicleSpawnManager
        this.homeIntersectionX = NetworkLayout.FW_X; // mặc định
    }

    /** Di chuyển theo vectơ hướng với tốc độ hiện tại. */
    public void move() {
        if (stopped) return;
        speed = Math.max(0, Math.min(maxSpeed, speed + acceleration));
        x += directionVector.x * speed;
        y += directionVector.y * speed;
    }

    // --- Getter / Setter ---
    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getWidth()  { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public double getSpeed()  { return speed; }
    public void setSpeed(double s) { this.speed = s; }
    public double getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(double s) { this.maxSpeed = s; }
    public double getAcceleration() { return acceleration; }
    public void setAcceleration(double a) { this.acceleration = a; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction d) {
        this.direction = d;
        this.directionVector = d.toVector();
    }
    public Vector2D getDirectionVector() { return directionVector; }

    public boolean isStopped()        { return stopped; }
    public void setStopped(boolean b) { this.stopped = b; }

    public double getAngle()        { return angle; }
    public void setAngle(double a)  { this.angle = a; }
    public double getTargetAngle()  { return targetAngle; }
    public void setTargetAngle(double a) { this.targetAngle = a; }

    public boolean isTurning()        { return turning; }
    public void setTurning(boolean b) { this.turning = b; }
    public boolean hasTurned()        { return turned; }
    public void setTurned(boolean b)  { this.turned = b; }
    public Direction getTargetDirection() { return targetDirection; }
    public void setTargetDirection(Direction d) { this.targetDirection = d; }
    public TurnType getTurnType()     { return turnType; }
    public void setTurnType(TurnType t) { this.turnType = t; }

    public boolean isPostTurnAligning()        { return postTurnAligning; }
    public void setPostTurnAligning(boolean b) { this.postTurnAligning = b; }

    public Lane getLane()               { return lane; }
    public void setLane(Lane l)         { this.lane = l; }
    public boolean isChangingLane()     { return changingLane; }
    public void setChangingLane(boolean b) { this.changingLane = b; }
    public Lane getTargetLane()         { return targetLane; }
    public void setTargetLane(Lane l)   { this.targetLane = l; }
    public int getLaneChangeCooldown()  { return laneChangeCooldown; }
    public void setLaneChangeCooldown(int c) { this.laneChangeCooldown = c; }

    public double getTargetX()     { return targetX; }
    public void setTargetX(double v) { this.targetX = v; }
    public double getTargetY()     { return targetY; }
    public void setTargetY(double v) { this.targetY = v; }

    public int getHomeIntersectionX()       { return homeIntersectionX; }
    public void setHomeIntersectionX(int v) { this.homeIntersectionX = v; }

    public DriverBehavior getBehavior()           { return behavior; }
    public void setBehavior(DriverBehavior b)     { this.behavior = b; }
}
