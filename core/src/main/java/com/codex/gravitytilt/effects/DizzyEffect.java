package com.codex.gravitytilt.effects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class DizzyEffect {
    private static final int DOTS = 5;

    public void render(ShapeRenderer shapes, Vector2 center, float headAngle, float time) {
        float headCos = MathUtils.cos(headAngle);
        float headSin = MathUtils.sin(headAngle);
        for (int i = 0; i < DOTS; i++) {
            float angle = time * 4.4f + MathUtils.PI2 * i / DOTS;
            float localX = MathUtils.cos(angle) * 0.45f;
            float localY = 0.38f + MathUtils.sin(angle) * 0.12f;
            float x = center.x + localX * headCos - localY * headSin;
            float y = center.y + localX * headSin + localY * headCos;
            float radius = i % 2 == 0 ? 0.07f : 0.05f;
            if (i % 2 == 0) {
                shapes.setColor(1f, 0.12f, 0.08f, 0.95f);
            } else {
                shapes.setColor(0.72f, 0.02f, 0.04f, 0.85f);
            }
            shapes.circle(x, y, radius, 14);
        }
    }
}
