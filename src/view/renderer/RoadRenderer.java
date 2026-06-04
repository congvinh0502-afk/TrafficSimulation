package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.network.NetworkLayout;

/** Renderer mạng lưới đường — tọa độ từ NetworkLayout, khớp logic xe. */
public class RoadRenderer {

    private static final Color ASPHALT = Color.rgb(60, 60, 60);
    private static final Color MARKING = Color.WHITE;
    private static final Color ZEBRA   = Color.rgb(230, 230, 230);
    private static final Color ISLAND_G= Color.rgb(40, 120, 50);
    private static final Color ISLAND_L= Color.rgb(140, 140, 140);
    private static final Color RB_EDGE = Color.rgb(90, 90, 90);

    private static final int RH = NetworkLayout.ROAD_HALF;
    private static final int EX = NetworkLayout.ARM_EXT;

    // ── Vẽ toàn mạng lưới ──────────────────────────────────────
    public void drawNetwork(GraphicsContext gc) {
        // 1. Đường E-W
        gc.setFill(ASPHALT);
        gc.fillRect(NetworkLayout.TW_X - RH, -RH,
                    (NetworkLayout.VW_X - NetworkLayout.TW_X + RH*2), RH*2);

        // 2. Nhánh N-S tại mỗi giao lộ
        for (int ix : new int[]{NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X}) {
            gc.setFill(ASPHALT);
            gc.fillRect(ix - RH, -(RH + EX), RH*2, EX);
            gc.fillRect(ix - RH,  RH,         RH*2, EX);
        }

        // 3. Vạch trung tâm (đứt)
        drawCenterLines(gc);

        // 4. Hộp giao lộ + chi tiết
        drawThreeWayDetail(gc);
        drawFourWayDetail(gc);
        drawFiveWayDetail(gc);
    }

    private void drawCenterLines(GraphicsContext gc) {
        gc.setFill(MARKING);
        // Đường ngang
        for (int x = NetworkLayout.TW_X - RH; x < NetworkLayout.VW_X + RH; x += 38) {
            if (!nearAny(x, 'x')) gc.fillRect(x+2, -2, 18, 4);
        }
        // Đường dọc tại mỗi giao lộ
        for (int ix : new int[]{NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X}) {
            for (int y = -(RH+EX); y < -RH; y += 38) gc.fillRect(ix-2, y+2, 4, 18);
            for (int y = RH; y < RH+EX; y += 38)     gc.fillRect(ix-2, y+2, 4, 18);
        }
    }

    // ── THREE_WAY (T-junction N+S+E, không có W) ───────────────
    public void drawThreeWayDetail(GraphicsContext gc) {
        int ix = NetworkLayout.TW_X, iy = 0;
        gc.setFill(ASPHALT);
        gc.fillRect(ix-RH, iy-RH, RH*2, RH*2);
        stopLine(gc, ix, iy, 'N');
        stopLine(gc, ix, iy, 'S');
        stopLine(gc, ix, iy, 'E');
        zebra(gc, ix, iy, 'N'); zebra(gc, ix, iy, 'S'); zebra(gc, ix, iy, 'E');
        // Bít tường phía Tây (không có đường)
        gc.setFill(Color.rgb(35, 70, 35));
        gc.fillRect(ix-RH-22, iy-RH, 22, RH*2);
    }

    // ── FOUR_WAY ────────────────────────────────────────────────
    public void drawFourWayDetail(GraphicsContext gc) {
        int ix = NetworkLayout.FW_X, iy = 0;
        gc.setFill(ASPHALT);
        gc.fillRect(ix-RH, iy-RH, RH*2, RH*2);
        stopLine(gc, ix, iy, 'N'); stopLine(gc, ix, iy, 'S');
        stopLine(gc, ix, iy, 'E'); stopLine(gc, ix, iy, 'W');
        zebra(gc, ix, iy, 'N'); zebra(gc, ix, iy, 'S');
        zebra(gc, ix, iy, 'E'); zebra(gc, ix, iy, 'W');
    }

    // ── FIVE_WAY ────────────────────────────────────────────────
    public void drawFiveWayDetail(GraphicsContext gc) {
        int ix = NetworkLayout.VW_X, iy = 0;
        drawDiagonalArm(gc, ix, iy);     // vẽ trước, bị phủ bởi box
        gc.setFill(ASPHALT);
        gc.fillRect(ix-RH, iy-RH, RH*2, RH*2);
        stopLine(gc, ix, iy, 'N'); stopLine(gc, ix, iy, 'S');
        stopLine(gc, ix, iy, 'E'); stopLine(gc, ix, iy, 'W');
        zebra(gc, ix, iy, 'N'); zebra(gc, ix, iy, 'S');
        zebra(gc, ix, iy, 'E'); zebra(gc, ix, iy, 'W');
        drawRoundabout(gc, ix, iy);
    }

    private void drawDiagonalArm(GraphicsContext gc, int ix, int iy) {
        gc.save();
        gc.translate(ix, iy);
        gc.rotate(-45);
        gc.setFill(ASPHALT);
        gc.fillRect(-RH, -(RH + EX), RH*2, EX);
        // Vạch trung tâm chéo
        gc.setFill(MARKING);
        for (int y = -(RH+EX); y < -RH-6; y += 38) gc.fillRect(-2, y+2, 4, 18);
        gc.restore();
    }

    private void drawRoundabout(GraphicsContext gc, int ix, int iy) {
        int OR = 28, IR = 16;
        gc.setFill(ASPHALT);
        gc.fillOval(ix-OR, iy-OR, OR*2, OR*2);
        gc.setStroke(RB_EDGE); gc.setLineWidth(2);
        gc.setLineDashes(5,4);
        gc.strokeOval(ix-OR+3, iy-OR+3, (OR-3)*2, (OR-3)*2);
        gc.setLineDashes();
        gc.setFill(ISLAND_L); gc.fillOval(ix-IR, iy-IR, IR*2, IR*2);
        gc.setFill(ISLAND_G); gc.fillOval(ix-IR+4, iy-IR+4, (IR-4)*2, (IR-4)*2);
        gc.setLineWidth(1);
    }

    private void stopLine(GraphicsContext gc, int ix, int iy, char side) {
        gc.setFill(MARKING);
        int o = RH - 3;
        switch (side) {
            case 'N': gc.fillRect(ix-RH, iy+o, RH*2, 3); break;
            case 'S': gc.fillRect(ix-RH, iy-o-3, RH*2, 3); break;
            case 'E': gc.fillRect(ix-o-3, iy-RH, 3, RH*2); break;
            case 'W': gc.fillRect(ix+o, iy-RH, 3, RH*2); break;
        }
    }

    private void zebra(GraphicsContext gc, int ix, int iy, char side) {
        gc.setFill(ZEBRA);
        int ZW=5, ZS=9, ZL=14;
        switch (side) {
            case 'N': for (int i=0;i<RH*2;i+=ZS+ZW) gc.fillRect(ix-RH+i, iy+RH+3, ZW, ZL); break;
            case 'S': for (int i=0;i<RH*2;i+=ZS+ZW) gc.fillRect(ix-RH+i, iy-RH-ZL-3, ZW, ZL); break;
            case 'E': for (int i=0;i<RH*2;i+=ZS+ZW) gc.fillRect(ix-RH-ZL-3, iy-RH+i, ZL, ZW); break;
            case 'W': for (int i=0;i<RH*2;i+=ZS+ZW) gc.fillRect(ix+RH+3, iy-RH+i, ZL, ZW); break;
        }
    }

    private boolean nearAny(int x, char axis) {
        for (int ix : new int[]{NetworkLayout.TW_X, NetworkLayout.FW_X, NetworkLayout.VW_X}) {
            if (Math.abs(x - ix) < RH+4) return true;
        }
        return false;
    }
}
