package model.vehicle;

import javafx.scene.paint.Color;

public abstract class Vehicle {
    protected double x, y;
    protected double width, height;
    protected double speed;
    protected double angle; // Hướng đầu xe (Tính bằng độ - degrees)
    protected Color color;

    public Vehicle(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = 3.0; // Tốc độ chạy test ban đầu
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getAngle() { return angle; }
    public Color getColor() { return color; }
    public double getSpeed() { return speed; }

    public void setSpeed(double speed) { 
        this.speed = speed; 
    }
    
    public void setAngle(double angle) { 
        this.angle = angle; 
    }
}