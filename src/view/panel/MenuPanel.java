package view.panel;

import model.SimulationConfig;
import model.intersection.IntersectionType;
import util.TrafficDensity;

import javax.swing.*;
import java.awt.*;

/**
 * Panel menu — cho phép người dùng cấu hình phiên mô phỏng.
 *
 * <p>
 * Thu thập 5 thông số:
 * <ul>
 * <li>Loại ngã rẽ (Three/Four/Five Way)</li>
 * <li>Chế độ đèn (AUTO / MANUAL)</li>
 * <li>Kiểu đếm ngược đèn</li>
 * <li>Số lượng xe tối đa (slider 1–100)</li>
 * <li>Mật độ giao thông (LOW / MEDIUM / HIGH)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Khi người dùng nhấn START, callback {@code onStart} được gọi.
 * Lấy cấu hình đã chọn qua {@link #getConfig()}.
 * </p>
 */
public class MenuPanel extends JPanel {

    private final JComboBox<String> intersectionBox;
    private final JComboBox<String> modeBox;
    private final JComboBox<String> lightBox;
    private final JSlider vehicleSlider;
    private final JComboBox<String> densityBox;

    // Màu sắc giao diện
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    /**
     * Tạo panel menu.
     *
     * @param onStart callback chạy khi người dùng nhấn START
     */
    public MenuPanel(Runnable onStart) {
        setLayout(null);
        setBackground(BG_COLOR);

        // Tiêu đề
        JLabel title = createLabel("SMART TRAFFIC SIMULATION", TITLE_FONT, 350, 50, 500, 40);
        add(title);

        // Loại ngã rẽ
        add(createLabel("Intersection:", 350, 150));
        intersectionBox = new JComboBox<>(new String[] { "Three Way", "Four Way", "Five Way" });
        intersectionBox.setBounds(500, 150, 200, 30);
        add(intersectionBox);

        // Chế độ đèn
        add(createLabel("Traffic Mode:", 350, 210));
        modeBox = new JComboBox<>(new String[] { "AUTO", "MANUAL" });
        modeBox.setBounds(500, 210, 200, 30);
        add(modeBox);

        // Kiểu đếm ngược
        add(createLabel("Light Type:", 350, 270));
        lightBox = new JComboBox<>(new String[] { "NO COUNTDOWN", "ALWAYS COUNTDOWN", "COUNT <= 10" });
        lightBox.setBounds(500, 270, 200, 30);
        add(lightBox);

        // Số lượng xe
        add(createLabel("Vehicle Count:", 350, 340));
        vehicleSlider = new JSlider(1, 100, 20);
        vehicleSlider.setBounds(500, 335, 200, 50);
        vehicleSlider.setMajorTickSpacing(10);
        vehicleSlider.setPaintTicks(true);
        vehicleSlider.setPaintLabels(true);
        vehicleSlider.setOpaque(false);
        add(vehicleSlider);

        // Mật độ giao thông
        add(createLabel("Traffic Density:", 350, 405));
        densityBox = new JComboBox<>(new String[] { "LOW", "MEDIUM", "HIGH" });
        densityBox.setBounds(500, 405, 200, 30);
        add(densityBox);

        // Nút bắt đầu
        JButton startButton = new JButton("START SIMULATION");
        startButton.setBounds(450, 490, 250, 50);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(e -> onStart.run());
        add(startButton);
    }

    // ----------------------------------------------------------
    // Lấy cấu hình từ form
    // ----------------------------------------------------------

    /**
     * Đọc các lựa chọn hiện tại trên form và trả về {@link SimulationConfig}.
     *
     * @return cấu hình phiên mô phỏng
     */
    public SimulationConfig getConfig() {
        return new SimulationConfig(
                parseIntersectionType(),
                (String) modeBox.getSelectedItem(),
                (String) lightBox.getSelectedItem(),
                vehicleSlider.getValue(),
                parseDensity());
    }

    private IntersectionType parseIntersectionType() {
        switch ((String) intersectionBox.getSelectedItem()) {
            case "Three Way":
                return IntersectionType.THREE_WAY;
            case "Five Way":
                return IntersectionType.FIVE_WAY;
            default:
                return IntersectionType.FOUR_WAY;
        }
    }

    private TrafficDensity parseDensity() {
        switch ((String) densityBox.getSelectedItem()) {
            case "LOW":
                return TrafficDensity.LOW;
            case "HIGH":
                return TrafficDensity.HIGH;
            default:
                return TrafficDensity.MEDIUM;
        }
    }

    // ----------------------------------------------------------
    // Tiện ích tạo label
    // ----------------------------------------------------------

    private JLabel createLabel(String text, int x, int y) {
        return createLabel(text, LABEL_FONT, x, y, 150, 30);
    }

    private JLabel createLabel(String text, Font font, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(font);
        label.setBounds(x, y, w, h);
        return label;
    }
}