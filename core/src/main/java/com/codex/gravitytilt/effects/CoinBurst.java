package com.codex.gravitytilt.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public final class CoinBurst {
    private static final int PARTICLES = 18;
    private static final float LIFE = 0.55f;

    private final float[] x = new float[PARTICLES];
    private final float[] y = new float[PARTICLES];
    private final float[] vx = new float[PARTICLES];
    private final float[] vy = new float[PARTICLES];
    private float age;

    public CoinBurst(float centerX, float centerY) {
        for (int i = 0; i < PARTICLES; i++) {
            float angle = MathUtils.PI2 * i / PARTICLES + MathUtils.random(-0.16f, 0.16f);
            float speed = MathUtils.random(1.4f, 3.2f);
            x[i] = centerX;
            y[i] = centerY;
            vx[i] = MathUtils.cos(angle) * speed;
            vy[i] = MathUtils.sin(angle) * speed + MathUtils.random(0.25f, 0.85f);
        }
    }

    public void update(float delta) {
        age += delta;
        for (int i = 0; i < PARTICLES; i++) {
            vy[i] -= 5.8f * delta;
            x[i] += vx[i] * delta;
            y[i] += vy[i] * delta;
        }
    }

    public void render(ShapeRenderer shapes) {
        float t = MathUtils.clamp(age / LIFE, 0f, 1f);
        float alpha = 1f - t;
        for (int i = 0; i < PARTICLES; i++) {
            float warm = i % 3 == 0 ? 0.95f : 0.75f;
            shapes.setColor(1f, warm, 0.08f, alpha);
            shapes.circle(x[i], y[i], MathUtils.lerp(0.075f, 0.025f, t), 10);
        }
    }

    public boolean isAlive() {
        return age < LIFE;
    }
}
