package util;

import java.util.ArrayList;
import java.util.List;

public class PathBuilder {
    
    /**
     * Dùng cho rẽ trái/phải thông thường (Ngã 3, Ngã 4)
     */
    public static List<double[]> buildCubicPath(
            double startX, double startY,   // Tọa độ xe hiện tại (làn vào)
            double c1X, double c1Y,         // Điểm kiểm soát 1 (kéo dài từ làn vào)
            double c2X, double c2Y,         // Điểm kiểm soát 2 (kéo dài ngược từ làn ra)
            double endX, double endY,       // Tọa độ kết thúc cua (làn ra)
            int steps) {                    // Số điểm mượt (thường 20-30)
            
        List<double[]> path = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double u = 1 - t;
            
            // Công thức nội suy Cubic Bezier
            double x = u*u*u*startX + 3*u*u*t*c1X + 3*u*t*t*c2X + t*t*t*endX;
            double y = u*u*u*startY + 3*u*u*t*c1Y + 3*u*t*t*c2Y + t*t*t*endY;
            path.add(new double[]{x, y});
        }
        return path;
    }

    /**
     * Dùng cho xoay quanh vòng xuyến Ngã 5
     */
    public static List<double[]> buildRoundaboutPath(
            double startX, double startY, double endX, double endY,
            double cx, double cy, double ringRadius) {
            
        List<double[]> path = new ArrayList<>();
        double entryAngle = Math.atan2(startY - cy, startX - cx);
        double exitAngle = Math.atan2(endY - cy, endX - cx);
        
        // Tính góc delta sao cho luôn đi ngược chiều kim đồng hồ
        double delta = entryAngle - exitAngle;
        while (delta <= 0) delta += Math.PI * 2;

        int arcSteps = 30; // Độ mượt của vòng
        for (int i = 1; i <= arcSteps; i++) {
            double currentAngle = entryAngle - delta * i / arcSteps;
            double x = cx + Math.cos(currentAngle) * ringRadius;
            double y = cy + Math.sin(currentAngle) * ringRadius;
            path.add(new double[]{x, y});
        }
        // Thêm nốt điểm thoát
        path.add(new double[]{endX, endY});
        return path;
    }
}