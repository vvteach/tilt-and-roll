package com.codex.gravitytilt;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.codex.gravitytilt.assets.TextureAssets;
import com.codex.gravitytilt.effects.CoinBurst;
import com.codex.gravitytilt.effects.DizzyEffect;
import com.codex.gravitytilt.effects.FireworkEffect;
import com.codex.gravitytilt.input.GameControls;
import com.codex.gravitytilt.levels.LevelCatalog;
import com.codex.gravitytilt.levels.LevelDefinition;
import com.codex.gravitytilt.objects.Coin;
import com.codex.gravitytilt.objects.GameObject;
import com.codex.gravitytilt.objects.Goal;
import com.codex.gravitytilt.objects.Hazard;
import com.codex.gravitytilt.objects.Platform;
import com.codex.gravitytilt.objects.Player;
import com.codex.gravitytilt.objects.Spring;
import com.codex.gravitytilt.physics.GameContactListener;

public class GravityTiltGame extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 24f;
    private static final float WORLD_HEIGHT = 13.5f;
    private static final int LEVEL_COLUMNS = 5;
    private static final int LEVEL_ROWS = 5;
    private static final String PROGRESS_KEY = "unlockedLevel";
    private static final float TIME_STEP = 1f / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float MOVE_FORCE = 26f;
    private static final float MAX_SPEED = 6.4f;
    private static final float JUMP_IMPULSE = 7.4f;
    private static final float JUMP_BUFFER_TIME = 0.12f;
    private static final float COYOTE_TIME = 0.1f;
    private static final float SHAKE_RESTART_THRESHOLD = 18f;
    private static final float DIZZY_EFFECT_TIME = 2f;

    private enum GameMode {
        SPLASH,
        LEVEL_SELECT,
        PLAYING
    }

    private LevelCatalog levels;
    private final Array<GameObject> objects = new Array<>();
    private final Array<GameObject> pendingDestroy = new Array<>();
    private final Array<CoinBurst> coinBursts = new Array<>();
    private final Array<FireworkEffect> fireworks = new Array<>();
    private final DizzyEffect dizzyEffect = new DizzyEffect();

    private Preferences progress;
    private World world;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private Viewport viewport;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private TextureAssets textures;
    private BitmapFont font;
    private Player player;
    private GameMode mode;
    private int currentLevel;
    private int unlockedLevel;
    private int totalCoins;
    private float accumulator;
    private int score;
    private int groundContacts;
    private int goalContacts;
    private float jumpBufferTimer;
    private float coyoteTimer;
    private boolean gameOver;
    private boolean won;
    private boolean goalTouched;
    private boolean helpVisible;
    private float touchMove;
    private float gameOverTime;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        hudCamera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        textures = new TextureAssets();
        font = createHudFont();
        Gdx.input.setInputProcessor(new GameControls(this));
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        textures.updateScale(viewport);
        updateHudCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        levels = new LevelCatalog(Gdx.files.internal("levels/levels.json"));
        progress = Gdx.app.getPreferences("gravity-tilt-progress");
        unlockedLevel = MathUtils.clamp(progress.getInteger(PROGRESS_KEY, 0), 0, levels.size() - 1);
        currentLevel = unlockedLevel;
        mode = GameMode.SPLASH;
    }

    private BitmapFont createHudFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/hud.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 38;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2.2f;
        parameter.borderColor = new Color(0.02f, 0.12f, 0.34f, 1f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = new Color(0.92f, 0.55f, 0.02f, 0.65f);
        parameter.minFilter = TextureFilter.Linear;
        parameter.magFilter = TextureFilter.Linear;

        BitmapFont hudFont = generator.generateFont(parameter);
        hudFont.setUseIntegerPositions(true);
        generator.dispose();
        return hudFont;
    }

    public void buildWorld() {
        if (world != null) {
            world.dispose();
        }
        LevelDefinition level = levels.get(currentLevel);
        world = new World(new Vector2(0f, -20f), true);
        world.setContactListener(new GameContactListener(this));
        objects.clear();
        pendingDestroy.clear();
        coinBursts.clear();
        fireworks.clear();
        score = 0;
        totalCoins = 0;
        groundContacts = 0;
        goalContacts = 0;
        jumpBufferTimer = 0f;
        coyoteTimer = 0f;
        gameOver = false;
        won = false;
        goalTouched = false;
        touchMove = 0f;
        gameOverTime = 0f;
        accumulator = 0f;
        mode = GameMode.PLAYING;

        for (float[] platform : level.getPlatforms()) {
            addPlatform(platform[0], platform[1], platform[2], platform[3]);
        }
        for (float[] coin : level.getCoins()) {
            addCoin(coin[0], coin[1]);
        }
        for (float[] hazard : level.getHazards()) {
            addHazard(hazard[0], hazard[1], hazard[2], hazard[3]);
        }
        for (float[] spring : level.getSprings()) {
            addSpring(spring[0], spring[1], spring[2], spring[3]);
        }
        float[] goal = level.getGoal();
        objects.add(new Goal(world, goal[0], goal[1], goal[2], goal[3]));

        player = new Player(world, level.getStartX(), level.getStartY());
        objects.add(player);
    }

    private void addPlatform(float x, float y, float width, float height) {
        objects.add(new Platform(world, x, y, width, height));
    }

    private void addWall(float x, float y, float width, float height) {
        objects.add(new Platform(world, x, y, width, height));
    }

    private void addCoin(float x, float y) {
        objects.add(new Coin(world, x, y));
        totalCoins++;
    }

    private void addHazard(float x, float y, float width, float height) {
        objects.add(new Hazard(world, x, y, width, height));
    }

    private void addSpring(float x, float y, float width, float height) {
        objects.add(new Spring(world, x, y, width, height));
    }

    @Override
    public void render() {
        update(Math.min(Gdx.graphics.getDeltaTime(), 0.25f));
        draw();
    }

    private void update(float delta) {
        if (mode == GameMode.SPLASH) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                showLevelSelect();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            toggleHelp();
            return;
        }
        if (helpVisible) {
            return;
        }

        if (mode == GameMode.LEVEL_SELECT) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                currentLevel = unlockedLevel;
                buildWorld();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            showLevelSelect();
            return;
        }
        if (won && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || isMobileShakeRestart())) {
            startNextLevel();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            buildWorld();
            return;
        }
        if (gameOver && isMobileShakeRestart()) {
            buildWorld();
            return;
        }

        if (!gameOver && !won) {
            updateInputTimers(delta);
            applyPlayerInput(delta);
            if (player.getBody().getPosition().y < -2f) {
                lose();
            }
        }

        accumulator += delta;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }

        for (GameObject object : objects) {
            object.update(delta);
        }
        for (int i = coinBursts.size - 1; i >= 0; i--) {
            CoinBurst burst = coinBursts.get(i);
            burst.update(delta);
            if (!burst.isAlive()) {
                coinBursts.removeIndex(i);
            }
        }
        for (int i = fireworks.size - 1; i >= 0; i--) {
            FireworkEffect firework = fireworks.get(i);
            firework.update(delta);
            if (!firework.isAlive()) {
                fireworks.removeIndex(i);
            }
        }
        if (gameOver) {
            gameOverTime += delta;
        }

        for (GameObject object : pendingDestroy) {
            if (objects.removeValue(object, true)) {
                object.destroy(world);
            }
        }
        pendingDestroy.clear();
    }

    private void updateInputTimers(float delta) {
        if (groundContacts > 0) {
            coyoteTimer = COYOTE_TIME;
        } else {
            coyoteTimer = Math.max(0f, coyoteTimer - delta);
        }
        jumpBufferTimer = Math.max(0f, jumpBufferTimer - delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            queueJump();
        }
    }

    private void applyPlayerInput(float delta) {
        float direction = touchMove;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction += 1f;
        }
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            direction += MathUtils.clamp(-Gdx.input.getAccelerometerX() / 4f, -1f, 1f);
        }
        direction = MathUtils.clamp(direction, -1f, 1f);

        Vector2 velocity = player.getBody().getLinearVelocity();
        if (Math.abs(velocity.x) < MAX_SPEED || Math.signum(direction) != Math.signum(velocity.x)) {
            player.move(direction * MOVE_FORCE);
        }
        if (jumpBufferTimer > 0f && coyoteTimer > 0f) {
            player.jump(JUMP_IMPULSE);
            jumpBufferTimer = 0f;
            coyoteTimer = 0f;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.06f, 0.07f, 0.09f, 1f);
        if (mode == GameMode.SPLASH) {
            drawSplash();
            return;
        }
        if (mode == GameMode.LEVEL_SELECT) {
            drawLevelSelect();
            drawHelpUi();
            return;
        }

        camera.position.set(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, 0f);
        camera.update();

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawBackground();
        shapes.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderTexturedObjects();
        batch.end();

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        renderShapeObjects();
        shapes.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        drawHud();
        batch.end();

        drawHelpUi();
    }

    private void drawBackground() {
        shapes.setColor(0.09f, 0.12f, 0.16f, 1f);
        shapes.rect(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        shapes.setColor(0.16f, 0.19f, 0.22f, 1f);
        for (int i = 0; i < 8; i++) {
            shapes.rect(i * 3.5f - 1f, 0f, 0.08f, WORLD_HEIGHT);
        }
    }

    private void drawSplash() {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        textures.drawSplash(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    private void renderShapeObjects() {
        player.setRenderColor(gameOver ? Color.SCARLET : goalTouched ? Color.LIME : Color.GOLD);
        player.setExpression(gameOver ? Player.Expression.HURT : goalTouched ? Player.Expression.HAPPY : Player.Expression.NORMAL);
        for (GameObject object : objects) {
            object.render(shapes);
        }
        for (CoinBurst burst : coinBursts) {
            burst.render(shapes);
        }
        for (FireworkEffect firework : fireworks) {
            firework.render(shapes);
        }
        if (gameOver && gameOverTime < DIZZY_EFFECT_TIME) {
            dizzyEffect.render(shapes, player.getBody().getPosition(), player.getBody().getAngle(), gameOverTime);
        }
    }

    private void renderTexturedObjects() {
        for (GameObject object : objects) {
            object.render(batch, textures);
        }
    }

    private void drawHud() {
        float x = 28f;
        float y = Gdx.graphics.getHeight() - 42f;
        float lineHeight = 56f;

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Coins: " + score + " / " + totalCoins, x, y);
        font.draw(batch, "Level: " + (currentLevel + 1) + " / " + levels.size(), x, y - lineHeight);
        if (gameOver || won) {
            String hint;
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                hint = won ? "Shake - next level" : "Shake - restart";
            } else {
                hint = won ? "Space - next level" : "R - restart";
            }
            GlyphLayout layout = new GlyphLayout(font, hint);
            font.draw(batch, hint, (Gdx.graphics.getWidth() - layout.width) * 0.5f, y - lineHeight * 2.2f);
        }
    }

    private void drawLevelSelect() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float cell = Math.min(width * 0.14f, height * 0.14f);
        float gap = cell * 0.18f;
        float gridWidth = LEVEL_COLUMNS * cell + (LEVEL_COLUMNS - 1) * gap;
        float gridHeight = LEVEL_ROWS * cell + (LEVEL_ROWS - 1) * gap;
        float startX = (width - gridWidth) * 0.5f;
        float startY = (height - gridHeight) * 0.45f;

        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.06f, 0.07f, 0.09f, 1f);
        shapes.rect(0f, 0f, width, height);

        for (int i = 0; i < levels.size(); i++) {
            int col = i % LEVEL_COLUMNS;
            int row = i / LEVEL_COLUMNS;
            float x = startX + col * (cell + gap);
            float y = startY + (LEVEL_ROWS - 1 - row) * (cell + gap);
            boolean open = i <= unlockedLevel;
            boolean selected = i == currentLevel;
            if (open) {
                shapes.setColor(selected ? Color.LIME : Color.GOLD);
            } else {
                shapes.setColor(0.25f, 0.28f, 0.31f, 1f);
            }
            shapes.rect(x, y, cell, cell);
            shapes.setColor(0.04f, 0.05f, 0.06f, 1f);
            shapes.rect(x + 4f, y + 4f, cell - 8f, cell - 8f);
        }
        shapes.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

        for (int i = 0; i < levels.size(); i++) {
            int col = i % LEVEL_COLUMNS;
            int row = i / LEVEL_COLUMNS;
            float x = startX + col * (cell + gap);
            float y = startY + (LEVEL_ROWS - 1 - row) * (cell + gap);
            boolean open = i <= unlockedLevel;
            if (!open) {
                float lockSize = cell * 0.48f;
                textures.drawLock(batch, x + (cell - lockSize) * 0.5f, y + (cell - lockSize) * 0.5f, lockSize);
                continue;
            }
            String label = String.valueOf(i + 1);
            GlyphLayout layout = new GlyphLayout(font, label);
            font.setColor(Color.WHITE);
            font.draw(batch, label, x + (cell - layout.width) * 0.5f, y + (cell + layout.height) * 0.5f);
        }
        batch.end();
    }

    private void drawHelpUi() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float radius = 34f;
        float centerX = width - 40f;
        float centerY = height - 52f;

        if (helpVisible) {
            batch.setProjectionMatrix(hudCamera.combined);
            batch.begin();
            textures.drawHelp(batch, width, height);
            batch.end();
        }

        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.12f, 0.16f, 0.19f, 1f);
        shapes.circle(centerX, centerY, radius, 32);
        shapes.setColor(Color.GOLD);
        shapes.circle(centerX, centerY, radius - 4f, 32);
        shapes.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.getData().setScale(1.2f);
        String question = "?";
        GlyphLayout questionLayout = new GlyphLayout(font, question);
        font.setColor(Color.WHITE);
        font.draw(batch, question, centerX - questionLayout.width * 0.5f + 2f,
                centerY + questionLayout.height * 0.5f);
        font.getData().setScale(1f);

        if (helpVisible) {
            String title = "HELP";
            String desktop = "Desktop";
            String move = "A/D or arrows - move";
            String jump = "Space - jump / next level";
            String menu = "R - restart, M/Esc - levels";
            String mobile = "Mobile";
            String tilt = "Tilt phone - move, tap anywhere - jump";
            String shake = "Shake phone - restart";
            String unlock = "Levels unlock one by one.";
            float panelWidth = Math.min(860f, width * 0.72f);
            float panelHeight = Math.min(500f, height * 0.58f);
            float panelY = (height - panelHeight) * 0.5f;
            float margin = Math.max(18f, Math.min(34f, panelWidth * 0.055f));
            float scale = MathUtils.clamp(Math.min(panelWidth / 760f, panelHeight / 440f), 0.48f, 0.82f);
            float y = panelY + panelHeight - margin - 18f * scale;
            float line = 50f * scale;
            font.setColor(Color.WHITE);
            font.getData().setScale(scale);
            float blockWidth = Math.max(textWidth(title), Math.max(textWidth(desktop), Math.max(textWidth(move),
                    Math.max(textWidth(jump), Math.max(textWidth(menu), Math.max(textWidth(mobile),
                            Math.max(textWidth(tilt), Math.max(textWidth(shake), textWidth(unlock)))))))));
            float x = (width - blockWidth) * 0.5f;
            font.draw(batch, title, x, y);
            font.draw(batch, desktop, x, y - line * 1.15f);
            font.draw(batch, move, x, y - line * 2.05f);
            font.draw(batch, jump, x, y - line * 2.95f);
            font.draw(batch, menu, x, y - line * 3.85f);
            font.draw(batch, mobile, x, y - line * 5.0f);
            font.draw(batch, tilt, x, y - line * 5.9f);
            font.draw(batch, shake, x, y - line * 6.8f);
            font.draw(batch, unlock, x, y - line * 7.7f);
            font.getData().setScale(1f);
        }
        batch.end();
    }

    private float textWidth(String text) {
        return new GlyphLayout(font, text).width;
    }

    public void collect(Coin coin) {
        if (!pendingDestroy.contains(coin, true)) {
            Vector2 p = coin.getBody().getPosition();
            coinBursts.add(new CoinBurst(p.x, p.y));
            pendingDestroy.add(coin);
            score++;
        }
    }

    public void lose() {
        if (gameOver) {
            return;
        }
        gameOver = true;
        gameOverTime = 0f;
        player.getBody().setLinearVelocity(0f, 0f);
    }

    public void win() {
        if (won) {
            return;
        }
        goalTouched = true;
        won = true;
        Vector2 p = player.getBody().getPosition();
        fireworks.add(new FireworkEffect(p.x, p.y));
        player.getBody().setLinearVelocity(0f, 0f);
        unlockNextLevel();
    }

    public void triggerSpring(Spring spring) {
        if (isRoundFinished() || spring.isActive()) {
            return;
        }
        spring.trigger();
        Vector2 velocity = player.getBody().getLinearVelocity();
        float impulse = velocity.y < -1.5f ? 11.2f : 7.4f;
        player.getBody().setLinearVelocity(velocity.x, Math.max(0f, velocity.y));
        Vector2 center = player.getBody().getWorldCenter();
        player.getBody().applyLinearImpulse(0f, impulse, center.x, center.y, true);
    }

    public boolean isRoundFinished() {
        return gameOver || won;
    }

    public boolean isLevelSelect() {
        return mode == GameMode.LEVEL_SELECT;
    }

    public boolean isSplash() {
        return mode == GameMode.SPLASH;
    }

    public boolean handleUiTouch(int screenX, int screenY) {
        if (isHelpButton(screenX, screenY)) {
            toggleHelp();
            return true;
        }
        if (helpVisible) {
            helpVisible = false;
            return true;
        }
        return false;
    }

    public void toggleHelp() {
        helpVisible = !helpVisible;
    }

    public void showLevelSelect() {
        touchMove = 0f;
        helpVisible = false;
        mode = GameMode.LEVEL_SELECT;
    }

    public void startNextLevel() {
        if (currentLevel < levels.size() - 1) {
            currentLevel++;
            buildWorld();
        } else {
            showLevelSelect();
        }
    }

    private boolean isMobileShakeRestart() {
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            return false;
        }
        float x = Gdx.input.getAccelerometerX();
        float y = Gdx.input.getAccelerometerY();
        float z = Gdx.input.getAccelerometerZ();
        return x * x + y * y + z * z > SHAKE_RESTART_THRESHOLD * SHAKE_RESTART_THRESHOLD;
    }

    private boolean isHelpButton(int screenX, int screenY) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float centerX = width - 40f;
        float centerY = height - 52f;
        float touchY = height - screenY;
        float dx = screenX - centerX;
        float dy = touchY - centerY;
        return dx * dx + dy * dy <= 42f * 42f;
    }

    public void selectLevelAt(int screenX, int screenY) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float worldY = height - screenY;
        float cell = Math.min(width * 0.14f, height * 0.14f);
        float gap = cell * 0.18f;
        float gridWidth = LEVEL_COLUMNS * cell + (LEVEL_COLUMNS - 1) * gap;
        float gridHeight = LEVEL_ROWS * cell + (LEVEL_ROWS - 1) * gap;
        float startX = (width - gridWidth) * 0.5f;
        float startY = (height - gridHeight) * 0.45f;

        for (int i = 0; i < levels.size(); i++) {
            int col = i % LEVEL_COLUMNS;
            int row = i / LEVEL_COLUMNS;
            float x = startX + col * (cell + gap);
            float y = startY + (LEVEL_ROWS - 1 - row) * (cell + gap);
            if (screenX >= x && screenX <= x + cell && worldY >= y && worldY <= y + cell && i <= unlockedLevel) {
                currentLevel = i;
                buildWorld();
                return;
            }
        }
    }

    private void unlockNextLevel() {
        if (currentLevel >= unlockedLevel && unlockedLevel < levels.size() - 1) {
            unlockedLevel = currentLevel + 1;
            progress.putInteger(PROGRESS_KEY, unlockedLevel);
            progress.flush();
        }
    }

    public void queueJump() {
        jumpBufferTimer = JUMP_BUFFER_TIME;
    }

    public void setTouchMove(float touchMove) {
        this.touchMove = touchMove;
    }

    public void changeGroundContacts(int delta) {
        groundContacts = Math.max(0, groundContacts + delta);
    }

    public void changeGoalContacts(int delta) {
        goalContacts = Math.max(0, goalContacts + delta);
        if (goalContacts > 0) {
            goalTouched = true;
            win();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        textures.updateScale(viewport);
        updateHudCamera(width, height);
    }

    private void updateHudCamera(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
    }

    @Override
    public void dispose() {
        if (world != null) {
            world.dispose();
        }
        shapes.dispose();
        batch.dispose();
        textures.dispose();
        font.dispose();
    }
}
