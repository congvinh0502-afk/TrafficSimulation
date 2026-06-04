package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Renderer thiên nhiên (JavaFX). */
public class NatureRenderer {

    private static final int ROAD_MIN = 460, ROAD_MAX = 740;

    public void drawBeautifulTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(90,50,15));   gc.fillRect(x+10,y+22,6,12);
        gc.setFill(Color.rgb(15,95,15));   gc.fillOval(x,y,26,26);
        gc.setFill(Color.rgb(35,145,35));  gc.fillOval(x+2,y+1,22,22);
        gc.setFill(Color.rgb(75,195,75));  gc.fillOval(x+5,y-1,14,14);
    }

    public void drawPineTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(100,60,20));  gc.fillRect(x+9,y+24,5,10);
        gc.setFill(Color.rgb(20,100,30));
        gc.fillPolygon(new double[]{x,x+26,x+13},new double[]{y+26,y+26,y+10},3);
        gc.setFill(Color.rgb(30,130,45));
        gc.fillPolygon(new double[]{x+4,x+22,x+13},new double[]{y+18,y+18,y+4},3);
        gc.setFill(Color.rgb(50,160,60));
        gc.fillPolygon(new double[]{x+7,x+19,x+13},new double[]{y+12,y+12,y},3);
    }

    public void drawBushTree(GraphicsContext gc, int x, int y) {
        gc.setFill(Color.rgb(110,65,20));  gc.fillRect(x+10,y+20,4,8);
        gc.setFill(Color.rgb(30,110,50));  gc.fillOval(x,y+5,20,18); gc.fillOval(x+8,y+2,20,18); gc.fillOval(x+4,y,18,18);
        gc.setFill(Color.rgb(60,165,80));  gc.fillOval(x+4,y+2,12,10); gc.fillOval(x+10,y,10,10);
        gc.setFill(Color.rgb(240,140,160,0.71)); gc.fillOval(x+6,y+4,5,4); gc.fillOval(x+14,y+2,4,4);
    }

    public void drawTreeRow(GraphicsContext gc, int startX, int startY, int length, boolean horizontal) {
        int sp=55, idx=0;
        if (horizontal) {
            for (int x=startX;x<startX+length;x+=sp) {
                if (x<ROAD_MIN||x>ROAD_MAX) drawTreeByIndex(gc,x,startY+6,idx++);
            }
        } else {
            for (int y=startY;y<startY+length;y+=sp) {
                if (y<ROAD_MIN||y>ROAD_MAX) drawTreeByIndex(gc,startX+6,y,idx++);
            }
        }
    }

    public void drawTreeByIndex(GraphicsContext gc, int x, int y, int idx) {
        switch (idx%3) {
            case 0: drawBeautifulTree(gc,x,y); break;
            case 1: drawPineTree(gc,x,y);      break;
            case 2: drawBushTree(gc,x,y);      break;
        }
    }

    public void drawParkWithPond(GraphicsContext gc, int x, int y, int w, int h) {
        gc.setFill(Color.rgb(50,160,70));   gc.fillRect(x,y,w,h);
        gc.setFill(Color.rgb(55,120,190));  gc.fillOval(x+w/4,y+h/4,w/2,h/2);
        gc.setStroke(Color.rgb(120,110,100)); gc.setLineWidth(3);
        gc.strokeOval(x+w/4,y+h/4,w/2,h/2); gc.setLineWidth(1);
        gc.setFill(Color.rgb(140,75,25));
        gc.fillRect(x+w/4-15,y+h/2,10,20);
        gc.fillRect(x+3*w/4+5,y+h/2,10,20);
        drawBeautifulTree(gc,x+10,y+10);
        drawPineTree(gc,x+w-35,y+10);
        drawBushTree(gc,x+10,y+h-35);
        drawBeautifulTree(gc,x+w-35,y+h-35);
    }
}
