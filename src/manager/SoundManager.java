package manager;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    
    // Khai báo 6 clip âm thanh
    private static Clip ambulanceClip;
    private static Clip fireTruckClip;
    private static Clip signalClip;
    private static Clip carClip;
    private static Clip motorbikeClip;
    private static Clip hornClip;

    public static void init() {
        ambulanceClip = loadClip("res/audio/ambulance.wav");
        fireTruckClip = loadClip("res/audio/firetruck.wav");
        signalClip    = loadClip("res/audio/signal.wav");
        carClip       = loadClip("res/audio/car.wav");
        motorbikeClip = loadClip("res/audio/motorbike.wav");
        hornClip      = loadClip("res/audio/horn.wav");
    }

    private static Clip loadClip(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                return clip;
            } else {
                System.out.println("Không tìm thấy file: " + path);
            }
        } catch (Exception e) {
            System.out.println("Lỗi load file âm thanh: " + path);
        }
        return null;
    }

    // ==========================================
    // CÁC HÀM PHÁT ÂM THANH 1 LẦN (CÒI XIN VƯỢT)
    // ==========================================

    public static void playCarHorn() {
        playOnce(carClip);
    }

    public static void playMotorbikeHorn() {
        playOnce(motorbikeClip);
    }

    public static void playGeneralHorn() {
        playOnce(hornClip);
    }

    private static void playOnce(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0); 
        clip.start();
    }

    // ==========================================
    // CÁC HÀM PHÁT ÂM THANH LIÊN TỤC (LẶP)
    // ==========================================

    public static void updateAmbulance(boolean active) {
        loopClip(ambulanceClip, active);
    }

    public static void updateFireTruck(boolean active) {
        loopClip(fireTruckClip, active);
    }

    public static void updateSignal(boolean active) {
        loopClip(signalClip, active);
    }

    private static void loopClip(Clip clip, boolean active) {
        if (clip == null) return;
        if (active && !clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else if (!active && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }
}