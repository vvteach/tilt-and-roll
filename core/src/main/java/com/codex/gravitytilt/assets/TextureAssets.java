package com.codex.gravitytilt.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class TextureAssets implements Disposable {
    private static final float FALLBACK_PIXELS_PER_WORLD = 64f;
    private static final float PLATFORM_TILE_SCREEN_WIDTH = 48f;
    private static final float PLATFORM_TILE_SCREEN_HEIGHT = 24f;
    private static final float HAZARD_TILE_SCREEN_WIDTH = 48f;
    private static final float HAZARD_TILE_SCREEN_HEIGHT = 16f;
    private static final float DOOR_SCREEN_WIDTH = 64f;

    private final Texture platformTexture;
    private final Texture hazardTexture;
    private final Texture doorTexture;
    private final Texture lockTexture;
    private final Texture splashTexture;
    private final Texture helpTexture;
    private final Texture springTexture;
    private final TextureRegion door;
    private final TextureRegion lock;
    private final TextureRegion splash;
    private final TextureRegion help;
    private final TextureRegion spring;
    private float pixelsPerWorld = FALLBACK_PIXELS_PER_WORLD;

    public TextureAssets() {
        platformTexture = load("textures/platform.png");
        hazardTexture = load("textures/hazard.png");
        doorTexture = load("textures/door.png");
        lockTexture = load("textures/lock.png");
        splashTexture = loadClamped("textures/splash.png");
        helpTexture = loadClamped("textures/help.png");
        springTexture = loadClamped("textures/spring.png");
        door = new TextureRegion(doorTexture);
        lock = new TextureRegion(lockTexture);
        splash = new TextureRegion(splashTexture);
        help = new TextureRegion(helpTexture);
        spring = new TextureRegion(springTexture);
    }

    public void updateScale(Viewport viewport) {
        if (viewport.getScreenWidth() > 0) {
            pixelsPerWorld = viewport.getScreenWidth() / viewport.getWorldWidth();
        }
    }

    public void drawPlatform(SpriteBatch batch, float x, float y, float width, float height) {
        drawRepeated(batch, platformTexture, x, y, width, height,
                screenToWorld(PLATFORM_TILE_SCREEN_WIDTH), screenToWorld(PLATFORM_TILE_SCREEN_HEIGHT));
    }

    public void drawHazard(SpriteBatch batch, float x, float y, float width, float height) {
        drawRepeated(batch, hazardTexture, x, y, width, height,
                screenToWorld(HAZARD_TILE_SCREEN_WIDTH), screenToWorld(HAZARD_TILE_SCREEN_HEIGHT));
    }

    public void drawDoor(SpriteBatch batch, float x, float y, float width, float height) {
        float drawWidth = Math.min(width, screenToWorld(DOOR_SCREEN_WIDTH));
        float drawHeight = drawWidth * door.getRegionHeight() / door.getRegionWidth();
        if (drawHeight > height) {
            drawHeight = height;
            drawWidth = drawHeight * door.getRegionWidth() / door.getRegionHeight();
        }
        batch.draw(door, x + (width - drawWidth) * 0.5f, y, drawWidth, drawHeight);
    }

    public void drawLock(SpriteBatch batch, float x, float y, float size) {
        batch.draw(lock, x, y, size, size);
    }

    public void drawSpring(SpriteBatch batch, float x, float y, float width, float height, boolean active) {
        if (!active) {
            batch.draw(spring, x, y, width, height);
            return;
        }
        float compressedHeight = height * 0.68f;
        float compressedWidth = width * 1.08f;
        batch.draw(spring, x - (compressedWidth - width) * 0.5f, y, compressedWidth, compressedHeight);
    }

    public void drawSplash(SpriteBatch batch, float screenWidth, float screenHeight) {
        drawFullscreen(batch, splash, screenWidth, screenHeight);
    }

    public void drawHelp(SpriteBatch batch, float screenWidth, float screenHeight) {
        drawFullscreen(batch, help, screenWidth, screenHeight);
    }

    private void drawFullscreen(SpriteBatch batch, TextureRegion region, float screenWidth, float screenHeight) {
        float imageWidth = region.getRegionWidth();
        float imageHeight = region.getRegionHeight();
        float scale = Math.max(screenWidth / imageWidth, screenHeight / imageHeight);
        float drawWidth = imageWidth * scale;
        float drawHeight = imageHeight * scale;
        batch.draw(region, (screenWidth - drawWidth) * 0.5f, (screenHeight - drawHeight) * 0.5f,
                drawWidth, drawHeight);
    }

    private Texture load(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        return texture;
    }

    private Texture loadClamped(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
        return texture;
    }

    private float screenToWorld(float pixels) {
        return pixels / pixelsPerWorld;
    }

    private void drawRepeated(SpriteBatch batch, Texture texture, float x, float y, float width, float height,
            float tileWidth, float tileHeight) {
        float u2 = width / tileWidth;
        float v2 = height / tileHeight;
        batch.draw(texture, x, y, width, height, 0f, 0f, u2, v2);
    }

    @Override
    public void dispose() {
        platformTexture.dispose();
        hazardTexture.dispose();
        doorTexture.dispose();
        lockTexture.dispose();
        splashTexture.dispose();
        helpTexture.dispose();
        springTexture.dispose();
    }
}
