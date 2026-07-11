package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

abstract class CircleGameObject extends GameObject {
    private final float radius;
    private final int segments;

    CircleGameObject(Body body, float radius, int segments) {
        super(body);
        this.radius = radius;
        this.segments = segments;
    }

    @Override
    public void render(ShapeRenderer shapes) {
        Vector2 p = getBody().getPosition();
        shapes.circle(p.x, p.y, radius, segments);
    }

    protected float getRadius() {
        return radius;
    }
}
