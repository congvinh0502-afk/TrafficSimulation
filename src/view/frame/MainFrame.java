package view.frame;

import config.Constants;
import model.SimulationConfig;
import view.panel.MenuPanel;
import view.panel.SimulationPanel;

import javax.swing.JFrame;

/**
 * Cửa sổ chính của ứng dụng.
 *
 * <p>
 * Quản lý việc chuyển đổi giữa hai màn hình:
 * <ul>
 * <li>{@link MenuPanel} — màn hình cấu hình ban đầu</li>
 * <li>{@link SimulationPanel} — màn hình mô phỏng</li>
 * </ul>
 * </p>
 *
 * <p>
 * Kích thước cố định {@link Constants#WINDOW_WIDTH} ×
 * {@link Constants#WINDOW_HEIGHT}. Không cho phép thay đổi kích thước
 * để đảm bảo tọa độ giao lộ luôn chính xác.
 * </p>
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Smart City Traffic Simulation");
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        showMenu();
        setVisible(true);
    }

    // ==========================================================
    // Chuyển màn hình
    // ==========================================================

    /**
     * Hiển thị màn hình menu.
     * Dùng mảng 1 phần tử để tham chiếu {@code menuPanel} trong lambda.
     */
    private void showMenu() {
        final MenuPanel[] ref = new MenuPanel[1];
        ref[0] = new MenuPanel(() -> {
            SimulationConfig config = ref[0].getConfig();
            startSimulation(config);
        });
        switchTo(ref[0]);
    }

    /**
     * Chuyển sang màn hình mô phỏng với cấu hình đã chọn.
     *
     * @param config cấu hình phiên mô phỏng
     */
    private void startSimulation(SimulationConfig config) {
        switchTo(new SimulationPanel(config));
    }

    /**
     * Thay thế nội dung cửa sổ bằng panel mới.
     *
     * @param panel panel cần hiển thị
     */
    private void switchTo(java.awt.Container panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
}