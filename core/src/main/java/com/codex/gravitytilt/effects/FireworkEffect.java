package com.codex.gravitytilt.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public final class FireworkEffect {
    private static final int BURSTS = 4;
    private static final int PARTICLES_PER_BURST = 22;
    private static final float BURST_GAP = 0.18f;
    private static final float PARTICLE_LIFE = 1.05f;

    private final float[][] x = new float[BURSTS][PARTICLES_PER_BURST];
    private final float[][] y = new float[BURSTS][PARTICLES_PER_BURST];
    private final float[][] vx = new float[BURSTS][PARTICLES_PER_BURST];
    private final float[][] vy = new float[BURSTS][PARTICLES_PER_BURST];
    private final float[] startTime = new float[BURSTS];
    private final Color[] colors = {
            new Color(1f, 0.18f, 0.1f, 1f),
            new Color(0.18f, 0.75f, 1f, 1f),
            new Color(1f, 0.88f, 0.12f, 1f),
            new Color(0.55f, 1f, 0.22f, 1f)
    };
    private float age;

    public FireworkEffect(float centerX, float centerY) {
        for (int burst = 0; burst < BURSTS; burst++) {
            startTime[burst] = burst * BURST_GAP;
            float burstX = centerX + MathUtils.random(-1.2f, 1.2f);
            float burstY = centerY + MathUtils.random(0.4f, 1.4f);
            for (int i = 0; i < PARTICLES_PER_BURST; i++) {
                float angle = MathUtils.PI2 * i / PARTICLES_PER_BURST + MathUtils.random(-0.11f, 0.11f);
                float speed = MathUtils.random(1.8f, 4.2f);
                x[burst][i] = burstX;
                y[burst][i] = burstY;
                vx[burst][i] = MathUtils.cos(angle) * speed;
                vy[burst][i] = MathUtils.sin(angle) * speed + 0.8f;
            }
        }
    }

    public void update(float delta) {
        age += delta;
        for (int burst = 0; burst < BURSTS; burst++) {
            if (age < startTime[burst]) {
                continue;
            }
            for (int i = 0; i < PARTICLES_PER_BURST; i++) {
                vy[burst][i] -= 4.2f * delta;
                vx[burst][i] *= 0.992f;
                x[burst][i] += vx[burst][i] * delta;
                y[burst][i] += vy[burst][i] * delta;
            }
        }
    }

    public void render(ShapeRenderer shapes) {
        for (int burst = 0; burst < BURSTS; burst++) {
            float localAge = age - startTime[burst];
            if (localAge < 0f || localAge > PARTICLE_LIFE) {
                continue;
            }
            float t = MathUtils.clamp(localAge / PARTICLE_LIFE, 0f, 1f);
            Color color = colors[burst % colors.length];
            for (int i = 0; i < PARTICLES_PER_BURST; i++) {
                float alpha = 1f - t;
                shapes.setColor(color.r, color.g, color.b, alpha);
                shapes.circle(x[burst][i], y[burst][i], MathUtils.lerp(0.08f, 0.025f, t), 10);
            }
        }
    }

    public boolean isAlive() {
        return age < startTime[BURSTS - 1] + PARTICLE_LIFE;
    }
}
