package view.renderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * Điều phối vẽ toàn bộ cảnh quan (JavaFX).
 *
 * <p>Gọi các renderer con theo thứ tự lớp vẽ.</p>
 */
public class EnvironmentRenderer {

    private final BaseRenderer     base     = new BaseRenderer();
    private final RoadRenderer     road     = new RoadRenderer();
    private final BuildingRenderer building = new BuildingRenderer();
    private final NatureRenderer   nature   = new NatureRenderer();
    private final ParkingRenderer  parking  = new ParkingRenderer();

    public void renderFourWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderFourWay(gc);
        building.drawSkyscraper(gc, 30, 30);
        building.drawLuxuryHouse(gc, 160, 40);
        building.drawLuxuryHouse(gc, 155, 140);
        nature.drawTreeRow(gc, 30, 200, 430, true);

        building.drawModernFactory(gc, 780, 40);
        parking.drawParkingLotWithCars(gc, 820, 30, 340, 180);
        nature.drawTreeRow(gc, 760, 205, 440, true);

        nature.drawParkWithPond(gc, 15, 560, 400, 220);

        nature.drawTreeRow(gc, 750, 540, 450, true);
        nature.drawTreeRow(gc, 745, 560, 220, false);
        building.drawLuxuryHouse(gc, 800, 600);
        building.drawLuxuryHouse(gc, 920, 650);
        building.drawLuxuryRestaurant(gc, 1060, 620);

        base.drawStreetLights(gc);
    }

    public void renderThreeWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderThreeWay(gc);

        // Góc Tây-Bắc
        nature.drawParkWithPond(gc, 15, 15, 400, 225);

        // Góc Tây-Nam (dưới giao lộ, phần cỏ phía dưới trục dọc bị bịt)
        building.drawLuxuryHouse(gc, 50, 560);
        building.drawLuxuryHouse(gc, 155, 610);
        building.drawLuxuryHouse(gc, 300, 600);
        nature.drawTreeRow(gc, 20, 540, 240, false);

        // Góc Đông-Bắc
        parking.drawParkingLotWithCars(gc, 780, 40, 340, 180);
        building.drawModernFactory(gc, 1000, 30);

        // Góc Đông-Nam
        building.drawLuxuryHouse(gc, 760, 560);
        building.drawLuxuryHouse(gc, 900, 560);
        building.drawSkyscraper(gc, 1080, 560);

        // Cây dọc bên phải đường ngang (phần trên và dưới giao lộ)
        nature.drawTreeRow(gc, 745, 20, 250, false); // phần trên
        nature.drawTreeRow(gc, 745, 540, 250, false); // phần dưới

        // Bịt vùng cỏ dưới trục dọc bằng cây
        nature.drawTreeRow(gc, 505, 520, 190, false);
    }

    public void renderFiveWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderFiveWay(gc);

        // Tâm bùng binh tại (600,400), bán kính 170px
        // Cảnh vật đặt ở các góc tự do giữa 5 nhánh

        // Góc NW-N (~234°): nhà phố + cây (phía trái-trên)
        building.drawSkyscraper(gc, 30, 30);
        building.drawLuxuryHouse(gc, 160, 35);
        building.drawLuxuryHouse(gc, 155, 145);
        nature.drawTreeRow(gc, 30, 215, 240, true);

        // Góc N-NE (~306°): tòa nhà cao (phía trên)
        building.drawModernFactory(gc, 430, 30);

        // Góc NE-SE (~18°): bãi đỗ xe (phía phải-trên)
        parking.drawParkingLotWithCars(gc, 850, 30, 310, 170);

        // Góc SE-SW (~90°): nhà hàng (phía dưới)
        building.drawLuxuryRestaurant(gc, 470, 640);

        // Góc SW-NW (~162°): công viên (phía trái-dưới)
        nature.drawParkWithPond(gc, 20, 530, 230, 220);

        base.drawStreetLights(gc);
    }
}
