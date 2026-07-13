package com.codex.gravitytilt.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.codex.gravitytilt.GravityTiltGame;

public final class GameControls extends InputAdapter {
    private final GravityTiltGame game;

    public GameControls(GravityTiltGame game) {
        this.game = game;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (game.isSplash()) {
            game.showLevelSelect();
            return true;
        }
        if (game.handleUiTouch(screenX, screenY)) {
            return true;
        }
        if (game.isLevelSelect()) {
            game.selectLevelAt(screenX, screenY);
            return true;
        }
        if (game.isRoundFinished()) {
            game.showLevelSelect();
            return true;
        }
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            game.queueJump();
            return true;
        }
        if (screenX > Gdx.graphics.getWidth() * 0.5f) {
            game.queueJump();
        } else {
            game.setTouchMove(screenX < Gdx.graphics.getWidth() * 0.25f ? -1f : 1f);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            return true;
        }
        if (screenX < Gdx.graphics.getWidth() * 0.5f) {
            game.setTouchMove(screenX < Gdx.graphics.getWidth() * 0.25f ? -1f : 1f);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        game.setTouchMove(0f);
        return true;
    }
}
