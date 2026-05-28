package view.renderer;

import java.awt.*;

public class RoadRenderer {

    // =========================================================
    // FOUR WAY (NGÃ TƯ)
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Lòng đường nhựa vuông góc (Rộng 200px)
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);     // Trục dọc
        g.fillRect(0, 300, 1200, 200);    // Trục ngang

        // Mép vỉa hè xám đậm cho ngã tư thường
        g.setColor(new Color(40, 40, 40));
        g.fillRect(295, 0, 5, 800);
        g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5);
        g.fillRect(0, 500, 1200, 5);

        // Vạch đứt chia làn
        g.setColor(Color.WHITE);
        for (int i = 0; i < 800; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(395, i, 10, 20, 4, 4);
        }
        for (int i = 0; i < 1200; i += 40) {
            if (i < 280 || i > 500) g.fillRoundRect(i, 395, 20, 10, 4, 4);
        }

        // Vạch dừng đèn đỏ
        g.fillRect(300, 270, 200, 6);
        g.fillRect(300, 524, 200, 6);
        g.fillRect(270, 300, 6, 200);
        g.fillRect(524, 300, 6, 200);

        // Vạch đi bộ
        drawHorizontalZebra(g, 300, 280);
        drawHorizontalZebra(g, 300, 490);
        drawVerticalZebra(g, 280, 300);
        drawVerticalZebra(g, 490, 300);

        // Đảo giao thông nhỏ ngã tư thường
        g.setColor(new Color(90, 90, 90));
        g.fillOval(360, 360, 80, 80);

        g.dispose();
    }

    // =========================================================
    // THREE WAY (NGÃ BA)
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 500, 200);
        g.dispose();
    }

    // =========================================================
    // FIVE WAY (NGÃ NĂM VÒNG XUYẾN LỚN - CHUẨN TỌA ĐỘ)
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = 400; // Tâm hình học chính xác của hệ thống 1200x800
        int centerY = 400;
        int roadWidth = 200;       // Độ rộng đường đồng bộ hoàn toàn
        int roundaboutDia = 340;   // Đường kính thảm nhựa bùng binh lớn kết nối 5 ngả
        int islandDia = 160;       // Đường kính đảo cỏ trung tâm

        // 1. Trải nhựa nền đường 4 trục vuông góc
        g.setColor(new Color(55, 55, 55));
        g.fillRect(300, 0, 200, 800);
        g.fillRect(0, 300, 1200, 200);

        // 2. Vẽ mép đường (Vỉa hè góc) cho các trục thẳng bên ngoài vùng bùng binh
        g.setColor(new Color(40, 40, 40));
        g.fillRect(295, 0, 5, 800);  g.fillRect(500, 0, 5, 800);
        g.fillRect(0, 295, 1200, 5); g.fillRect(0, 500, 1200, 5);

        // 3. Vẽ nhánh đường chéo thứ 5 (Đông Bắc) lao thẳng vào tâm 400, 400
        drawDiagonalRoad(g, centerX, centerY, roadWidth);

        // 4. Vẽ đè khối bùng binh lớn lên chính giữa để nuốt trọn các điểm giao lỗi cấu trúc
        drawRoundabout(g, centerX, centerY, roundaboutDia, islandDia);

        // 5. Vẽ vạch kẻ đi bộ lùi ra ngoài rìa bùng binh một chút để tạo không gian thoáng
        drawHorizontalZebra(g, 300, 210); // Nhánh Bắc (Lùi lên trên)
        drawHorizontalZebra(g, 300, 570); // Nhánh Nam (Lùi xuống dưới)
        drawVerticalZebra(g, 210, 300);   // Nhánh Tây (Lùi sang trái)
        drawVerticalZebra(g, 570, 300);   // Nhánh Đông (Lùi sang phải)

        g.dispose();
    }
    

    private void drawHorizontalZebra(Graphics2D g, int startX, int y) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(startX + i, y, 10, 20);
        }
    }

    private void drawVerticalZebra(Graphics2D g, int x, int startY) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < 200; i += 20) {
            g.fillRect(x, startY + i, 20, 10);
        }
    }

    // Vẽ nhánh đường chéo Đông Bắc (Xoay ngược chiều kim đồng hồ 45 độ)
    public void drawDiagonalRoad(Graphics2D g2d, int centerX, int centerY, int roadWidth) {
        Graphics2D gChieo = (Graphics2D) g2d.create();
        gChieo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        gChieo.translate(centerX, centerY);
        gChieo.rotate(Math.toRadians(-45)); // Quay hướng lên góc trên bên phải màn hình
        
        // Mép đường chéo
        gChieo.setColor(new Color(40, 40, 40));
        gChieo.fillRect(-(roadWidth / 2 + 5), -900, roadWidth + 10, 900);
        
        // Lòng đường chéo nhựa xám
        gChieo.setColor(new Color(55, 55, 55));
        gChieo.fillRect(-roadWidth / 2, -900, roadWidth, 900); 

        // Vạch đứt phân làn cho nhánh đường chéo
        gChieo.setColor(Color.WHITE);
        for (int i = -900; i < -180; i += 40) {
            gChieo.fillRoundRect(-5, i, 10, 20, 4, 4);
        }
        
        // Vạch đi bộ chéo dành riêng cho nhánh thứ 5
        gChieo.setColor(Color.WHITE);
        for (int i = -roadWidth/2; i < roadWidth/2; i += 20) {
            gChieo.fillRect(i, -230, 10, 20);
        }
        
        gChieo.dispose();
    }

    // Vẽ khối bùng binh tròn
    public void drawRoundabout(Graphics2D g2d, int centerX, int centerY, int outerDia, int islandDia) {
        Graphics2D gRound = (Graphics2D) g2d.create();
        gRound.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Thảm nhựa bùng binh
        gRound.setColor(new Color(55, 55, 55));
        gRound.fillOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // Mép viền đá bao quanh bùng binh nhựa để phân biệt với các ngả đường nhập làn
        gRound.setColor(new Color(45, 45, 45));
        gRound.setStroke(new BasicStroke(3));
        gRound.drawOval(centerX - outerDia / 2, centerY - outerDia / 2, outerDia, outerDia);

        // 2. Vạch đứt xoay vòng tròn hướng dẫn xe chạy trong vòng xuyến
        gRound.setColor(Color.WHITE);
        float[] dash = {14.0f, 12.0f};
        gRound.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        int laneRadius = outerDia - 60;
        gRound.drawOval(centerX - laneRadius / 2, centerY - laneRadius / 2, laneRadius, laneRadius);

        // 3. Đảo đá xỉ vỉa hè bùng binh
        gRound.setStroke(new BasicStroke(1));
        gRound.setColor(new Color(160, 160, 160));
        gRound.fillOval(centerX - islandDia / 2, centerY - islandDia / 2, islandDia, islandDia);

        // 4. Lõi cỏ xanh trang trí tiểu cảnh bùng binh trung tâm
        gRound.setColor(new Color(35, 130, 55)); 
        gRound.fillOval(centerX - (islandDia - 14) / 2, centerY - (islandDia - 14) / 2, islandDia - 14, islandDia - 14);
        
        gRound.dispose();
    }
}