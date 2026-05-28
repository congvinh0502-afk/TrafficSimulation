package view.renderer;

import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

import java.awt.*;

public class TrafficLightRenderer {

    public void render(
            Graphics2D g2d,
            TrafficLight light,
            int x,
            int y,
            String lightType
    ) {

        Graphics2D g = (Graphics2D) g2d.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // =====================================================
        // BODY
        // =====================================================

        g.setColor(new Color(35, 35, 35));

        g.fillRoundRect(
                x,
                y,
                42,
                110,
                12,
                12
        );

        // viền

        g.setColor(Color.BLACK);

        g.drawRoundRect(
                x,
                y,
                42,
                110,
                12,
                12
        );

        // =====================================================
        // LIGHTS
        // =====================================================

        drawLight(
                g,
                x + 6,
                y + 6,
                Color.RED,
                light.getColor() == LightColor.RED
        );

        drawLight(
                g,
                x + 6,
                y + 40,
                Color.YELLOW,
                light.getColor() == LightColor.YELLOW
        );

        drawLight(
                g,
                x + 6,
                y + 74,
                Color.GREEN,
                light.getColor() == LightColor.GREEN
        );

        // =====================================================
        // POLE
        // =====================================================

        g.setColor(Color.DARK_GRAY);

        g.fillRect(
                x + 18,
                y + 110,
                6,
                35
        );

        // =====================================================
        // TIMER
        // =====================================================

        boolean showTimer = false;

        switch (lightType) {

            case "ALWAYS COUNTDOWN":

                showTimer = true;
                break;

            case "COUNT <= 10":

                showTimer = light.getTimer() / 60 <= 10;
                break;

            case "NO COUNTDOWN":

                showTimer = false;
                break;
        }

        if (showTimer) {

            int seconds = light.getTimer() / 60;

            // nền timer

            g.setColor(new Color(20, 20, 20));

            g.fillRoundRect(
                    x - 2,
                    y - 32,
                    46,
                    24,
                    8,
                    8
            );

            // text

            g.setColor(Color.WHITE);

            g.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            16
                    )
            );

            String text = String.valueOf(seconds);

            FontMetrics fm = g.getFontMetrics();

            int textX =
                    x + (42 - fm.stringWidth(text)) / 2;

            g.drawString(
                    text,
                    textX,
                    y - 14
            );
        }

        g.dispose();
    }

    // =========================================================
    // DRAW SINGLE LIGHT
    // =========================================================

    private void drawLight(
            Graphics2D g,
            int x,
            int y,
            Color color,
            boolean active
    ) {

        // glow

        if (active) {

            g.setColor(
                    new Color(
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue(),
                            80
                    )
            );

            g.fillOval(
                    x - 6,
                    y - 6,
                    42,
                    42
            );
        }

        // light body

        if (active) {

            g.setColor(color);

        } else {

            g.setColor(color.darker().darker());
        }

        g.fillOval(
                x,
                y,
                30,
                30
        );

        // highlight

        g.setColor(
                new Color(255, 255, 255, 120)
        );

        g.fillOval(
                x + 6,
                y + 5,
                10,
                10
        );
    }
}