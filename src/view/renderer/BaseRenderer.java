package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renderer nền (JavaFX) — cỏ và đèn đường.
 */
public class BaseRenderer {

    private static final Color GRASS_BASE   = Color.rgb(65, 142, 65);
    private static final Color GRASS_DETAIL = Color.rgb(58, 132, 58);
    private static final Color LIGHT_GLOW   = Color.rgb(255, 255, 150, 0.18);
    private static final Color LIGHT_POLE   = Color.DARKGRAY;
    private static final Color LIGHT_BULB   = Color.WHITE;

    private static final int[][] LIGHT_POS = {
        {450, 250}, {750, 250}, {450, 550}, {750, 550}
    };

    public void drawGrass(GraphicsContext gc) {
        gc.setFill(GRASS_BASE);
        gc.fillRect(0, 0, 1200, 800);

        gc.setFill(GRASS_DETAIL);
        for (int i = 0; i < 1200; i += 50) {
            for (int j = 0; j < 800; j += 50) {
                if ((i + j) % 3 == 0) gc.fillOval(i + 15, j + 10, 6, 4);
            }
        }
    }

    public void drawStreetLights(GraphicsContext gc) {
        for (int[] p : LIGHT_POS) {
            gc.setFill(LIGHT_GLOW);
            gc.fillOval(p[0] - 20, p[1] - 20, 50, 50);
            gc.setFill(LIGHT_POLE);
            gc.fillOval(p[0] - 4, p[1] - 4, 8, 8);
            gc.setFill(LIGHT_BULB);
            gc.fillOval(p[0] - 2, p[1] - 2, 4, 4);
        }
    }
}
