# Báo cáo lỗi logic - TrafficSimulation.VN

Tập hợp các lỗi/không nhất quán logic đã rà soát nhanh trong repository và hướng khắc phục ngắn gọn.

## Tổng quan

- Những file đã kiểm tra chính: [src/controller/TrafficController.java](src/controller/TrafficController.java), [src/manager/LaneManager.java](src/manager/LaneManager.java), [src/manager/VehicleSpawnManager.java](src/manager/VehicleSpawnManager.java), [src/model/vehicle/Vehicle.java](src/model/vehicle/Vehicle.java), [src/model/vehicle/Car.java](src/model/vehicle/Car.java), [src/strategy/driver/NormalDriver.java](src/strategy/driver/NormalDriver.java), [src/view/renderer/RoadRenderer.java](src/view/renderer/RoadRenderer.java).

## Vấn đề cụ thể

1. Lane center không nhất quán với renderer
   - File: [src/manager/LaneManager.java](src/manager/LaneManager.java)
   - Mô tả: Các giá trị center X/Y hiện tại không đối xứng và không khớp với vùng đường được vẽ trong `RoadRenderer`. Kết quả: vị trí xe, thay đổi lane, và căn chỉnh sai.
   - Khắc phục: Định nghĩa hằng số trung tâm lane dựa trên kích thước đường (vd. INTERSECTION_LEFT/RIGHT/TOP/BOTTOM, LANE_CENTER_X/Y) và trả về giá trị tính từ hằng số đó.
   - Hướng giải quyết (chi tiết):
     1. Tạo `src/config/Constants.java` chứa hằng số hình học, ví dụ:
        - `INTERSECTION_LEFT = 300`, `INTERSECTION_TOP = 300`, `INTERSECTION_WIDTH = 200`, `INTERSECTION_HEIGHT = 200`.
        - Từ đó tính `INTERSECTION_RIGHT = INTERSECTION_LEFT + INTERSECTION_WIDTH`, `INTERSECTION_BOTTOM = INTERSECTION_TOP + INTERSECTION_HEIGHT`, và `LANE_OFFSET = 20`.
     2. Sửa `LaneManager.getLaneCenterX/Y()` để trả giá trị tính toán dựa trên `Constants` (ví dụ: `return INTERSECTION_LEFT + LANE_OFFSET` hoặc `INTERSECTION_RIGHT - LANE_OFFSET`).
     3. Chạy ứng dụng, kiểm tra visually: các xe nên nằm chính xác trên đường kẻ.
     4. Nếu có nhiều loại bản đồ (3-way/4-way/5-way), cung cấp hàm cấu hình map hoặc enum để trả về tập hằng số phù hợp.

2. Ngưỡng dừng/biên giao lộ rải rác và mâu thuẫn
   - File: [src/controller/TrafficController.java](src/controller/TrafficController.java)
   - Mô tả: Nhiều số cứng (360, 430, 520, 640, ...) dùng để xác định khi nào xe phải dừng/được cho vào giao lộ; các giá trị này không đồng bộ giữa các hàm và với bản đồ.
   - Khắc phục: Tạo hằng số tập trung (INTERSECTION_TOP/BOTTOM/LEFT/RIGHT, STOP_LINE_DISTANCE) và sử dụng chúng xuyên suốt các kiểm tra (checkTrafficLight, canEnterIntersection, handleTurning...).
   - Hướng giải quyết (chi tiết):
     1. Đưa các số cứng liên quan giao lộ vào `Constants.java`: `INTERSECTION_LEFT/RIGHT/TOP/BOTTOM`, `STOP_LINE_OFFSET` (khoảng cách trước vạch dừng), `INTERSECTION_PADDING`.
     2. Thay mọi so sánh như `vehicle.getX() + 60 >= 360` bằng helper rõ ràng: `isPastStopLine(vehicle, direction)` hoặc `vehicleIsNearIntersection(vehicle, STOP_LINE_OFFSET)` sử dụng hằng số.
     3. Viết unit test nhỏ hoặc log để xác minh xe dừng ở cùng một vị trí khi đèn đỏ cho tất cả hướng.

3. Strategy `shouldStop(...)` không được sử dụng
   - Files: [src/strategy/driver/\*.java](src/strategy/driver/) và [src/controller/TrafficController.java](src/controller/TrafficController.java)
   - Mô tả: Các lớp hành vi lái xe (Normal/Aggressive/Emergency) định nghĩa `shouldStop(...)` nhưng controller hiện tại không gọi method này; controller tự quyết dừng bằng logic cứng.
   - Khắc phục: Tích hợp `vehicle.getBehavior().shouldStop(...)` vào `checkTrafficLight()` (với fallback nếu behavior == null) để hành vi lái thực sự ảnh hưởng tới quyết định dừng.
   - Hướng giải quyết (chi tiết):
     1. Ở đầu `checkTrafficLight()`, lấy `DriverBehavior b = vehicle.getBehavior()`.
     2. Ở điểm quyết định dừng, thay logic cứng `if (mustStop || blocked) vehicle.setStopped(true)` thành:
        - Nếu `b != null` thì `mustStop = b.shouldStop(vehicle, vehicles, relevantLight)` (với `relevantLight` là vertical/horizontal tuỳ hướng).
        - Kết hợp `blocked` với `behavior` (ví dụ: ambulance trả luôn false cho shouldStop).
     3. Đảm bảo `VehicleSpawnManager` đã gán behavior cho mọi xe; nếu không, mặc định gán `NormalDriver`.
     4. Kiểm thử: tạo 1 ambulance (EmergencyDriver) và 1 car (NormalDriver) trước vạch dừng khi đèn đỏ, và xác nhận ambulance không dừng nếu `shouldStop` trả false.

4. Spawn positions không khớp với lane center
   - File: [src/manager/VehicleSpawnManager.java](src/manager/VehicleSpawnManager.java)
   - Mô tả: `getSpawnX()` / `getSpawnY()` trả các toạ độ cứng (470, 530, -100, 1100, ...) không khớp với `LaneManager` dẫn đến xe spawn lệch hoặc chồng nhau.
   - Khắc phục: Spawn theo `LaneManager.getLaneCenterX/Y(direction, lane)` cộng thêm offset spawn phù hợp theo hướng.
   - Hướng giải quyết (chi tiết):
     1. Trong `spawnRandomVehicle()` lấy lane ngẫu nhiên trước, sau đó lấy center bằng `LaneManager.getLaneCenterX/Y(direction, lane)`.
     2. Tính offset spawn dọc theo hướng (ví dụ: phía trên/below intersection dùng -SPAWN_DISTANCE hoặc +SPAWN_DISTANCE) và đặt `x`/`y` tương ứng.
     3. Ví dụ mã ngắn:
        - `int centerX = LaneManager.getLaneCenterX(direction, lane);`
        - `double spawnX = direction == EAST ? centerX - SPAWN_DISTANCE : (direction == WEST ? centerX + SPAWN_DISTANCE : centerX);`
     4. Điều chỉnh `canSpawn()` threshold nếu cần để tránh spawn chồng.

5. Magic numbers và các hằng số khoảng cách phân tán
   - Files: nhiều chỗ (`TrafficController`, `VehicleSpawnManager`, `Vehicle`, ...)
   - Mô tả: Sử dụng nhiều số cố định (60, 120, 40, 25, 3, 5, ...). Khó bảo trì và dễ gây lỗi khi thay đổi map.
   - Khắc phục: Tạo `src/config/Constants.java` chứa tên hằng số (SAFE_DISTANCE, LANE_OFFSET, LANE_CHANGE_COOLDOWN, TURN_FINISH_TOLERANCE, INTERSECTION_BOUNDS, ...) và thay thế số cứng.
   - Hướng giải quyết (chi tiết):
     1. Thiết kế `Constants.java` với các nhóm: Map geometry, Physics (speed limits), Safety distances, Timers (cooldown), Visual offsets.
     2. Thay tất cả số cứng hiện có trong `TrafficController`, `VehicleSpawnManager`, `Vehicle` bằng tham chiếu tới `Constants`.
     3. Giữ một file README ngắn mô tả ý nghĩa từng hằng số để dễ tinh chỉnh.

6. `alignVehicle()` dùng `targetX != 0`/`targetY != 0` làm sentinel
   - File: [src/controller/TrafficController.java](src/controller/TrafficController.java)
   - Mô tả: `targetX`/`targetY` mặc định 0 có thể là toạ độ hợp lệ; dùng 0 làm kiểm tra là không an toàn.
   - Khắc phục: Thay bằng flag rõ ràng (`hasTargetX`), hoặc khởi tạo `targetX/targetY` bằng `Double.NaN` và kiểm tra bằng `Double.isNaN()`.
   - Hướng giải quyết (chi tiết):
     1. Trong `Vehicle` đổi kiểu `targetX/targetY` mặc định thành `Double.NaN` (hoặc thêm boolean `hasTargetPos`).
     2. Thay điều kiện `if (vehicle.getTargetX() != 0)` bằng `if (!Double.isNaN(vehicle.getTargetX()))`.
     3. Cập nhật nơi nào set target để gán giá trị thực hoặc `Double.NaN` khi xóa target.

7. So sánh khoảng cách/chiều hướng dễ bị nhầm trong một số hàm
   - Files: `maintainDistance()`, `canEnterIntersection()` trong [src/controller/TrafficController.java](src/controller/TrafficController.java)
   - Mô tả: Các điều kiện dạng `other.getY() > current.getY()` và phép trừ lấy khoảng cách có thể gây sai nếu trục/định nghĩa ahead/behind không được chuẩn hóa.
   - Khắc phục: Viết helper `isAhead(current, other)` theo direction để tránh nhầm lẫn, và dùng giá trị tuyệt đối/so sánh theo hướng nhất quán.
   - Hướng giải quyết (chi tiết):
     1. Thêm helper trong `TrafficController` hoặc tiện ích riêng `PositionUtils.isAhead(current, other)`:
        - Nếu direction == SOUTH: ahead nếu other.y > current.y.
        - Nếu NORTH: ahead nếu other.y < current.y.
        - Nếu EAST: ahead nếu other.x > current.x.
        - Nếu WEST: ahead nếu other.x < current.x.
     2. Thay tất cả điều kiện hướng bằng gọi helper này và tính khoảng cách dọc/trục ngang phù hợp.
     3. Viết test nhỏ kiểm tra các trường hợp trước/sau cho mỗi hướng.

8. Lane change cooldown và kiểm tra va chạm lane change có thể không đủ chặt
   - File: [src/controller/TrafficController.java](src/controller/TrafficController.java)
   - Mô tả: Kiểm tra khoảng cách khi đổi lane chỉ nhìn vào `distance < 120` nhưng không xét tốc độ, hướng, hoặc khu vực an toàn phía trước/sau.
   - Khắc phục: Kiểm tra vùng an toàn theo bounding box tuỳ theo tốc độ; sử dụng khoảng cách trước và sau khác nhau, hoặc kiểm tra projection theo hướng di chuyển.
   - Hướng giải quyết (chi tiết):
     1. Thay `distance(current, other) < SAFE_DISTANCE` bằng kiểm tra bounding box mở rộng theo tốc độ: `safeAhead = speed * TIME_BUFFER + BASE_AHEAD_DISTANCE`, `safeBehind = speed * TIME_BUFFER + BASE_BEHIND_DISTANCE`.
     2. Khi kiểm tra lane target, xét cả `other` ở phía trước và phía sau theo hướng di chuyển, không chỉ Euclidean distance.
     3. Điều chỉnh `LaneChangeCooldown` dựa vào tốc độ và thời gian cần để hoàn thành chuyển lane (`laneChangeTime = laneWidth / lateralSpeed`).

## Hành động đề xuất (ngắn)


Nếu bạn muốn, tôi có thể ngay lập tức tạo các thay đổi mẫu (tạo `Constants.java` và sửa `LaneManager` + `VehicleSpawnManager` + `TrafficController` để dùng hằng số). Bạn muốn tôi tiến hành sửa tự động không?

---
## Ghi chú về việc chạy build/test

- Tôi đã cố gắng chạy build dự án bằng Ant (`ant -f build.xml`) trong thư mục gốc dự án, nhằm biên dịch và thu thập lỗi thực thi.
- Kết quả: lệnh không chạy được trong môi trường hiện tại: `ant` không được tìm thấy (CommandNotFound). Vì vậy tôi **không thể** thu được lỗi biên dịch hoặc test trực tiếp từ môi trường này.

Hướng dẫn để bạn tự chạy và thu log lỗi (Windows PowerShell):

```
cd path\to\TrafficSimulation.VN
ant -f build.xml
```

Nếu bạn sử dụng IDE (NetBeans/Eclipse/IntelliJ) mở project và chạy build để xem lỗi. Khi có lỗi biên dịch hoặc test, gửi cho tôi phần output (hoặc file `build/logs` nếu có) — tôi sẽ phân tích và thêm chi tiết vào file này.

Gợi ý nhanh nếu thấy lỗi khi build:

- Nếu xuất hiện lỗi `package ... does not exist` → kiểm tra classpath / cấu trúc `src` và `build.xml`.
- Nếu xuất hiện lỗi `cannot find symbol` hoặc tương tự → tìm và sửa imports hoặc khai báo biến/method.
- Nếu lỗi runtime khi chạy ứng dụng → gửi stacktrace đầy đủ.


## Hành động đề xuất (ngắn)
- Thêm helper `isAhead()` và chuẩn hoá logic khoảng cách/điều kiện.

Nếu bạn muốn, tôi có thể ngay lập tức tạo các thay đổi mẫu (tạo `Constants.java` và sửa `LaneManager` + `VehicleSpawnManager` + `TrafficController` để dùng hằng số). Bạn muốn tôi tiến hành sửa tự động không?
