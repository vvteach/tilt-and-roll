package com.codex.gravitytilt.levels;

public final class LevelDefinition {
    private final float startX;
    private final float startY;
    private final float[][] platforms;
    private final float[][] coins;
    private final float[][] hazards;
    private final float[][] springs;
    private final float[] goal;

    public LevelDefinition(float startX, float startY, float[][] platforms, float[][] coins, float[][] hazards,
            float[][] springs, float[] goal) {
        this.startX = startX;
        this.startY = startY;
        this.platforms = platforms;
        this.coins = coins;
        this.hazards = hazards;
        this.springs = springs;
        this.goal = goal;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float[][] getPlatforms() {
        return platforms;
    }

    public float[][] getCoins() {
        return coins;
    }

    public float[][] getHazards() {
        return hazards;
    }

    public float[][] getSprings() {
        return springs;
    }

    public float[] getGoal() {
        return goal;
    }
}
