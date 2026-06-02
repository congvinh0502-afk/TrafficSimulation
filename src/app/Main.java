package app;

import manager.SoundManager;
import view.frame.MainFrame;

public class Main {

    public static void main(String[] args) {

        SoundManager.getInstance().initVehicleSounds(); 

        new MainFrame();

    }
}