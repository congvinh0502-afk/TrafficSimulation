package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.network.NetworkLayout;

/** Renderer cây cỏ — không đặt cây trên nhánh chéo 5-way. */
public class NatureRenderer {

    private static final int RH = NetworkLayout.ROAD_HALF;
    // Vùng chéo của 5-way: tránh đặt cây trong phạm vi này
    private static final int VW_X = NetworkLayout.VW_X;
    private static final int VW_Y = NetworkLayout.VW_Y;
    private static final double DIAG_CLEAR = 80; // px clearance từ tâm chéo

    public void drawBeautifulTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(90, 50, 15));  gc.fillRect(x+10, y+22, 6, 12);
        gc.setFill(Color.rgb(15, 95, 15));  gc.fillOval(x, y, 26, 26);
        gc.setFill(Color.rgb(35, 145, 35)); gc.fillOval(x+2, y+1, 22, 22);
        gc.setFill(Color.rgb(75, 195, 75)); gc.fillOval(x+5, y-1, 14, 14);
    }

    public void drawPineTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(100, 60, 20)); gc.fillRect(x+9, y+24, 5, 10);
        gc.setFill(Color.rgb(20, 100, 30));
        gc.fillPolygon(new double[]{x, x+26, x+13}, new double[]{y+26, y+26, y+10}, 3);
        gc.setFill(Color.rgb(30, 130, 45));
        gc.fillPolygon(new double[]{x+4, x+22, x+13}, new double[]{y+18, y+18, y+4}, 3);
        gc.setFill(Color.rgb(50, 160, 60));
        gc.fillPolygon(new double[]{x+7, x+19, x+13}, new double[]{y+12, y+12, y}, 3);
    }

    public void drawBushTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(110, 65, 20)); gc.fillRect(x+10, y+20, 4, 8);
        gc.setFill(Color.rgb(30, 110, 50));
        gc.fillOval(x, y+5, 20, 18); gc.fillOval(x+8, y+2, 20, 18); gc.fillOval(x+4, y, 18, 18);
        gc.setFill(Color.rgb(60, 165, 80));
        gc.fillOval(x+4, y+2, 12, 10); gc.fillOval(x+10, y, 10, 10);
        gc.setFill(Color.rgb(240, 140, 160, 0.7));
        gc.fillOval(x+6, y+4, 5, 4); gc.fillOval(x+14, y+2, 4, 4);
    }

    /**
     * Vẽ hàng cây.
     * @param avoidDiag nếu true, bỏ qua cây gần nhánh chéo VW
     * @param intX      tâm X giao lộ để tránh đặt cây trên mặt đường (-1 = không tránh)
     * @param intY      tương tự (-1 = không tránh)
     */
    public void drawTreeRow(GraphicsContext gc, int startX, int startY, int length,
                            boolean horizontal, int intX, int intY) {
        int sp = 52, idx = 0;
        if (horizontal) {
            for (int x = startX; x < startX + length; x += sp) {
                if (onRoad(x, startY, intX, intY)) { idx++; continue; }
                if (onDiagonal(x, startY)) { idx++; continue; }
                drawByIdx(gc, x, startY, idx++);
            }
        } else {
            for (int y = startY; y < startY + length; y += sp) {
                if (onRoad(startX, y, intX, intY)) { idx++; continue; }
                if (onDiagonal(startX, y)) { idx++; continue; }
                drawByIdx(gc, startX, y, idx++);
            }
        }
    }

    public void drawParkWithPond(GraphicsContext gc, int x, int y, int w, int h) {
        gc.setFill(Color.rgb(50, 160, 70));  gc.fillRect(x, y, w, h);
        gc.setFill(Color.rgb(55, 120, 190)); gc.fillOval(x+w/4, y+h/4, w/2, h/2);
        gc.setStroke(Color.rgb(120, 110, 100)); gc.setLineWidth(2);
        gc.strokeOval(x+w/4, y+h/4, w/2, h/2); gc.setLineWidth(1);
        gc.setFill(Color.rgb(140, 75, 25));
        gc.fillRect(x+w/4-12, y+h/2, 8, 18);
        gc.fillRect(x+3*w/4+4, y+h/2, 8, 18);
        drawBeautifulTree(gc, x+8,  y+8);
        drawPineTree(gc,  x+w-34, y+8);
        drawBushTree(gc,  x+8,  y+h-32);
        drawBeautifulTree(gc, x+w-34, y+h-32);
    }

    private void drawByIdx(GraphicsContext gc, int x, int y, int idx) {
        switch (idx % 3) {
            case 0: drawBeautifulTree(gc, x, y); break;
            case 1: drawPineTree(gc, x, y);      break;
            default: drawBushTree(gc, x, y);     break;
        }
    }

    /** Kiểm tra điểm (px, py) có nằm trên mặt đường không. */
    private boolean onRoad(int px, int py, int intX, int intY) {
        if (intX < 0) return false;
        boolean onNS = Math.abs(px - intX) < RH + 14;
        boolean onEW = Math.abs(py - 0) < RH + 14; // đường ngang ở y=0
        return onNS || onEW;
    }

    /** Kiểm tra điểm có gần nhánh chéo của 5-way không. */
    private boolean onDiagonal(int px, int py) {
        // Nhánh chéo 45° từ tâm VW_X, VW_Y đi hướng NE
        // Khoảng cách từ điểm đến đường chéo: |((px-VW_X) + (py-VW_Y))/sqrt(2)|
        double dx = px - VW_X, dy = py - VW_Y;
        // Chỉ kiểm tra phần NE (dx > 0 && dy < 0)
        if (dx < -20 || dy > 20) return false;
        double distToLine = Math.abs(dx + dy) / Math.sqrt(2); // |dx - (-dy)| / sqrt(2)... 
        // Đường chéo NE: y = VW_Y - (x - VW_X) → x + y = VW_X + VW_Y = VW_X
        // dist = |px + py - VW_X| / sqrt(2)
        double dist = Math.abs(px + py - VW_X) / Math.sqrt(2);
        return dist < RH + 14;
    }
}
