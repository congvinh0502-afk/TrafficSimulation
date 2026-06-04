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
        // Trục dọc — chỉ từ trên xuống tâm giao lộ (không có nhánh xuống)
        gc.setFill(ASPHALT);
        gc.fillRect(500, 0, 200, 500); // dừng ở y=500 (đáy giao lộ)

        // Đường ngang — trải full width như 4-way
        gc.fillRect(0, 300, 1200, 200);

        // Mép vỉa hè
        gc.setFill(ROAD_EDGE);
        gc.fillRect(495, 0, 5, 500); // mép trái trục dọc, dừng ở y=500
        gc.fillRect(700, 0, 5, 500); // mép phải trục dọc, dừng ở y=500
        gc.fillRect(0, 295, 1200, 5); // mép trên đường ngang
        gc.fillRect(0, 500, 1200, 5); // mép dưới đường ngang

        // Vạch đứt chia làn — dọc (chỉ phần trên giao lộ)
        gc.setFill(MARKING);
        for (int i = 0; i < 280; i += 40) {
            gc.fillRoundRect(595, i, 10, 20, 4, 4);
        }
        // Vạch đứt chia làn — ngang (tránh vùng giao lộ)
        for (int i = 0; i < 1200; i += 40) {
            if (i < 480 || i > 700)
                gc.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Vạch dừng đèn đỏ
        gc.fillRect(500, 270, 200, 6); // trên giao lộ (xe NORTH dừng đây)
        gc.fillRect(470, 300, 6, 200); // mép trái (xe EAST dừng đây)
        gc.fillRect(724, 300, 6, 200); // mép phải (xe WEST dừng đây)

        // Vạch đi bộ
        drawHorizontalZebra(gc, 500, 280); // trước vạch dừng NORTH
        drawVerticalZebra(gc, 480, 300);
        drawVerticalZebra(gc, 690, 300);

        // Bịt đầu dưới trục dọc (vỉa hè, không có đường)
        gc.setFill(Color.rgb(65, 142, 65)); // màu cỏ
        gc.fillRect(500, 500, 200, 300);
        // Viền vỉa hè bịt đầu
        gc.setFill(Color.rgb(180, 180, 180));
        gc.fillRect(495, 498, 210, 5);
    }

    // ==========================================================
    // Ngã tư
    // ==========================================================

    public void renderFourWay(GraphicsContext gc) {
        // Mặt đường
        gc.setFill(ASPHALT);
        gc.fillRect(500, 0, 200, 800);
        gc.fillRect(0, 300, 1200, 200);

        // Mép vỉa hè
        gc.setFill(ROAD_EDGE);
        gc.fillRect(495, 0, 5, 800);
        gc.fillRect(700, 0, 5, 800);
        gc.fillRect(0, 295, 1200, 5);
        gc.fillRect(0, 500, 1200, 5);

        // Vạch đứt chia làn — dọc
        gc.setFill(MARKING);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500)
                gc.fillRoundRect(595, i, 10, 20, 4, 4);
        }
        // Vạch đứt chia làn — ngang
        for (int i = 0; i < 1200; i += 40) {
            if (i < 480 || i > 700)
                gc.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Vạch dừng đèn đỏ
        gc.fillRect(500, 270, 200, 6);
        gc.fillRect(500, 524, 200, 6);
        gc.fillRect(470, 300, 6, 200);
        gc.fillRect(724, 300, 6, 200);

        // Vạch đi bộ
        drawHorizontalZebra(gc, 500, 280);
        drawHorizontalZebra(gc, 500, 490);
        drawVerticalZebra(gc, 480, 300);
        drawVerticalZebra(gc, 690, 300);

        // Đảo giao thông nhỏ
        gc.setFill(ISLAND_GRAY);
        gc.fillOval(560, 360, 80, 80);
    }

    // ==========================================================
    // Ngã năm
    // ==========================================================

    public void renderFiveWay(GraphicsContext gc) {
        int cx = 600;
        int cy = 400;
        int roadHalfW = 100; // nửa chiều rộng mỗi nhánh (2 làn = 100px)
        int edgePad = 6; // độ rộng mép đường
        int roundaboutOuter = 170; // bán kính ngoài bùng binh
        int roundaboutIsland = 80; // bán kính đảo giữa

        // 5 góc nhánh (độ, 0°=phải, tăng CW)
        // 270°=NORTH, 342°=NE, 54°=SE, 126°=SW, 198°=NW
        double[] angles = { 270, 342, 54, 126, 198 };

        // --- Bước 1: vẽ mép đường (tối hơn) cho tất cả nhánh ---
        gc.setFill(ROAD_EDGE);
        for (double a : angles) {
            drawFiveWayArm(gc, cx, cy, a, roadHalfW + edgePad, 800);
        }

        // --- Bước 2: vẽ mặt nhựa cho tất cả nhánh ---
        gc.setFill(ASPHALT);
        for (double a : angles) {
            drawFiveWayArm(gc, cx, cy, a, roadHalfW, 800);
        }

        // --- Bước 3: bùng binh phủ lên che điểm giao trung tâm ---
        drawRoundabout(gc, cx, cy, roundaboutOuter * 2, roundaboutIsland * 2);

        // --- Bước 4: vạch đứt phân làn giữa mỗi nhánh ---
        gc.setFill(MARKING);
        for (double a : angles) {
            drawFiveWayArmDivider(gc, cx, cy, a, roundaboutOuter + 10, 800);
        }

        // --- Bước 5: vạch dừng + zebra tại miệng mỗi nhánh ---
        for (double a : angles) {
            drawFiveWayArmStopLine(gc, cx, cy, a, roundaboutOuter + 2, roadHalfW);
            drawFiveWayArmZebra(gc, cx, cy, a, roundaboutOuter + 12, roadHalfW);
        }
    }

    /**
     * Vẽ thân nhánh đường: hình chữ nhật xoay theo góc.
     * Gốc tại tâm bùng binh, kéo dài ra ngoài length px.
     */
    private void drawFiveWayArm(GraphicsContext gc, int cx, int cy,
            double angleDeg, int halfWidth, int length) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg); // 0°=phải, tăng CW
        // Sau rotate: trục x chỉ ra ngoài theo hướng nhánh
        // fillRect vẽ từ (0, -halfWidth) kéo dài length theo trục x
        gc.fillRect(0, -halfWidth, length, halfWidth * 2);
        gc.restore();
    }

    /**
     * Vẽ vạch đứt phân làn giữa nhánh (nét đứt theo trục nhánh).
     */
    private void drawFiveWayArmDivider(GraphicsContext gc, int cx, int cy,
            double angleDeg, double startDist, double endDist) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg);
        double d = startDist;
        while (d < endDist) {
            // vạch đứt nằm dọc trục nhánh, tại y=0 (giữa 2 làn)
            gc.fillRoundRect(d, -5, 22, 10, 4, 4);
            d += 40;
        }
        gc.restore();
    }

    /**
     * Vẽ vạch dừng đèn đỏ ngang miệng nhánh.
     */
    private void drawFiveWayArmStopLine(GraphicsContext gc, int cx, int cy,
            double angleDeg, double dist, int halfWidth) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg);
        gc.setFill(MARKING);
        gc.fillRect(dist, -halfWidth, 6, halfWidth * 2);
        gc.restore();
    }

    /**
     * Vẽ vạch đi bộ (zebra) vuông góc với nhánh, ngay ngoài bùng binh.
     */
    private void drawFiveWayArmZebra(GraphicsContext gc, int cx, int cy,
            double angleDeg, double dist, int halfWidth) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg);
        gc.setFill(MARKING);
        // Vạch zebra: các thanh ngang (vuông góc với trục nhánh)
        for (int offset = -halfWidth; offset < halfWidth; offset += 18) {
            gc.fillRect(dist, offset, 20, 9);
        }
        gc.restore();
    }

    /**
     * Vẽ một nhánh đường (hình chữ nhật xoay) từ tâm ra ngoài.
     * 
     * @param cx,      cy tâm bùng binh
     * @param angleDeg góc hướng nhánh (0°=phải, tăng CW)
     * @param width    chiều rộng nhánh (px)
     * @param length   chiều dài nhánh (px, đủ ra mép màn hình)
     */
    private void drawArm(GraphicsContext gc, int cx, int cy,
            double angleDeg, int width, int length) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg + 90); // +90 vì fillRect vẽ xuống dưới
        gc.fillRect(-width / 2.0, 0, width, length);
        gc.restore();
    }

    /**
     * Vẽ vạch đứt giữa làn cho một nhánh.
     */
    private void drawArmCenterLine(GraphicsContext gc, int cx, int cy,
            double angleDeg, double startDist, double endDist) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg + 90);
        double d = startDist;
        while (d < endDist) {
            gc.fillRoundRect(-5, d, 10, 20, 4, 4);
            d += 40;
        }
        gc.restore();
    }

    /**
     * Vẽ vạch đi bộ (zebra) vuông góc với nhánh, tại khoảng cách dist từ tâm.
     */
    private void drawArmZebra(GraphicsContext gc, int cx, int cy,
            double angleDeg, double dist, int roadWidth) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg + 90);
        gc.setFill(MARKING);
        for (int i = -roadWidth / 2; i < roadWidth / 2; i += 20) {
            gc.fillRect(i, dist, 10, 18);
        }
        gc.restore();
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
