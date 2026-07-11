package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.codex.gravitytilt.physics.BodyFactory;

import java.util.ArrayList;

public final class Player extends CircleGameObject {
    static final float RADIUS = 0.42f;

    private static final int FACE_SEGMENTS = 36;
    private static final int FEATURE_SEGMENTS = 12;

    private Color renderColor = Color.GOLD;
    private Expression expression = Expression.NORMAL;

    public Player(World world, float x, float y) {
        super(BodyFactory.createPlayer(world, x, y, RADIUS), RADIUS, 36);
        attachFixtures();
    }

    public void setRenderColor(Color renderColor) {
        this.renderColor = renderColor;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void move(float force) {
        getBody().applyForceToCenter(force, 0f, true);
        getBody().applyTorque(-force * 0.12f, true);
    }

    public void jump(float impulse) {
        Vector2 center = getBody().getWorldCenter();
        getBody().applyLinearImpulse(0f, impulse, center.x, center.y, true);
    }

    private void attachFixtures() {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        for (Fixture fixture : getBody().getFixtureList()) {
            fixtures.add(fixture);
        }
        fixtures.get(0).setUserData(this);
        fixtures.get(1).setUserData(new PlayerFoot(this));
    }

    @Override
    public void render(ShapeRenderer shapes) {
        Vector2 p = getBody().getPosition();
        float angle = getBody().getAngle();

        shapes.setColor(renderColor);
        shapes.circle(p.x, p.y, RADIUS, FACE_SEGMENTS);

        shapes.setColor(0.18f, 0.16f, 0.12f, 1f);
        drawExpression(shapes, p, angle);
    }

    private void drawExpression(ShapeRenderer shapes, Vector2 center, float angle) {
        if (expression == Expression.HURT) {
            drawCrossEye(shapes, center, angle, -0.14f, 0.12f);
            drawCrossEye(shapes, center, angle, 0.14f, 0.12f);
            drawArcMouth(shapes, center, angle, true, 0.08f);
        } else if (expression == Expression.HAPPY) {
            drawHappyEye(shapes, center, angle, -0.14f, 0.13f);
            drawHappyEye(shapes, center, angle, 0.14f, 0.13f);
            drawArcMouth(shapes, center, angle, false, 0.13f);
        } else {
            drawRotatedCircle(shapes, center, angle, -0.14f, 0.12f, 0.055f);
            drawRotatedCircle(shapes, center, angle, 0.14f, 0.12f, 0.055f);
            drawArcMouth(shapes, center, angle, false, 0.09f);
        }
    }

    private void drawArcMouth(ShapeRenderer shapes, Vector2 center, float angle, boolean frown, float depth) {
        for (int i = 0; i < 9; i++) {
            float t = i / 8f;
            float localX = MathUtils.lerp(-0.19f, 0.19f, t);
            float normalized = localX / 0.19f;
            float curve = depth * (1f - normalized * normalized);
            float localY = frown ? -0.18f + curve : -0.1f - curve;
            drawRotatedCircle(shapes, center, angle, localX, localY, 0.028f);
        }
    }

    private void drawHappyEye(ShapeRenderer shapes, Vector2 center, float angle, float eyeX, float eyeY) {
        for (int i = 0; i < 5; i++) {
            float t = i / 4f;
            float localX = eyeX + MathUtils.lerp(-0.055f, 0.055f, t);
            float normalized = (localX - eyeX) / 0.055f;
            float localY = eyeY + 0.035f * (1f - normalized * normalized);
            drawRotatedCircle(shapes, center, angle, localX, localY, 0.021f);
        }
    }

    private void drawCrossEye(ShapeRenderer shapes, Vector2 center, float angle, float eyeX, float eyeY) {
        for (int i = 0; i < 5; i++) {
            float t = MathUtils.lerp(-0.055f, 0.055f, i / 4f);
            drawRotatedCircle(shapes, center, angle, eyeX + t, eyeY + t, 0.022f);
            drawRotatedCircle(shapes, center, angle, eyeX + t, eyeY - t, 0.022f);
        }
    }

    private void drawRotatedCircle(ShapeRenderer shapes, Vector2 center, float angle, float localX, float localY, float radius) {
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float x = center.x + localX * cos - localY * sin;
        float y = center.y + localX * sin + localY * cos;
        shapes.circle(x, y, radius, FEATURE_SEGMENTS);
    }

    public enum Expression {
        NORMAL,
        HURT,
        HAPPY
    }
}
