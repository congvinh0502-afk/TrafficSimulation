package view.renderer;

import java.awt.*;

/**
 * EnvironmentRenderer – Điều phối toàn bộ cảnh quan môi trường.
 *
 * Phân công module:
 *   RoadRenderer      → vẽ lòng đường, vạch kẻ, vạch dừng, bùng binh
 *   BuildingRenderer  → vẽ các loại công trình (skyscraper, factory, luxuryHouse, restaurant)
 *   NatureRenderer    → vẽ cây (3 kiểu), hàng cây, công viên hồ nước
 *   ParkingRenderer   → vẽ bãi đỗ xe có xe đậu
 *   BaseRenderer      → vẽ nền cỏ, đèn đường
 *
 * EnvironmentRenderer KHÔNG tự copy logic vẽ – chỉ gọi các module trên.
 */
public class EnvironmentRenderer {

    private final RoadRenderer      road     = new RoadRenderer();
    private final BuildingRenderer  building = new BuildingRenderer();
    private final NatureRenderer    nature   = new NatureRenderer();
    private final ParkingRenderer   parking  = new ParkingRenderer();
    private final BaseRenderer      base     = new BaseRenderer();

    // =========================================================
    // FOUR WAY – Ngã tư: khu đô thị hỗn hợp
    //   • Góc TL : Nhà cao tầng (skyscraper) + cụm biệt thự
    //   • Góc TR : Bãi đỗ xe lớn + nhà máy
    //   • Góc DL : Công viên hồ nước
    //   • Góc DR : Cụm nhà ở + hàng cây đa dạng
    // =========================================================
    public void renderFourWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderFourWay(g2d);

        // --- GÓC TRÊN BÊN TRÁI: Nhà cao tầng + biệt thự ---
        building.drawSkyscraper(g2d, 30, 30);
        building.drawLuxuryHouse(g2d, 160, 40);
        building.drawLuxuryHouse(g2d, 155, 140);
        nature.drawTreeRow(g2d, 30, 200, 230, true);

        // --- GÓC TRÊN BÊN PHẢI: Bãi đỗ xe + nhà máy ---
        building.drawModernFactory(g2d, 580, 40);
        parking.drawParkingLotWithRealCars(g2d, 740, 30, 340, 180);
        nature.drawTreeRow(g2d, 560, 205, 640, true);

        // --- GÓC DƯỚI BÊN TRÁI: Công viên hồ nước ---
        nature.drawParkWithPond(g2d, 15, 560, 240, 220);

        // --- GÓC DƯỚI BÊN PHẢI: Khu nhà ở + hàng cây đa dạng ---
        nature.drawTreeRow(g2d, 550, 540, 630, true);
        nature.drawTreeRow(g2d, 545, 560, 220, false);
        building.drawLuxuryHouse(g2d, 600, 600);
        building.drawLuxuryHouse(g2d, 720, 650);
        building.drawLuxuryRestaurant(g2d, 870, 620);

        base.drawStreetLights(g2d);
    }

    // =========================================================
    // THREE WAY – Ngã ba: khu ngoại ô yên tĩnh
    //   • Góc TL : Công viên hồ nước
    //   • Góc BL : Cụm biệt thự nhỏ
    //   • Vùng phải: Bãi đỗ xe + nhà ở + hàng cây dài
    // =========================================================
    public void renderThreeWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderThreeWay(g2d);

        // Cảnh quan bên trái
        nature.drawParkWithPond(g2d, 15, 15, 240, 225);
        building.drawLuxuryHouse(g2d, 50, 600);
        building.drawLuxuryHouse(g2d, 155, 650);
        nature.drawTreeRow(g2d, 20, 560, 230, false);

        // Vùng đất bên phải
        parking.drawParkingLotWithRealCars(g2d, 580, 40, 340, 180);
        building.drawModernFactory(g2d, 960, 30);
        building.drawLuxuryHouse(g2d, 600, 580);
        building.drawLuxuryHouse(g2d, 760, 580);
        building.drawSkyscraper(g2d, 940, 580);
        nature.drawTreeRow(g2d, 545, 20, 760, false);
    }

    // =========================================================
    // FIVE WAY – Ngã năm / bùng binh: trung tâm đô thị sôi động
    //   • Góc TL : Nhà cao tầng + biệt thự
    //   • Góc TR : Bãi đỗ xe lớn + biệt thự
    //   • Góc DL : Công viên hồ nước
    //   • Góc DR : Nhà máy + nhà hàng + khu nhà ở
    //   • Bùng binh: 4 cây trang trí trung tâm
    // =========================================================
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        base.drawGrass(g);
        road.renderFiveWay(g);

        // Cây trang trí trên đảo bùng binh trung tâm (tâm 400,400)
        int cx = 400, cy = 400;
        nature.drawPineTree(g, cx - 18, cy - 58);
        nature.drawBeautifulTree(g, cx - 18, cy + 28);
        nature.drawBushTree(g, cx - 58, cy - 18);
        nature.drawPineTree(g, cx + 28, cy - 18);

        // --- GÓC TRÊN BÊN TRÁI ---
        building.drawSkyscraper(g, 30, 30);
        building.drawLuxuryHouse(g, 160, 40);
        building.drawLuxuryHouse(g, 155, 140);
        nature.drawTreeRow(g, 30, 205, 230, true);

        // --- GÓC TRÊN BÊN PHẢI (đẩy xa để nhường đường chéo) ---
        building.drawLuxuryHouse(g, 560, 40);
        parking.drawParkingLotWithRealCars(g, 850, 30, 320, 175);
        nature.drawTreeRow(g, 555, 205, 300, true);

        // --- GÓC DƯỚI BÊN TRÁI ---
        nature.drawParkWithPond(g, 15, 560, 240, 220);

        // --- GÓC DƯỚI BÊN PHẢI ---
        building.drawModernFactory(g, 590, 615);
        building.drawLuxuryRestaurant(g, 730, 650);
        building.drawLuxuryHouse(g, 870, 600);
        nature.drawTreeRow(g, 555, 540, 625, true);
        nature.drawTreeRow(g, 545, 565, 215, false);

        base.drawStreetLights(g);
        g.dispose();
    }
}