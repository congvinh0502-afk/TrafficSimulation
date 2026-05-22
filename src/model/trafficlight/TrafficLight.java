package model.trafficlight;

public class TrafficLight {

    private LightColor color;

    private int timer;

    public TrafficLight(LightColor color, int timer) {

        this.color = color;
        this.timer = timer;
    }
    

    public void update() {

        timer--;

        if (timer <= 0) {

            switchLight();
        }
    }

    private void switchLight() {

        switch (color) {

            case GREEN:

                color = LightColor.YELLOW;
                timer = 120;
                break;

            case YELLOW:

                color = LightColor.RED;
                timer = 300;
                break;

            case RED:

                color = LightColor.GREEN;
                timer = 300;
                break;
        }
    }

    public LightColor getColor() {
        return color;
    }

    public int getTimer() {
        return timer;
    }
    public void setColor(
        LightColor color
) {

    this.color = color;
}

public void setTimer(
        int timer
) {

    this.timer = timer;
}
}