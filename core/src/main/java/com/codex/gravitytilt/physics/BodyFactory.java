package com.codex.gravitytilt.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public final class BodyFactory {
    public static Body createStaticBox(World world, float x, float y, float width, float height, boolean sensor) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width * 0.5f, height * 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.9f;
        fixtureDef.isSensor = sensor;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public static Body createStaticCircleSensor(World world, float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    public static Body createPlayer(World world, float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = false;
        bodyDef.bullet = true;
        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.2f;
        fixtureDef.friction = 0.45f;
        fixtureDef.restitution = 0.08f;
        body.createFixture(fixtureDef);
        circle.dispose();

        PolygonShape foot = new PolygonShape();
        foot.setAsBox(radius * 0.62f, 0.08f, new Vector2(0f, -radius), 0f);
        FixtureDef footDef = new FixtureDef();
        footDef.shape = foot;
        footDef.isSensor = true;
        body.createFixture(footDef);
        foot.dispose();
        return body;
    }

    private BodyFactory() {
    }
}
