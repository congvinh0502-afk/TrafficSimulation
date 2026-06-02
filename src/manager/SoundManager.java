package manager;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

/**
 * SoundManager — quản lý âm thanh toàn cục cho simulation.
 *
 * Cơ chế đếm xe (count-based):
 *   - Mỗi loại xe có một Clip loop riêng.
 *   - Khi xe đầu tiên của loại đó xuất hiện → bắt đầu loop.
 *   - Khi xe cuối cùng của loại đó biến mất → dừng loop.
 *   - Nhiều xe cùng loại chỉ dùng 1 Clip, không bị chồng âm.
 */
public class SoundManager {

    private static SoundManager instance;

    // Clip đã load sẵn, key = tên âm thanh (vd: "car", "ambulance")
    private final Map<String, Clip> soundClips = new HashMap<>();

    // Đếm số xe đang active theo loại
    private final Map<String, Integer> activeCounts = new HashMap<>();

    private boolean soundEnabled = true;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ─────────────────────────────────────────────────────────────
    // KHỞI TẠO ÂM THANH — gọi 1 lần khi app khởi động
    // ─────────────────────────────────────────────────────────────

    /**
     * Load tất cả âm thanh xe từ thư mục resources/sounds/.
     * Gọi một lần duy nhất khi khởi động app (ví dụ trong App.java).
     *
     * Tên file phải khớp: car.wav, motorbike.wav, bicycle.wav,
     *                      ambulance.wav, firetruck.wav
     */
    public void initVehicleSounds() {
        loadSound("car",       "/resoureces/sounds/CarSound.wav");
        loadSound("motorbike", "/resoureces/sounds/MotorbikeSound.wav");
    
        loadSound("ambulance", "/resoureces/sounds/AmbulanceSound.wav");
        loadSound("firetruck", "/resoureces/sounds/FireTruckSound.wav");  
        loadSound("turnsignal", "/resoureces/sounds/TurnSignalSound.wav"); // thêm dòng này
    }

    // ─────────────────────────────────────────────────────────────
    // GỌI TỪ VehicleSpawnManager
    // ─────────────────────────────────────────────────────────────

    /**
     * Gọi khi một xe được spawn vào simulation.
     * Nếu đây là xe đầu tiên của loại này → bắt đầu loop âm thanh.
     */
    public void onVehicleSpawned(String soundKey) {
        if (soundKey == null) return;
        int count = activeCounts.getOrDefault(soundKey, 0) + 1;
        activeCounts.put(soundKey, count);
        if (count == 1) {
            loopSound(soundKey); // xe đầu tiên → bật âm thanh
        }
    }

    /**
     * Gọi khi một xe bị xóa khỏi simulation.
     * Nếu đây là xe cuối cùng của loại này → dừng âm thanh.
     */
    public void onVehicleRemoved(String soundKey) {
        if (soundKey == null) return;
        int count = Math.max(0, activeCounts.getOrDefault(soundKey, 0) - 1);
        activeCounts.put(soundKey, count);
        if (count == 0) {
            stopSound(soundKey); // hết xe → tắt âm thanh
        }
    }

    /**
     * Gọi khi simulation bị reset hoàn toàn (xóa hết xe).
     */
    public void stopAllSounds() {
        for (String key : soundClips.keySet()) {
            stopSound(key);
        }
        activeCounts.clear();
    }

    // ─────────────────────────────────────────────────────────────
    // API NỘI BỘ
    // ─────────────────────────────────────────────────────────────

    private void loadSound(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("[SoundManager] File không tìm thấy: " + path);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            soundClips.put(name, clip);
            System.out.println("[SoundManager] Loaded: " + name);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[SoundManager] Không load được: " + path + " — " + e.getMessage());
        }
    }

    private void loopSound(String name) {
        if (!soundEnabled) return;
        Clip clip = soundClips.get(name);
        if (clip == null) return;
        if (clip.isRunning()) return; // đang chạy rồi, không cần làm gì
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void stopSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // BẬT / TẮT ÂM THANH TOÀN CỤC
    // ─────────────────────────────────────────────────────────────

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            // Tắt hết clip đang chạy
            for (String key : soundClips.keySet()) {
                stopSound(key);
            }
        } else {
            // Bật lại clip nào đang có xe active
            for (Map.Entry<String, Integer> entry : activeCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    loopSound(entry.getKey());
                }
            }
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}