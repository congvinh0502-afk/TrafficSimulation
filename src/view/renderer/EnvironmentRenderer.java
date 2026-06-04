package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Điều phối vẽ toàn bộ cảnh quan (JavaFX) bằng thuật toán Procedural
 * Generation.
 */
public class EnvironmentRenderer {

    private final BaseRenderer base = new BaseRenderer();
    private final RoadRenderer road = new RoadRenderer();
    private final BuildingRenderer building = new BuildingRenderer();
    private final NatureRenderer nature = new NatureRenderer();
    private final ParkingRenderer parking = new ParkingRenderer();

    // Cache vật thể để tránh tính toán lại mỗi frame (gây nháy hình)
    private int currentIntersectionType = -1;
    private final List<Runnable> cachedProps = new ArrayList<>();

    public void renderFourWay(GraphicsContext gc) {
        checkAndGenerateGrid(gc, 4);
        base.drawGrass(gc);
        road.renderFourWay(gc);
        drawCachedProps(gc);
    }

    public void renderThreeWay(GraphicsContext gc) {
        checkAndGenerateGrid(gc, 3);
        base.drawGrass(gc);
        road.renderThreeWay(gc);
        drawCachedProps(gc);
    }

    public void renderFiveWay(GraphicsContext gc) {
        checkAndGenerateGrid(gc, 5);
        base.drawGrass(gc);
        road.renderFiveWay(gc);
        drawCachedProps(gc);
    }

    private void drawCachedProps(GraphicsContext gc) {
        for (Runnable prop : cachedProps) {
            prop.run();
        }
    }

    // ==========================================================
    // PROCEDURAL GENERATION: Quét lưới và tự động sinh cảnh quan
    // ==========================================================
    private void checkAndGenerateGrid(GraphicsContext gc, int type) {
        if (currentIntersectionType == type)
            return;
        currentIntersectionType = type;
        cachedProps.clear();

        // Tăng kích thước lưới lên 150 để các tòa nhà to không bị tràn sang ô bên cạnh
        int cellSize = 150;
        int cols = 1200 / cellSize;
        int rows = 800 / cellSize;
        Random rng = new Random(type * 1000L); 

        for (int r = 0; r <= rows; r++) {
            for (int c = 0; c <= cols; c++) {
                int x = c * cellSize;
                int y = r * cellSize;
                int cx = x + cellSize / 2;
                int cy = y + cellSize / 2;

                // Tăng margin an toàn để tòa nhà cách xa mép đường hơn
                boolean isRoad = isRoadCollision(x, y, type, 25) ||
                                 isRoadCollision(x + cellSize, y, type, 25) ||
                                 isRoadCollision(x, y + cellSize, type, 25) ||
                                 isRoadCollision(x + cellSize, y + cellSize, type, 25) ||
                                 isRoadCollision(cx, cy, type, 25);

                boolean isSidewalk = isRoadCollision(cx, cy, type, -15);

                if (!isRoad) {
                    int rand = rng.nextInt(100);
                    
                    // Căn giữa tương đối trong lưới 150x150
                    final int drawX = x + 15;
                    final int drawY = y + 15;

                    if (rand < 25) {
                        cachedProps.add(() -> nature.drawParkWithPond(gc, drawX, drawY, 120, 120));
                    } else if (rand < 40) {
                        cachedProps.add(() -> building.drawSkyscraper(gc, drawX, drawY));
                    } else if (rand < 60) {
                        cachedProps.add(() -> building.drawLuxuryHouse(gc, drawX, drawY));
                    } else if (rand < 75) {
                        cachedProps.add(() -> building.drawModernFactory(gc, drawX, drawY));
                    } else if (rand < 85) {
                        cachedProps.add(() -> building.drawLuxuryRestaurant(gc, drawX, drawY));
                    } else {
                        cachedProps.add(() -> parking.drawParkingLotWithCars(gc, drawX, drawY, 120, 120)); 
                    }
                } else if (!isSidewalk) {
                    if ((c + r) % 2 == 0) { 
                        cachedProps.add(() -> drawProceduralStreetLight(gc, cx, cy));
                    }
                }
            }
        }
    }

    /**
     * Toán học kiểm tra va chạm giữa 1 điểm và mặt đường
     */
    private boolean isRoadCollision(int px, int py, int type, int margin) {
        if (type == 3) { // 3-way
            boolean isHoriz = (py > 300 - margin && py < 500 + margin);
            boolean isVert = (px > 500 - margin && px < 700 + margin && py < 500 + margin);
            return isHoriz || isVert;
        } else if (type == 4) { // 4-way
            boolean isHoriz = (py > 300 - margin && py < 500 + margin);
            boolean isVert = (px > 500 - margin && px < 700 + margin);
            return isHoriz || isVert;
        } else if (type == 5) { // 5-way
            if (Math.hypot(px - 600, py - 400) < 170 + margin)
                return true;
            double[] angles = { 270, 342, 54, 126, 198 };
            for (double ang : angles) {
                double rad = Math.toRadians(ang);
                double dx = px - 600;
                double dy = py - 400;
                double proj = dx * Math.cos(rad) + dy * Math.sin(rad); // Độ dài trên trục nhánh
                double perp = Math.abs(-dx * Math.sin(rad) + dy * Math.cos(rad)); // Khoảng cách vuông góc tới trục
                if (proj > -50 && perp < 100 + margin)
                    return true;
            }
        }
        return false;
    }

    private void drawProceduralStreetLight(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x - 3, y - 3, 6, 6);
        gc.setFill(Color.rgb(255, 255, 150, 0.6)); // Ánh sáng đèn
        gc.fillOval(x - 12, y - 12, 24, 24);
    }
}