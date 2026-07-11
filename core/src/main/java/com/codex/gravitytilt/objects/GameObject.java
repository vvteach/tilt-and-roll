package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.codex.gravitytilt.assets.TextureAssets;

public abstract class GameObject {
    private final Body body;

    GameObject(Body body) {
        this.body = body;
    }

    protected void attachToFixtures() {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }
    }

    public Body getBody() {
        return body;
    }

    public void destroy(World world) {
        world.destroyBody(body);
    }

    public void update(float delta) {
    }

    public abstract void render(ShapeRenderer shapes);

    public void render(SpriteBatch batch, TextureAssets textures) {
    }
}
