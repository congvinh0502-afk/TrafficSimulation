package model.road;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Đại diện một con đường gồm nhiều làn.
 *
 * <p>
 * Dùng để mô hình hóa cấu trúc đường trong tương lai
 * khi mở rộng lên mạng lưới giao thông (RoadNetwork).
 * Hiện tại chưa dùng trong logic mô phỏng chính.
 * </p>
 */
public class Road {

    private final List<Lane> lanes;

    public Road() {
        this.lanes = new ArrayList<>();
    }

    /**
     * Thêm một làn vào đường này.
     *
     * @param lane làn cần thêm
     */
    public void addLane(Lane lane) {
        lanes.add(lane);
    }

    /**
     * @return danh sách làn (chỉ đọc)
     */
    public List<Lane> getLanes() {
        return Collections.unmodifiableList(lanes);
    }
}