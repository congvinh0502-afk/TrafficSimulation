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
        nature.drawTreeRow(gc, 30, 200, 230, true);

        building.drawModernFactory(gc, 580, 40);
        parking.drawParkingLotWithCars(gc, 740, 30, 340, 180);
        nature.drawTreeRow(gc, 560, 205, 640, true);

        nature.drawParkWithPond(gc, 15, 560, 240, 220);

        nature.drawTreeRow(gc, 550, 540, 630, true);
        nature.drawTreeRow(gc, 545, 560, 220, false);
        building.drawLuxuryHouse(gc, 600, 600);
        building.drawLuxuryHouse(gc, 720, 650);
        building.drawLuxuryRestaurant(gc, 870, 620);

        base.drawStreetLights(gc);
    }

    public void renderThreeWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderThreeWay(gc);

        // Góc Tây-Bắc
        nature.drawParkWithPond(gc, 15, 15, 240, 225);

        // Góc Tây-Nam (dưới giao lộ, phần cỏ phía dưới trục dọc bị bịt)
        building.drawLuxuryHouse(gc, 50, 560);
        building.drawLuxuryHouse(gc, 155, 610);
        nature.drawTreeRow(gc, 20, 540, 240, false);

        // Góc Đông-Bắc
        parking.drawParkingLotWithCars(gc, 580, 40, 340, 180);
        building.drawModernFactory(gc, 960, 30);

        // Góc Đông-Nam
        building.drawLuxuryHouse(gc, 560, 560);
        building.drawLuxuryHouse(gc, 700, 560);
        building.drawSkyscraper(gc, 880, 560);

        // Cây dọc bên phải đường ngang (phần trên và dưới giao lộ)
        nature.drawTreeRow(gc, 545, 20, 250, false); // phần trên
        nature.drawTreeRow(gc, 545, 540, 250, false); // phần dưới

        // Bịt vùng cỏ dưới trục dọc bằng cây
        nature.drawTreeRow(gc, 305, 520, 190, false);
    }

    public void renderFiveWay(GraphicsContext gc) {
        base.drawGrass(gc);
        road.renderFiveWay(gc);

        // 5 nhánh 72° tỏa từ tâm (400,400), bùng binh r=170px
        // Góc: 270°=N, 342°=NE, 54°=SE, 126°=SW, 198°=NW
        // Các góc tự do nằm giữa các nhánh:
        // Giữa N(270°) và NE(342°) → ~306°, hướng phải-trên
        // Giữa NE(342°) và SE(54°) → ~18°, hướng phải
        // Giữa SE(54°) và SW(126°)→ ~90°, hướng dưới
        // Giữa SW(126°) và NW(198°)→ ~162°, hướng trái-dưới
        // Giữa NW(198°) và N(270°) → ~234°, hướng trái-trên

        // Góc phải-trên (giữa N và NE, ~306°): tòa nhà cao tầng
        building.drawSkyscraper(gc, 560, 30);

        // Góc phải (~18°, Đông xa): bãi đỗ xe
        parking.drawParkingLotWithCars(gc, 800, 310, 360, 170);

        // Góc dưới (~90°, Nam xa): nhà máy + nhà hàng
        building.drawModernFactory(gc, 480, 620);
        building.drawLuxuryRestaurant(gc, 630, 650);

        // Góc trái-dưới (~162°): công viên
        nature.drawParkWithPond(gc, 20, 530, 230, 220);

        // Góc trái-trên (~234°): nhà phố + cây
        building.drawSkyscraper(gc, 30, 30);
        building.drawLuxuryHouse(gc, 155, 35);
        building.drawLuxuryHouse(gc, 150, 145);
        nature.drawTreeRow(gc, 30, 210, 210, true);

        // Cây trang trí xa trục đường (góc SE và SW)
        nature.drawTreeRow(gc, 620, 560, 500, true); // hàng ngang phía dưới
        nature.drawTreeRow(gc, 830, 20, 520, false); // hàng dọc phía phải

        base.drawStreetLights(gc);
    }
}
