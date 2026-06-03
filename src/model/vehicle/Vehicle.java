package model.vehicle;

import config.Constants;
import math.Vector2D;
import strategy.driver.DriverBehavior;
import util.Direction;
import util.Lane;
import util.TurnType;

/**
 * Lớp cơ sở cho tất cả phương tiện.
 *
 * <p>Thay đổi so với phiên bản cũ:
 * <ul>
 *   <li>{@link #acceleration} — gia tốc px/frame².
 *       {@code speed = clamp(speed + acceleration, 0, maxSpeed)} mỗi frame.</li>
 *   <li>{@link #directionVector} — vectơ hướng toán học;
 *       {@code move()} tính toán dựa trên vectơ này.</li>
 *   <li>{@link #postTurnAligning} — sau khi rẽ xong, xe alignment dần
 *       thay vì snap tức thì (fix teleport).</li>
 * </ul>
 * </p>
 */
public abstract class Vehicle {

    // --------------------------------------------------------
    // Vị trí và kích thước
    // --------------------------------------------------------
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // --------------------------------------------------------
    // Chuyển động — vectơ + vật lý
    // --------------------------------------------------------
    protected double    speed;
    protected double    maxSpeed;
    protected double    acceleration;
    protected Direction direction;
    protected Vector2D  directionVector;
    protected boolean   stopped;

    // --------------------------------------------------------
    // Góc hiển thị
    // --------------------------------------------------------
    protected double angle;
    protected double targetAngle;

    // --------------------------------------------------------
    // Rẽ
    // --------------------------------------------------------
    protected boolean   turning;
    protected boolean   turned;
    protected Direction targetDirection;
    protected TurnType  turnType;

    // --------------------------------------------------------
    // Alignment sau rẽ (fix teleport)
    // --------------------------------------------------------
    protected boolean postTurnAligning;

    // --------------------------------------------------------
    // Làn đường
    // --------------------------------------------------------
    protected Lane    lane;
    protected boolean changingLane;
    protected Lane    targetLane;
    private   int     laneChangeCooldown;

    // --------------------------------------------------------
    // Vị trí đích (dùng khi alignment)
    // --------------------------------------------------------
    protected double targetX;
    protected double targetY;

    // --------------------------------------------------------
    // Chiến lược lái
    // --------------------------------------------------------
    protected DriverBehavior behavior;

    // ==========================================================
    // Constructor
    // ==========================================================

    protected Vehicle(double x, double y, Direction direction) {
        this.x               = x;
        this.y               = y;
        this.direction       = direction;
        this.directionVector = direction.toVector();
        this.lane            = Lane.RIGHT;
        this.angle           = direction.toAngleDeg();
        this.acceleration    = Constants.DEFAULT_ACCELERATION;
    }

    // ==========================================================
    // Di chuyển
    // ==========================================================

    /**
     * Di chuyển xe một bước.
     *
     * <p>Luồng:
     * <ol>
     *   <li>Tính tốc độ mới = clamp(speed + acceleration, 0, maxSpeed).</li>
     *   <li>Dịch chuyển theo {@link #directionVector}.</li>
     * </ol>
     * </p>
     */
    public void move() {
        if (stopped) return;

        // Áp dụng gia tốc
        speed = Math.max(0, Math.min(maxSpeed, speed + acceleration));

        // Di chuyển theo vectơ hướng
        x += directionVector.x * speed;
        y += directionVector.y * speed;
    }

    // ==========================================================
    // Getter / Setter — vị trí
    // ==========================================================

    public double getX()      { return x; }
    public double getY()      { return y; }
    public double getWidth()  { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // ==========================================================
    // Getter / Setter — chuyển động
    // ==========================================================

    public double getSpeed()  { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getMaxSpeed()  { return maxSpeed; }
    public void setMaxSpeed(double s) { this.maxSpeed = s; }

    public double getAcceleration() { return acceleration; }
    public void setAcceleration(double a) { this.acceleration = a; }

    public Direction getDirection()  { return direction; }
    public void setDirection(Direction d) {
        this.direction       = d;
        this.directionVector = d.toVector();
    }

    public Vector2D getDirectionVector() { return directionVector; }

    public boolean isStopped()           { return stopped; }
    public void setStopped(boolean s)    { this.stopped = s; }

    // ==========================================================
    // Getter / Setter — góc
    // ==========================================================

    public double getAngle()           { return angle; }
    public void setAngle(double a)     { this.angle = a; }
    public double getTargetAngle()     { return targetAngle; }
    public void setTargetAngle(double a){ this.targetAngle = a; }

    // ==========================================================
    // Getter / Setter — rẽ
    // ==========================================================

    public boolean   isTurning()          { return turning; }
    public void      setTurning(boolean b){ this.turning = b; }
    public boolean   hasTurned()          { return turned; }
    public void      setTurned(boolean b) { this.turned = b; }
    public Direction getTargetDirection() { return targetDirection; }
    public void      setTargetDirection(Direction d){ this.targetDirection = d; }
    public TurnType  getTurnType()        { return turnType; }
    public void      setTurnType(TurnType t){ this.turnType = t; }

    // ==========================================================
    // Getter / Setter — alignment sau rẽ
    // ==========================================================

    public boolean isPostTurnAligning()          { return postTurnAligning; }
    public void    setPostTurnAligning(boolean b) { this.postTurnAligning = b; }

    // ==========================================================
    // Getter / Setter — làn đường
    // ==========================================================

    public Lane    getLane()                       { return lane; }
    public void    setLane(Lane l)                 { this.lane = l; }
    public boolean isChangingLane()                { return changingLane; }
    public void    setChangingLane(boolean b)      { this.changingLane = b; }
    public Lane    getTargetLane()                 { return targetLane; }
    public void    setTargetLane(Lane l)           { this.targetLane = l; }
    public int     getLaneChangeCooldown()         { return laneChangeCooldown; }
    public void    setLaneChangeCooldown(int c)    { this.laneChangeCooldown = c; }

    // ==========================================================
    // Getter / Setter — vị trí đích
    // ==========================================================

    public double getTargetX()     { return targetX; }
    public void   setTargetX(double tx){ this.targetX = tx; }
    public double getTargetY()     { return targetY; }
    public void   setTargetY(double ty){ this.targetY = ty; }

    // ==========================================================
    // Getter / Setter — chiến lược lái
    // ==========================================================

    public DriverBehavior getBehavior()            { return behavior; }
    public void           setBehavior(DriverBehavior b){ this.behavior = b; }
}
