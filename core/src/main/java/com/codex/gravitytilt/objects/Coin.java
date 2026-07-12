package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.codex.gravitytilt.physics.BodyFactory;

public final class Coin extends CircleGameObject {
    private static final float RADIUS = 0.27f;
    private static final float INNER_RADIUS = 0.22f;
    private static final float ONE_OFFSET_X = 0.02f;

    public Coin(World world, float x, float y) {
        super(BodyFactory.createStaticCircleSensor(world, x, y, RADIUS), RADIUS, 24);
        attachToFixtures();
    }

    @Override
    public void render(ShapeRenderer shapes) {
        Vector2 p = getBody().getPosition();
        shapes.setColor(0.58f, 0.38f, 0.05f, 1f);
        shapes.circle(p.x, p.y, RADIUS, 24);
        shapes.setColor(0.98f, 0.79f, 0.23f, 1f);
        shapes.circle(p.x, p.y, INNER_RADIUS, 24);
        drawOne(shapes);
    }

    private void drawOne(ShapeRenderer shapes) {
        Vector2 p = getBody().getPosition();
        float x = p.x + ONE_OFFSET_X;
        shapes.setColor(0.34f, 0.22f, 0.06f, 1f);
        shapes.rect(x - 0.035f, p.y - 0.13f, 0.07f, 0.25f);
        shapes.rect(x - 0.1f, p.y + 0.06f, 0.12f, 0.06f);
        shapes.rect(x - 0.075f, p.y - 0.16f, 0.15f, 0.055f);
    }
}
