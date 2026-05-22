package view.renderer;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TrafficLightRenderer {

    public void render(
            Graphics2D g2d,
            TrafficLight light,
            int x,
            int y
    ) {

        // cột đèn

        g2d.setColor(Color.BLACK);

        g2d.fillRect(x, y, 40, 100);

        // RED

        if (light.getColor() == LightColor.RED) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        g2d.fillOval(x + 5, y + 5, 30, 30);

        // YELLOW

        if (light.getColor() == LightColor.YELLOW) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        g2d.fillOval(x + 5, y + 35, 30, 30);

        // GREEN

        if (light.getColor() == LightColor.GREEN) {
            g2d.setColor(Color.GREEN);
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        g2d.fillOval(x + 5, y + 65, 30, 30);

        // TIMER

        g2d.setColor(Color.WHITE);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));

        g2d.drawString(
                String.valueOf(light.getTimer() / 60),
                x,
                y - 10
        );
    }
}