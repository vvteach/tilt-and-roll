package com.codex.gravitytilt.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.codex.gravitytilt.assets.TextureAssets;

abstract class BoxGameObject extends GameObject {
    private final float width;
    private final float height;

    BoxGameObject(Body body, float width, float height) {
        super(body);
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(ShapeRenderer shapes) {
    }

    protected void drawPlatformTexture(SpriteBatch batch, TextureAssets textures) {
        Vector2 p = getBody().getPosition();
        textures.drawPlatform(batch, p.x - width * 0.5f, p.y - height * 0.5f, width, height);
    }

    protected void drawHazardTexture(SpriteBatch batch, TextureAssets textures) {
        Vector2 p = getBody().getPosition();
        textures.drawHazard(batch, p.x - width * 0.5f, p.y - height * 0.5f, width, height);
    }

    protected void drawDoorTexture(SpriteBatch batch, TextureAssets textures) {
        Vector2 p = getBody().getPosition();
        textures.drawDoor(batch, p.x - width * 0.5f, p.y - height * 0.5f, width, height);
    }

    protected void drawSpringTexture(SpriteBatch batch, TextureAssets textures, boolean active) {
        Vector2 p = getBody().getPosition();
        textures.drawSpring(batch, p.x - width * 0.5f, p.y - height * 0.5f, width, height, active);
    }

    protected float getWidth() {
        return width;
    }

    protected float getHeight() {
        return height;
    }
}
