package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.codex.gravitytilt.assets.TextureAssets;
import com.codex.gravitytilt.physics.BodyFactory;

public final class Platform extends BoxGameObject {
    public Platform(World world, float x, float y, float width, float height) {
        super(BodyFactory.createStaticBox(world, x, y, width, height, false), width, height);
        attachToFixtures();
    }

    @Override
    public void render(SpriteBatch batch, TextureAssets textures) {
        drawPlatformTexture(batch, textures);
    }
}
