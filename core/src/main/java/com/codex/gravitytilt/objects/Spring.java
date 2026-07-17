package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.codex.gravitytilt.assets.TextureAssets;
import com.codex.gravitytilt.physics.BodyFactory;

public final class Spring extends BoxGameObject {
    private static final float ACTIVE_TIME = 0.18f;

    private float activeTimer;

    public Spring(World world, float x, float y, float width, float height) {
        super(BodyFactory.createStaticBox(world, x, y, width, height, true), width, height);
        attachToFixtures();
    }

    public void trigger() {
        activeTimer = ACTIVE_TIME;
    }

    public boolean isActive() {
        return activeTimer > 0f;
    }

    @Override
    public void update(float delta) {
        activeTimer = Math.max(0f, activeTimer - delta);
    }

    @Override
    public void render(SpriteBatch batch, TextureAssets textures) {
        drawSpringTexture(batch, textures, activeTimer > 0f);
    }
}
