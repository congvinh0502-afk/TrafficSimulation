package model.traffic;

public class TrafficLight {
    public enum Color { RED, GREEN }
    
    private Color color;
    private int timer;
    private int duration;

    public TrafficLight(int duration) {
        this.duration = duration;
        this.timer = duration;
        this.color = Color.RED; // Bắt đầu bằng màu đỏ
    }

    public void update() {
        timer--;
        if (timer <= 0) {
            color = (color == Color.RED) ? Color.GREEN : Color.RED;
            timer = duration;
        }
    }

    public Color getColor() { return color; }
}