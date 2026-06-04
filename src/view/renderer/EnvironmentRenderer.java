package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.network.NetworkLayout;

/**
 * Vẽ nền + công trình (không có bãi đỗ xe).
 * Cây không được đặt trên nhánh chéo của 5-way.
 */
public class EnvironmentRenderer {

    private final RoadRenderer     road     = new RoadRenderer();
    private final BuildingRenderer building = new BuildingRenderer();
    private final NatureRenderer   nature   = new NatureRenderer();

    private static final int RH = NetworkLayout.ROAD_HALF;

    public void renderNetwork(GraphicsContext gc, double canvasW, double canvasH) {
        drawGrass(gc, canvasW, canvasH);
        road.drawNetwork(gc);
        road.drawThreeWayDetail(gc);
        road.drawFourWayDetail(gc);
        road.drawFiveWayDetail(gc);
        drawDecoration(gc);
    }

    // ── Lỗi biên đã loại — dùng phương thức public để SimulationScene gọi ──

    public void renderNetworkWorld(GraphicsContext gc) {
        drawGrass(gc, 2000, 2000);  // vẽ đủ lớn cho mọi zoom
        road.drawNetwork(gc);
        drawDecoration(gc);
    }

    private void drawGrass(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.rgb(62, 138, 62));
        gc.fillRect(-800, -700, 2400, 1800); // đủ lớn khi pan/zoom
        // Texture nhỏ
        gc.setFill(Color.rgb(55, 128, 55));
        for (int x = -800; x < 1600; x += 48)
            for (int y = -700; y < 1100; y += 48)
                if ((x + y) % 3 == 0) gc.fillOval(x+12, y+8, 5, 3);
    }

    private void drawDecoration(GraphicsContext gc) {
        // ── Góc trên-trái (phía TÂY của THREE_WAY) ────────────
        building.drawSkyscraper(gc, NetworkLayout.TW_X - 360, -380);
        building.drawLuxuryHouse(gc, NetworkLayout.TW_X - 220, -340);
        nature.drawTreeRow(gc, NetworkLayout.TW_X - 380, -340, 280, true, -1, -1);

        // ── Giữa THREE_WAY và FOUR_WAY (phía BẮC, y < -RH) ───
        int midX = (NetworkLayout.TW_X + NetworkLayout.FW_X) / 2; // -200
        building.drawLuxuryHouse(gc, midX - 80, -280);
        building.drawModernFactory(gc, midX + 40, -360);
        // Cây phía bắc giữa 3W-4W (không trên mặt đường)
        nature.drawTreeRow(gc, NetworkLayout.TW_X + RH + 10, -280, 340, true, -1, -1);

        // ── Giữa FOUR_WAY và FIVE_WAY (phía BẮC) ─────────────
        int mid2X = (NetworkLayout.FW_X + NetworkLayout.VW_X) / 2; // +200
        building.drawLuxuryHouse(gc, mid2X - 60, -300);
        building.drawLuxuryRestaurant(gc, mid2X + 60, -380);
        nature.drawTreeRow(gc, NetworkLayout.FW_X + RH + 10, -300, 340, true, -1, -1);

        // ── Phía NAM (y > +RH) ────────────────────────────────
        nature.drawParkWithPond(gc, NetworkLayout.TW_X - 60, 100, 200, 180);
        building.drawLuxuryHouse(gc, NetworkLayout.FW_X - 180, 160);
        building.drawLuxuryHouse(gc, NetworkLayout.FW_X + 60, 200);
        building.drawSkyscraper(gc, NetworkLayout.VW_X - 180, 120);
        nature.drawTreeRow(gc, NetworkLayout.TW_X - RH - 280, 100, 800, true, -1, -1);

        // Cây dọc bên lề đường (trái + phải mỗi đường dọc)
        for (int ix : new int[]{NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X}) {
            nature.drawTreeRow(gc, ix + RH + 6, -(NetworkLayout.ARM_EXT - 20), NetworkLayout.ARM_EXT - RH - 30, false, ix, -1);
            nature.drawTreeRow(gc, ix - RH - 30, -(NetworkLayout.ARM_EXT - 20), NetworkLayout.ARM_EXT - RH - 30, false, ix, -1);
            // Phía nam tương tự
            nature.drawTreeRow(gc, ix + RH + 6, RH + 10, NetworkLayout.ARM_EXT - RH - 30, false, ix, -1);
            nature.drawTreeRow(gc, ix - RH - 30, RH + 10, NetworkLayout.ARM_EXT - RH - 30, false, ix, -1);
        }

        // ĐÈN ĐƯỜNG
        drawStreetLights(gc);
    }

    private void drawStreetLights(GraphicsContext gc) {
        // Đặt tại các góc giao lộ (ngoài mặt đường)
        for (int ix : new int[]{NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X}) {
            drawLight(gc, ix + RH + 5, -RH - 5);
            drawLight(gc, ix - RH - 12, -RH - 5);
            drawLight(gc, ix + RH + 5,  RH + 5);
            drawLight(gc, ix - RH - 12,  RH + 5);
        }
    }

    private void drawLight(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(255, 255, 150, 0.18));
        gc.fillOval(x - 14, y - 14, 36, 36);
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x, y, 7, 7);
        gc.setFill(Color.rgb(255, 255, 200));
        gc.fillOval(x + 2, y + 2, 3, 3);
    }
}
