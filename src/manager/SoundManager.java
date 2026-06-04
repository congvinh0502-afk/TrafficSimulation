package manager;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.vehicle.Ambulance;
import model.vehicle.Car;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;

/**
 * Quản lý âm thanh cho từng loại phương tiện.
 * Dùng javax.sound.sampled — không cần thêm dependency.
 *
 * <p>Cách dùng:
 * <pre>
 * // Mỗi frame:
 * SoundManager.getInstance().updateVehicleSound(vehicle);
 *
 * // Khi xe bị xóa:
 * SoundManager.getInstance().onVehicleRemoved(vehicle);
 * </pre>
 * </p>
 */
public class SoundManager {

    // ------------------------------------------------------------------
    // Enum key
    // ------------------------------------------------------------------
    public enum SoundKey {
        AMBULANCE, CAR, FIRETRUCK, MOTORBIKE, TURN_SIGNAL
    }

    // ------------------------------------------------------------------
    // Singleton
    // ------------------------------------------------------------------
    private static SoundManager instance;

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ------------------------------------------------------------------
    // Dữ liệu nội bộ
    // ------------------------------------------------------------------
    private final Map<SoundKey, Clip> clips       = new EnumMap<>(SoundKey.class);
    private final Map<SoundKey, Integer> activeCount = new EnumMap<>(SoundKey.class);
    
    // Thêm 2 map quản lý volume và mute riêng biệt
    private final Map<SoundKey, Float> volumes    = new EnumMap<>(SoundKey.class);
    private final Map<SoundKey, Boolean> muted    = new EnumMap<>(SoundKey.class);

    private SoundManager() {
        loadClip(SoundKey.AMBULANCE,   "AmbulanceSound.wav");
        loadClip(SoundKey.CAR,         "CarSound.wav");
        loadClip(SoundKey.FIRETRUCK,   "FireTruckSound.wav");
        loadClip(SoundKey.MOTORBIKE,   "MotorbikeSound.wav");
        loadClip(SoundKey.TURN_SIGNAL, "TurnSignalSound.wav");

        // Khởi tạo các giá trị mặc định cho từng SoundKey
        for (SoundKey key : SoundKey.values()) {
            activeCount.put(key, 0);
            volumes.put(key, 0.6f);   // thêm
            muted.put(key, false);    // thêm
        }
    }

    // ------------------------------------------------------------------
    // Load — thử 2 path để tương thích mọi cấu trúc project
    // ------------------------------------------------------------------
    private void loadClip(SoundKey key, String fileName) {
        String[] candidates = {
            "/sound/"           + fileName,
            "/resources/sound/" + fileName
        };

        for (String path : candidates) {
            try {
                URL url = getClass().getResource(path);
                if (url == null) continue;

                AudioInputStream ais = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setVolume(clip, 0.6f);
                clips.put(key, clip);
                System.out.println("[SoundManager] Loaded: " + path);
                return;

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("[SoundManager] Lỗi load " + path + ": " + e.getMessage());
            }
        }

        System.err.println("[SoundManager] Không tìm thấy: " + fileName);
    }

    /** Chỉnh volume cho Clip (0.0 – 1.0). */
    private void setVolume(Clip clip, float volume) {
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Chuyển 0‒1 → dB
            float dB = (float) (Math.log10(Math.max(volume, 0.0001f)) * 20.0);
            control.setValue(Math.max(control.getMinimum(), Math.min(dB, control.getMaximum())));
        } catch (IllegalArgumentException ignored) { }
    }

    // ------------------------------------------------------------------
    // API chính — gọi mỗi frame từ VehicleUpdatePipeline
    // ------------------------------------------------------------------

    /**
     * Cập nhật âm thanh dựa trên trạng thái xe.
     */
    public void updateVehicleSound(Vehicle vehicle) {
        SoundKey engineKey = getEngineKey(vehicle);
        if (engineKey == null) return; // Bicycle — không có âm thanh

        if (vehicle.isStopped()) {
            decrementAndStop(engineKey);
        } else {
            incrementAndPlay(engineKey);
        }

        // Turn signal: chỉ phát 1 lần khi vừa bắt đầu rẽ
        if (vehicle.isTurning() && !vehicle.hasTurned()) {
            playOnce(SoundKey.TURN_SIGNAL);
        }
    }

    /**
     * Gọi khi xe bị xóa khỏi simulation.
     */
    public void onVehicleRemoved(Vehicle vehicle) {
        SoundKey engineKey = getEngineKey(vehicle);
        if (engineKey == null) return;
        if (!vehicle.isStopped()) {
            decrementAndStop(engineKey);
        }
    }

    // ------------------------------------------------------------------
    // Điều khiển volume / mute tổng thể và riêng lẻ
    // ------------------------------------------------------------------

    public void setMasterVolume(float volume) {
        clips.values().forEach(c -> setVolume(c, volume));
    }

    public void muteAll() {
        clips.values().forEach(Clip::stop);
        activeCount.replaceAll((k, v) -> 0);
    }

    /** Chỉnh volume riêng cho 1 loại xe (0.0 – 1.0) */
    public void setVolume(SoundKey key, float volume) {
        volumes.put(key, Math.max(0f, Math.min(1f, volume)));
        Clip clip = clips.get(key);
        if (clip != null && !muted.getOrDefault(key, false)) {
            setVolume(clip, volume);
        }
    }

    /** Bật/tắt âm thanh 1 loại xe */
    public void setMuted(SoundKey key, boolean mute) {
        muted.put(key, mute);
        Clip clip = clips.get(key);
        if (clip == null) return;
        if (mute) {
            clip.stop();
        } else {
            // Chỉ resume nếu đang có xe loại đó đang chạy
            if (activeCount.getOrDefault(key, 0) > 0) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            setVolume(clip, volumes.getOrDefault(key, 0.6f));
        }
    }

    public boolean isMuted(SoundKey key) {
        return muted.getOrDefault(key, false);
    }

    public float getVolume(SoundKey key) {
        return volumes.getOrDefault(key, 0.6f);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private void incrementAndPlay(SoundKey key) {
        Clip clip = clips.get(key);
        if (clip == null) return;

        activeCount.merge(key, 1, Integer::sum);
        
        // Check trạng thái mute trước khi phát
        if (muted.getOrDefault(key, false)) return;
        
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void decrementAndStop(SoundKey key) {
        Clip clip = clips.get(key);
        if (clip == null) return;

        int count = Math.max(0, activeCount.getOrDefault(key, 0) - 1);
        activeCount.put(key, count);

        if (count == 0) clip.stop();
    }

    private void playOnce(SoundKey key) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private SoundKey getEngineKey(Vehicle vehicle) {
        if (vehicle instanceof Ambulance) return SoundKey.AMBULANCE;
        if (vehicle instanceof FireTruck)  return SoundKey.FIRETRUCK;
        if (vehicle instanceof Motorbike)  return SoundKey.MOTORBIKE;
        if (vehicle instanceof Car)        return SoundKey.CAR;
        return null; // Bicycle
    }
}