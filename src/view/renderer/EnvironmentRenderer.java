package view.renderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Điều phối vẽ toàn bộ cảnh quan môi trường.
 *
 * <p>
 * Lớp này chỉ gọi các renderer con theo đúng thứ tự lớp vẽ —
 * không chứa bất kỳ logic vẽ nào trực tiếp. Phân công:
 * <ul>
 * <li>{@link BaseRenderer} → nền cỏ và đèn đường</li>
 * <li>{@link RoadRenderer} → mặt đường và vạch kẻ</li>
 * <li>{@link BuildingRenderer} → công trình</li>
 * <li>{@link NatureRenderer} → cây và công viên</li>
 * <li>{@link ParkingRenderer} → bãi đỗ xe</li>
 * </ul>
 * </p>
 */
public class EnvironmentRenderer {

    private final BaseRenderer base = new BaseRenderer();
    private final RoadRenderer road = new RoadRenderer();
    private final BuildingRenderer building = new BuildingRenderer();
    private final NatureRenderer nature = new NatureRenderer();
    private final ParkingRenderer parking = new ParkingRenderer();

    // ==========================================================
    // Ngã tư
    // ==========================================================

    /**
     * Render cảnh quan ngã tư: khu đô thị hỗn hợp.
     * <ul>
     * <li>Góc TL: toà nhà cao tầng + biệt thự</li>
     * <li>Góc TR: nhà máy + bãi đỗ xe</li>
     * <li>Góc DL: công viên hồ nước</li>
     * <li>Góc DR: khu nhà ở + hàng cây</li>
     * </ul>
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderFourWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderFourWay(g2d);

        // Góc trên trái
        building.drawSkyscraper(g2d, 30, 30);
        building.drawLuxuryHouse(g2d, 160, 40);
        building.drawLuxuryHouse(g2d, 155, 140);
        nature.drawTreeRow(g2d, 30, 200, 230, true);

        // Góc trên phải
        building.drawModernFactory(g2d, 580, 40);
        parking.drawParkingLotWithCars(g2d, 740, 30, 340, 180);
        nature.drawTreeRow(g2d, 560, 205, 640, true);

        // Góc dưới trái
        nature.drawParkWithPond(g2d, 15, 560, 240, 220);

        // Góc dưới phải
        nature.drawTreeRow(g2d, 550, 540, 630, true);
        nature.drawTreeRow(g2d, 545, 560, 220, false);
        building.drawLuxuryHouse(g2d, 600, 600);
        building.drawLuxuryHouse(g2d, 720, 650);
        building.drawLuxuryRestaurant(g2d, 870, 620);

        base.drawStreetLights(g2d);
    }

    // ==========================================================
    // Ngã ba
    // ==========================================================

    /**
     * Render cảnh quan ngã ba: khu ngoại ô yên tĩnh.
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderThreeWay(Graphics2D g2d) {
        base.drawGrass(g2d);
        road.renderThreeWay(g2d);

        // Bên trái
        nature.drawParkWithPond(g2d, 15, 15, 240, 225);
        building.drawLuxuryHouse(g2d, 50, 600);
        building.drawLuxuryHouse(g2d, 155, 650);
        nature.drawTreeRow(g2d, 20, 560, 230, false);

        // Bên phải
        parking.drawParkingLotWithCars(g2d, 580, 40, 340, 180);
        building.drawModernFactory(g2d, 960, 30);
        building.drawLuxuryHouse(g2d, 600, 580);
        building.drawLuxuryHouse(g2d, 760, 580);
        building.drawSkyscraper(g2d, 940, 580);
        nature.drawTreeRow(g2d, 545, 20, 760, false);
    }

    // ==========================================================
    // Ngã năm
    // ==========================================================

    /**
     * Render cảnh quan ngã năm: trung tâm đô thị sôi động.
     * Có 4 cây trang trí trên đảo bùng binh trung tâm.
     *
     * @param g2d đối tượng đồ họa
     */
    public void renderFiveWay(Graphics2D g2d) {
        Graphics2D g = (Graphics2D) g2d.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        base.drawGrass(g);
        road.renderFiveWay(g);

        // Cây trang trí bùng binh (tâm 400, 400)
        int cx = 400, cy = 400;
        nature.drawPineTree(g, cx - 18, cy - 58);
        nature.drawBeautifulTree(g, cx - 18, cy + 28);
        nature.drawBushTree(g, cx - 58, cy - 18);
        nature.drawPineTree(g, cx + 28, cy - 18);

        // Góc trên trái
        building.drawSkyscraper(g, 30, 30);
        building.drawLuxuryHouse(g, 160, 40);
        building.drawLuxuryHouse(g, 155, 140);
        nature.drawTreeRow(g, 30, 205, 230, true);

        // Góc trên phải
        building.drawLuxuryHouse(g, 560, 40);
        parking.drawParkingLotWithCars(g, 850, 30, 320, 175);
        nature.drawTreeRow(g, 555, 205, 300, true);

        // Góc dưới trái
        nature.drawParkWithPond(g, 15, 560, 240, 220);

        // Góc dưới phải
        building.drawModernFactory(g, 590, 615);
        building.drawLuxuryRestaurant(g, 730, 650);
        building.drawLuxuryHouse(g, 870, 600);
        nature.drawTreeRow(g, 555, 540, 625, true);
        nature.drawTreeRow(g, 545, 565, 215, false);

        base.drawStreetLights(g);
        g.dispose();
    }
}