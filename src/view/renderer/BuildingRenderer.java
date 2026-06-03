package view.renderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Renderer công trình (JavaFX). */
public class BuildingRenderer {

    public void drawSkyscraper(GraphicsContext gc, int x, int y) {
        int w = 110, h = 150;
        gc.setFill(Color.rgb(40, 50, 40, 0.20)); gc.fillRect(x+10,y+10,w,h);
        gc.setFill(Color.rgb(45,60,80));          gc.fillRect(x,y,w,h);
        gc.setFill(Color.rgb(75,140,210));        gc.fillRect(x+15,y,w-30,h);
        int ww=8,wh=12;
        for (int r=0;r<7;r++) for (int c=0;c<5;c++) {
            boolean lit=(r+c)%3==0||(r==2&&c==4);
            gc.setFill(lit?Color.rgb(255,235,130):Color.rgb(110,180,240));
            gc.fillRect(x+20+c*(ww+8),y+15+r*(wh+7),ww,wh);
        }
        gc.setFill(Color.LIGHTGRAY); gc.fillRect(x+w/2-2,y-20,4,20);
        gc.setFill(Color.RED);       gc.fillOval(x+w/2-4,y-24,8,8);
    }

    public void drawLuxuryHouse(GraphicsContext gc, int x, int y) {
        int bw=85,bh=75;
        gc.setFill(Color.rgb(40,65,40,0.24)); gc.fillRect(x+6,y+6,bw,bh);
        gc.setFill(Color.rgb(245,240,230));   gc.fillRect(x,y,bw,bh);
        gc.setFill(Color.rgb(210,105,30));    gc.fillRect(x+12,y+10,bw-24,bh-20);
        int rx=x+18,ry=y+16,rw=bw-36,rh=bh-32;
        gc.setFill(Color.rgb(45,85,150));     gc.fillRect(rx,ry,rw,rh);
        int ccx=rx+rw/2,ccy=ry+rh/2;
        gc.setStroke(Color.rgb(25,50,100)); gc.setLineWidth(1.5);
        gc.strokeLine(rx,ry,ccx,ccy); gc.strokeLine(rx+rw,ry,ccx,ccy);
        gc.strokeLine(rx,ry+rh,ccx,ccy); gc.strokeLine(rx+rw,ry+rh,ccx,ccy);
        gc.setLineWidth(1);
        gc.setFill(Color.rgb(135,206,250,0.71)); gc.fillRect(ccx-4,ccy-3,8,6);
    }

    public void drawModernFactory(GraphicsContext gc, int x, int y) {
        int w=120,h=75;
        gc.setFill(Color.rgb(40,50,40,0.20)); gc.fillRect(x+6,y+6,w,h);
        gc.setFill(Color.rgb(125,135,145));   gc.fillRect(x,y+15,w-25,h-15);
        gc.setFill(Color.rgb(100,110,120));
        double[] rxs={x,x+25,x+25,x+50,x+50,x+75,x+75,x};
        double[] rys={y+15,y,y+15,y,y+15,y,y+15,y+15};
        gc.fillPolygon(rxs,rys,8);
        gc.setFill(Color.rgb(190,195,200)); gc.fillRect(x+15,y+40,35,35);
        gc.setFill(Color.rgb(150,60,45));   gc.fillRect(x+w-20,y-10,14,h+10);
        gc.setFill(Color.WHITE);            gc.fillRect(x+w-20,y,14,6); gc.fillRect(x+w-20,y+20,14,6);
    }

    public void drawLuxuryRestaurant(GraphicsContext gc, int x, int y) {
        int w=100,h=80;
        gc.setFill(Color.rgb(40,50,40,0.24)); gc.fillRect(x+6,y+6,w,h);
        gc.setFill(Color.rgb(250,243,224));   gc.fillRect(x,y,w,h);
        gc.setFill(Color.rgb(160,35,35));     gc.fillRect(x+10,y+5,w-20,16);
        gc.setFill(Color.rgb(240,190,60));    gc.fillRect(x+25,y+11,w-50,4);
        gc.setFill(Color.rgb(100,180,220,0.71));
        gc.fillRoundRect(x+15,y+35,22,35,10,10);
        gc.fillRoundRect(x+w-37,y+35,22,35,10,10);
        gc.setFill(Color.rgb(120,70,30)); gc.fillRect(x+45,y+42,12,28);
        for (int i=5;i<w-5;i+=10) {
            gc.setFill((i/10)%2==0?Color.rgb(190,40,40):Color.WHITE);
            gc.fillRect(x+i,y+21,10,10);
        }
    }
}
