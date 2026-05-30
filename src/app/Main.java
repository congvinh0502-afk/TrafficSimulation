package app;

import view.frame.MainFrame;

/**
 * Điểm khởi động của ứng dụng mô phỏng giao thông đô thị.
 *
 * <p>
 * Chỉ khởi tạo {@link MainFrame} — toàn bộ logic được
 * khởi động từ bên trong lớp đó.
 * </p>
 */
public class Main {

    public static void main(String[] args) {
        new MainFrame();
    }
}