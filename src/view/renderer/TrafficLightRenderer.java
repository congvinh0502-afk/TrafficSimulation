package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.network.NetworkLayout;
import model.trafficlight.LightColor;
import model.trafficlight.TrafficLight;

/**
 * Renderer đèn giao thông — đặt PHẢI làn xe, NGOÀI mặt đường.
 * Tọa độ tính từ NetworkLayout.northLightPos / southLightPos / ...
 */
public class TrafficLightRenderer {

    private static final int BW = NetworkLayout.LIGHT_BOX_W;  // 22
    private static final int BH = NetworkLayout.LIGHT_BOX_H;  // 58
    private static final int BR = 6;  // border radius

    /** Vẽ đèn dọc (N-bound + S-bound) tại giao lộ (ix, iy). */
    public void renderVertical(GraphicsContext gc, TrafficLight light, int ix, int iy, String timerMode) {
        double[] np = NetworkLayout.northLightPos(ix, iy);
        double[] sp = NetworkLayout.southLightPos(ix, iy);
        drawBox(gc, np[0], np[1], light);
        drawBox(gc, sp[0], sp[1], light);
        if (showTimer(light, timerMode)) {
            drawTimer(gc, np[0] + BW/2.0, np[1] - 16, light.getTimer()/60);
        }
    }

    /** Vẽ đèn ngang (E-bound + W-bound) tại giao lộ (ix, iy). */
    public void renderHorizontal(GraphicsContext gc, TrafficLight light, int ix, int iy, String timerMode) {
        double[] ep = NetworkLayout.eastLightPos(ix, iy);
        double[] wp = NetworkLayout.westLightPos(ix, iy);
        drawBox(gc, ep[0], ep[1], light);
        drawBox(gc, wp[0], wp[1], light);
    }

    // ── Hộp đèn ────────────────────────────────────────────────
    private void drawBox(GraphicsContext gc, double x, double y, TrafficLight light) {
        // Thân hộp
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRoundRect(x, y, BW, BH, BR, BR);
        gc.setStroke(Color.BLACK); gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, BW, BH, BR, BR);

        // Ba bóng đèn
        int bs = BW - 6;  // bulb size
        double bx = x + 3;
        double by0 = y + 4;
        double by1 = y + 4 + bs + 3;
        double by2 = y + 4 + (bs + 3) * 2;
        drawBulb(gc, bx, by0, Color.RED,    light.getColor() == LightColor.RED,    bs);
        drawBulb(gc, bx, by1, Color.YELLOW, light.getColor() == LightColor.YELLOW, bs);
        drawBulb(gc, bx, by2, Color.LIME,   light.getColor() == LightColor.GREEN,  bs);

        // Cột đèn
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + BW/2.0 - 2, y + BH, 4, 20);
    }

    private void drawBulb(GraphicsContext gc, double x, double y, Color c, boolean on, int sz) {
        if (on) {
            gc.setFill(c.deriveColor(0, 1, 1, 0.25));
            gc.fillOval(x - 4, y - 4, sz + 8, sz + 8);
        }
        gc.setFill(on ? c : c.darker().darker());
        gc.fillOval(x, y, sz, sz);
        gc.setFill(Color.rgb(255, 255, 255, 0.4));
        gc.fillOval(x + sz/4.0, y + sz/5.0, sz/3.0, sz/3.0);
    }

    private void drawTimer(GraphicsContext gc, double cx, double ty, int sec) {
        gc.setFill(Color.rgb(20, 20, 20));
        gc.fillRoundRect(cx - 14, ty - 2, 28, 16, 5, 5);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(sec), cx, ty + 12);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private boolean showTimer(TrafficLight light, String mode) {
        if ("ALWAYS COUNTDOWN".equals(mode)) return true;
        if ("COUNT <= 10".equals(mode))      return light.getTimer()/60 <= 10;
        return false;
    }
}
