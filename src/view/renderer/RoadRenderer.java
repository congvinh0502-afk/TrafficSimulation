package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Renderer đường giao thông (JavaFX).
 *
 * <p>Ba loại ngã rẽ:
 * <ul>
 *   <li>{@link #renderThreeWay} — ngã ba (NORTH, EAST, WEST, không có đèn ngang)</li>
 *   <li>{@link #renderFourWay} — ngã tư đầy đủ</li>
 *   <li>{@link #renderFiveWay} — ngã tư + đường chéo 45° + bùng binh</li>
 * </ul>
 * </p>
 */
public class RoadRenderer {

    private static final Color ASPHALT     = Color.rgb(55, 55, 55);
    private static final Color ROAD_EDGE   = Color.rgb(40, 40, 40);
    private static final Color MARKING     = Color.WHITE;
    private static final Color ISLAND_GRAY = Color.rgb(90, 90, 90);
    private static final Color ISLAND_LITE = Color.rgb(160, 160, 160);
    private static final Color ISLAND_GRN  = Color.rgb(35, 130, 55);
    private static final Color RB_EDGE     = Color.rgb(45, 45, 45);

    // ==========================================================
    // Ngã ba
    // ==========================================================

    public void renderThreeWay(GraphicsContext gc) {
        gc.setFill(ASPHALT);
        gc.fillRect(300, 0, 200, 800);   // trục dọc
        gc.fillRect(0, 300, 500, 200);   // nhánh trái (EAST + WEST)
    }

    // ==========================================================
    // Ngã tư
    // ==========================================================

    public void renderFourWay(GraphicsContext gc) {
        // Mặt đường
        gc.setFill(ASPHALT);
        gc.fillRect(300, 0, 200, 800);
        gc.fillRect(0, 300, 1200, 200);

        // Mép vỉa hè
        gc.setFill(ROAD_EDGE);
        gc.fillRect(295, 0, 5, 800);
        gc.fillRect(500, 0, 5, 800);
        gc.fillRect(0, 295, 1200, 5);
        gc.fillRect(0, 500, 1200, 5);

        // Vạch đứt chia làn — dọc
        gc.setFill(MARKING);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500)
                gc.fillRoundRect(395, i, 10, 20, 4, 4);
        }
        // Vạch đứt chia làn — ngang
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500)
                gc.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Vạch dừng đèn đỏ
        gc.fillRect(300, 270, 200, 6);
        gc.fillRect(300, 524, 200, 6);
        gc.fillRect(270, 300, 6, 200);
        gc.fillRect(524, 300, 6, 200);

        // Vạch đi bộ
        drawHorizontalZebra(gc, 300, 280);
        drawHorizontalZebra(gc, 300, 490);
        drawVerticalZebra(gc, 280, 300);
        drawVerticalZebra(gc, 490, 300);

        // Đảo giao thông nhỏ
        gc.setFill(ISLAND_GRAY);
        gc.fillOval(360, 360, 80, 80);
    }

    // ==========================================================
    // Ngã năm
    // ==========================================================

    public void renderFiveWay(GraphicsContext gc) {
        int cx = 400, cy = 400;

        // Bốn trục vuông góc
        gc.setFill(ASPHALT);
        gc.fillRect(300, 0, 200, 800);
        gc.fillRect(0, 300, 1200, 200);

        gc.setFill(ROAD_EDGE);
        gc.fillRect(295, 0, 5, 800);
        gc.fillRect(500, 0, 5, 800);
        gc.fillRect(0, 295, 1200, 5);
        gc.fillRect(0, 500, 1200, 5);

        // Đường chéo 45° phủ trước bùng binh
        drawDiagonalRoad(gc, cx, cy, 200);

        // Bùng binh (phủ lên để che điểm giao)
        drawRoundabout(gc, cx, cy, 340, 160);

        // Vạch đi bộ ngoài bùng binh
        drawHorizontalZebra(gc, 300, 210);
        drawHorizontalZebra(gc, 300, 570);
        drawVerticalZebra(gc, 210, 300);
        drawVerticalZebra(gc, 570, 300);
    }

    // ==========================================================
    // Thành phần con
    // ==========================================================

    private void drawHorizontalZebra(GraphicsContext gc, int startX, int y) {
        gc.setFill(MARKING);
        for (int i = 0; i < 200; i += 20) gc.fillRect(startX + i, y, 10, 20);
    }

    private void drawVerticalZebra(GraphicsContext gc, int x, int startY) {
        gc.setFill(MARKING);
        for (int i = 0; i < 200; i += 20) gc.fillRect(x, startY + i, 20, 10);
    }

    public void drawDiagonalRoad(GraphicsContext gc, int cx, int cy, int roadWidth) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(-45);

        gc.setFill(ROAD_EDGE);
        gc.fillRect(-(roadWidth / 2 + 5), -900, roadWidth + 10, 900);

        gc.setFill(ASPHALT);
        gc.fillRect(-roadWidth / 2, -900, roadWidth, 900);

        // Vạch đứt phân làn chéo
        gc.setFill(MARKING);
        for (int i = -900; i < -180; i += 40)
            gc.fillRoundRect(-5, i, 10, 20, 4, 4);

        // Vạch đi bộ chéo
        for (int i = -roadWidth / 2; i < roadWidth / 2; i += 20)
            gc.fillRect(i, -230, 10, 20);

        gc.restore();
    }

    public void drawRoundabout(GraphicsContext gc, int cx, int cy, int outerDia, int islandDia) {
        // Mặt đường bùng binh
        gc.setFill(ASPHALT);
        gc.fillOval(cx - outerDia / 2.0, cy - outerDia / 2.0, outerDia, outerDia);

        // Viền bùng binh
        gc.setStroke(RB_EDGE);
        gc.setLineWidth(3);
        gc.strokeOval(cx - outerDia / 2.0, cy - outerDia / 2.0, outerDia, outerDia);

        // Vạch vòng tròn phân làn (dashed)
        gc.setStroke(MARKING);
        gc.setLineWidth(2.5);
        gc.setLineDashes(14, 12);
        int laneR = outerDia - 60;
        gc.strokeOval(cx - laneR / 2.0, cy - laneR / 2.0, laneR, laneR);
        gc.setLineDashes(); // reset dashes

        // Đảo đá lề
        gc.setFill(ISLAND_LITE);
        gc.fillOval(cx - islandDia / 2.0, cy - islandDia / 2.0, islandDia, islandDia);

        // Lõi cỏ
        gc.setFill(ISLAND_GRN);
        int green = islandDia - 14;
        gc.fillOval(cx - green / 2.0, cy - green / 2.0, green, green);

        gc.setLineWidth(1);
    }
}
