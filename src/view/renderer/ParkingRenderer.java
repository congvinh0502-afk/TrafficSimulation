package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

/** Renderer bãi đỗ xe (JavaFX). */
public class ParkingRenderer {

    private static final Color[] COLORS = {
        Color.rgb(40,115,210), Color.rgb(230,185,15), Color.rgb(45,155,85),
        Color.rgb(225,225,230), Color.rgb(35,35,40),  Color.rgb(235,95,30)
    };

    public void drawParkingLotWithCars(GraphicsContext gc, int x, int y, int w, int h) {
        gc.setFill(Color.rgb(75,75,80));      gc.fillRect(x,y,w,h);
        gc.setStroke(Color.LIGHTGRAY);        gc.setLineWidth(1); gc.strokeRect(x,y,w,h);

        int sw=45,sh=65,idx=0;
        Random rng=new Random((long)x*y);

        for (int cx=x+15;cx<x+w-sw;cx+=sw+10) {
            gc.setStroke(Color.rgb(255,255,255,0.71)); gc.setLineWidth(1);
            gc.strokeRect(cx,y+5,sw,sh);
            gc.strokeRect(cx,y+h-sh-5,sw,sh);

            Color tc=COLORS[rng.nextInt(COLORS.length)];
            Color bc=COLORS[rng.nextInt(COLORS.length)];
            if (idx%2==0)   drawParkedCar(gc,cx+(sw-24)/2,y+5+(sh-38)/2,tc,true);
            if (idx%3==0||idx==1) drawParkedCar(gc,cx+(sw-24)/2,(y+h-sh-5)+(sh-38)/2,bc,false);
            idx++;
        }
    }

    private void drawParkedCar(GraphicsContext gc, int x, int y, Color c, boolean headingSouth) {
        int cw=24,ch=38;
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x-2,y+5,4,8,2,2); gc.fillRoundRect(x+cw-2,y+5,4,8,2,2);
        gc.fillRoundRect(x-2,y+ch-13,4,8,2,2); gc.fillRoundRect(x+cw-2,y+ch-13,4,8,2,2);
        gc.setFill(c); gc.fillRoundRect(x,y,cw,ch,6,6);
        gc.setFill(Color.rgb(50,50,60));
        if (headingSouth) gc.fillRect(x+3,y+ch-15,cw-6,8);
        else              gc.fillRect(x+3,y+8,cw-6,8);
    }
}
