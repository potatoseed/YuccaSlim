package com.yuccaworld.yuccaslim.model;

public class DailyOld {
    private int slimScore;
    private int targetFat;
    private int targetHeavy;

    public DailyOld() {
    }
    public DailyOld(int slimScore, int targetFat, int targetHeavy) {
        this.slimScore = slimScore;
        this.targetFat = targetFat;
        this.targetHeavy = targetHeavy;
    }

    public int getSlimScore() {
        return slimScore;
    }

    public void setSlimScore(int slimScore) {
        this.slimScore = slimScore;
    }

    public int getTargetFat() {
        return targetFat;
    }

    public void setTargetFat(int targetFat) {
        this.targetFat = targetFat;
    }

    public int getTargetHeavy() {
        return targetHeavy;
    }

    public void setTargetHeavy(int targetHeavy) {
        this.targetHeavy = targetHeavy;
    }
}
