package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

/**
 * Renderer đèn giao thông (JavaFX).
 *
 * <p>Hiển thị hộp đèn 3 bóng (đỏ-vàng-xanh), cột và đếm ngược
 * theo cài đặt lightType.</p>
 */
public class TrafficLightRenderer {

    private static final int BOX_W  = 42;
    private static final int BOX_H  = 110;
    private static final int BOX_ARC = 12;
    private static final int BULB_SIZE = 30;
    private static final int BULB_OFF  = 6;

    public void render(GraphicsContext gc, TrafficLight light, int x, int y, String lightType) {
        drawBox(gc, x, y);
        drawBulb(gc, x + BULB_OFF, y + BULB_OFF,      Color.RED,    light.getColor() == LightColor.RED);
        drawBulb(gc, x + BULB_OFF, y + BULB_OFF + 34,  Color.YELLOW, light.getColor() == LightColor.YELLOW);
        drawBulb(gc, x + BULB_OFF, y + BULB_OFF + 68,  Color.LIME,   light.getColor() == LightColor.GREEN);
        drawPole(gc, x, y);

        if (shouldShowTimer(light, lightType)) {
            drawTimer(gc, x, y, light.getTimer() / 60);
        }
    }

    private void drawBox(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(35, 35, 35));
        gc.fillRoundRect(x, y, BOX_W, BOX_H, BOX_ARC, BOX_ARC);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x, y, BOX_W, BOX_H, BOX_ARC, BOX_ARC);
    }

    private void drawPole(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + 18, y + BOX_H, 6, 35);
    }

    private void drawBulb(GraphicsContext gc, int x, int y, Color color, boolean active) {
        if (active) {
            gc.setFill(color.deriveColor(0, 1, 1, 0.3));
            gc.fillOval(x - 6, y - 6, BULB_SIZE + 12, BULB_SIZE + 12);
        }
        gc.setFill(active ? color : color.darker().darker());
        gc.fillOval(x, y, BULB_SIZE, BULB_SIZE);

        gc.setFill(Color.rgb(255, 255, 255, 0.47));
        gc.fillOval(x + 6, y + 5, 10, 10);
    }

    private void drawTimer(GraphicsContext gc, int x, int y, int seconds) {
        gc.setFill(Color.rgb(20, 20, 20));
        gc.fillRoundRect(x - 2, y - 32, 46, 24, 8, 8);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(seconds), x + BOX_W / 2.0, y - 14);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private boolean shouldShowTimer(TrafficLight light, String lightType) {
        switch (lightType) {
            case "ALWAYS COUNTDOWN": return true;
            case "COUNT <= 10":      return light.getTimer() / 60 <= 10;
            default:                 return false;
        }
    }
}
