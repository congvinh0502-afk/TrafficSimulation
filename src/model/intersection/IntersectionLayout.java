package model.intersection;

import config.Constants;
import util.Direction;
import util.Lane;

import java.util.*;

public class IntersectionLayout {

    public static class Arm {
        public final Direction direction;
        public final int leftLaneCenterX;
        public final int leftLaneCenterY;
        public final int rightLaneCenterX;
        public final int rightLaneCenterY;
        public final double spawnX;
        public final double spawnY;

        public Arm(Direction dir,
                   int leftX, int leftY,
                   int rightX, int rightY,
                   double spawnX, double spawnY) {
            this.direction        = dir;
            this.leftLaneCenterX  = leftX;
            this.leftLaneCenterY  = leftY;
            this.rightLaneCenterX = rightX;
            this.rightLaneCenterY = rightY;
            this.spawnX           = spawnX;
            this.spawnY           = spawnY;
        }
    }

    public static class TurnZone {
        public final int left, right, top, bottom;

        public TurnZone(int left, int right, int top, int bottom) {
            this.left   = left;
            this.right  = right;
            this.top    = top;
            this.bottom = bottom;
        }

        public boolean contains(double x, double y, double hw, double hh) {
            return x + hw > left  && x - hw < right
                && y + hh > top   && y - hh < bottom;
        }
    }

    private final int cx, cy;
    private final Map<Direction, Arm> arms;
    private final TurnZone turnZone;
    private final int recoverLeft, recoverRight, recoverTop, recoverBottom;
    private final int checkLeft,   checkRight,   checkTop,   checkBottom;

    private IntersectionLayout(int cx, int cy,
                                Map<Direction, Arm> arms,
                                TurnZone turnZone,
                                int recoverLeft,  int recoverRight,
                                int recoverTop,   int recoverBottom,
                                int checkLeft,    int checkRight,
                                int checkTop,     int checkBottom) {
        this.cx = cx;
        this.cy = cy;
        this.arms         = Collections.unmodifiableMap(arms);
        this.turnZone     = turnZone;
        this.recoverLeft  = recoverLeft;
        this.recoverRight = recoverRight;
        this.recoverTop   = recoverTop;
        this.recoverBottom= recoverBottom;
        this.checkLeft    = checkLeft;
        this.checkRight   = checkRight;
        this.checkTop     = checkTop;
        this.checkBottom  = checkBottom;
    }

    public int getCx() { return cx; }
    public int getCy() { return cy; }

    public Collection<Arm> getArms()    { return arms.values(); }
    public Arm             getArm(Direction d) { return arms.get(d); }
    public boolean         hasDirection(Direction d) { return arms.containsKey(d); }

    public List<Direction> getDirections() {
        List<Direction> result = new ArrayList<>();
        for (Direction d : arms.keySet()) {
            if (d.name().startsWith("FW_OUT"))
                continue;
            if (d == Direction.NORTH && arms.containsKey(Direction.FW_IN_54))
                continue;
            result.add(d);
        }
        return result;
    }

    public boolean isFiveWay() {
        return arms.containsKey(Direction.FW_IN_54);
    }

    public TurnZone getTurnZone() { return turnZone; }

    public int getLaneCenterX(Direction d, Lane lane) {
        Arm arm = arms.get(d);
        if (arm == null) return cx;
        return lane == Lane.LEFT ? arm.leftLaneCenterX : arm.rightLaneCenterX;
    }

    public int getLaneCenterY(Direction d, Lane lane) {
        Arm arm = arms.get(d);
        if (arm == null) return cy;
        return lane == Lane.LEFT ? arm.leftLaneCenterY : arm.rightLaneCenterY;
    }

    public double getSpawnX(Direction d) {
        Arm arm = arms.get(d);
        return arm != null ? arm.spawnX : Constants.SPAWN_OFFSCREEN_NEGATIVE;
    }

    public double getSpawnY(Direction d) {
        Arm arm = arms.get(d);
        return arm != null ? arm.spawnY : Constants.SPAWN_OFFSCREEN_NEGATIVE;
    }

    public boolean isOutsideRecover(double x, double y) {
        return x < recoverLeft || x > recoverRight
             || y < recoverTop  || y > recoverBottom;
    }

    public boolean isInsideCheck(double x, double y) {
        return x > checkLeft && x < checkRight
            && y > checkTop  && y < checkBottom;
    }

    public int getCheckLeft()   { return checkLeft; }
    public int getCheckRight()  { return checkRight; }
    public int getCheckTop()    { return checkTop; }
    public int getCheckBottom() { return checkBottom; }

    private static void addCardinalArms(Map<Direction, Arm> arms, int cx, int cy) {
        int lw   = Constants.LANE_WIDTH;
        int half = lw / 2;

        int nRightX = cx + half;
        int nLeftX  = cx + half + lw;
        arms.put(Direction.NORTH, new Arm(Direction.NORTH,
                nLeftX,  0,
                nRightX, 0,
                nRightX, Constants.SPAWN_OFFSCREEN_POSITIVE));

        int sRightX = cx - half;
        int sLeftX  = cx - half - lw;
        arms.put(Direction.SOUTH, new Arm(Direction.SOUTH,
                sLeftX,  0,
                sRightX, 0,
                sRightX, Constants.SPAWN_OFFSCREEN_NEGATIVE));

        int eRightY = cy - half;
        int eLeftY  = cy - half - lw;
        arms.put(Direction.EAST, new Arm(Direction.EAST,
                0, eLeftY,
                0, eRightY,
                Constants.SPAWN_OFFSCREEN_NEGATIVE, eRightY));

        int wRightY = cy + half;
        int wLeftY  = cy + half + lw;
        arms.put(Direction.WEST, new Arm(Direction.WEST,
                0, wLeftY,
                0, wRightY,
                Constants.SPAWN_OFFSCREEN_POSITIVE, wRightY));
    }

    public static IntersectionLayout fourWay(int cx, int cy) {
        int lw     = Constants.LANE_WIDTH;
        int tzPad  = 15;
        int recPad = 50;
        int buf    = 70;

        Map<Direction, Arm> arms = new LinkedHashMap<>();
        addCardinalArms(arms, cx, cy);

        TurnZone tz = new TurnZone(
                cx - lw - tzPad, cx + lw + tzPad,
                cy - lw - tzPad, cy + lw + tzPad);

        return new IntersectionLayout(cx, cy, arms, tz,
                cx - lw - recPad, cx + lw + recPad,
                cy - lw - recPad, cy + lw + recPad,
                cx - lw - buf,    cx + lw + buf,
                cy - lw - buf,    cy + lw + buf);
    }

    public static IntersectionLayout threeWay(int cx, int cy) {
        int lw = Constants.LANE_WIDTH;
        int half = lw / 2;
        int tzPad = 15;
        int recPad = 50;
        int buf = 70;

        Map<Direction, Arm> arms = new LinkedHashMap<>();

        // NORTH arm: Vẫn phải giữ để có làn định tuyến khi xe thoái ra (đi lên trên). 
        // Tuy nhiên đặt spawn thành NaN để CHẶN việc sinh xe từ làn dưới cùng.
        int nRightX = cx + half;
        int nLeftX  = cx + half + lw;
        arms.put(Direction.NORTH, new Arm(Direction.NORTH,
                nLeftX,  0,
                nRightX, 0,
                Double.NaN, Double.NaN));

        // SOUTH arm: xe đi xuống (spawn từ trên xuống)
        int sRightX = cx - half;
        int sLeftX = cx - half - lw;
        arms.put(Direction.SOUTH, new Arm(Direction.SOUTH,
                sLeftX, 0,
                sRightX, 0,
                sRightX, Constants.SPAWN_OFFSCREEN_NEGATIVE));

        int eRightY = cy - half;
        int eLeftY = cy - half - lw;
        arms.put(Direction.EAST, new Arm(Direction.EAST,
                0, eLeftY,
                0, eRightY,
                Constants.SPAWN_OFFSCREEN_NEGATIVE, eRightY));

        int wRightY = cy + half;
        int wLeftY = cy + half + lw;
        arms.put(Direction.WEST, new Arm(Direction.WEST,
                0, wLeftY,
                0, wRightY,
                Constants.SPAWN_OFFSCREEN_POSITIVE, wRightY));

        TurnZone tz = new TurnZone(
                cx - lw - tzPad, cx + lw + tzPad,
                cy - lw - tzPad, cy + lw + tzPad);

        return new IntersectionLayout(cx, cy, arms, tz,
                cx - lw - recPad, cx + lw + recPad,
                cy - lw - recPad, cy + lw + recPad,
                cx - lw - buf, cx + lw + buf,
                cy - lw - buf, cy + lw + buf);
    }

    public static IntersectionLayout fiveWay(int cx, int cy) {
        int recPad = 60;
        int buf = 80;
        int roundaboutR = 170;
        Map<Direction, Arm> arms = new LinkedHashMap<>();

        double[] branchAngles = { 270, 342, 54, 126, 198 };
        Direction[] inboundDirs = {
                Direction.SOUTH, Direction.FW_IN_342, Direction.FW_IN_54,
                Direction.FW_IN_126, Direction.FW_IN_198
        };
        Direction[] outboundDirs = {
                Direction.NORTH, Direction.FW_OUT_342, Direction.FW_OUT_54,
                Direction.FW_OUT_126, Direction.FW_OUT_198
        };

        double spawnDist = 450.0;

        for (int i = 0; i < 5; i++) {
            double branchRad = Math.toRadians(branchAngles[i]);
            double spawnX = cx + Math.cos(branchRad) * spawnDist;
            double spawnY = cy + Math.sin(branchRad) * spawnDist;
            arms.put(inboundDirs[i], new Arm(inboundDirs[i], 0, 0, 0, 0, spawnX, spawnY));
            arms.put(outboundDirs[i], new Arm(outboundDirs[i], 0, 0, 0, 0,
                    cx + Math.cos(branchRad) * (spawnDist + 200),
                    cy + Math.sin(branchRad) * (spawnDist + 200)));
        }

        TurnZone tz = new TurnZone(cx - roundaboutR, cx + roundaboutR,
                cy - roundaboutR, cy + roundaboutR);

        return new IntersectionLayout(cx, cy, arms, tz,
                cx - roundaboutR - recPad, cx + roundaboutR + recPad,
                cy - roundaboutR - recPad, cy + roundaboutR + recPad,
                cx - roundaboutR - buf, cx + roundaboutR + buf,
                cy - roundaboutR - buf, cy + roundaboutR + buf);
    }
}