package com.codex.gravitytilt.lwjgl3;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tilt and roll!");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(createGame(), config);
    }

    private static ApplicationListener createGame() {
        try {
            return (ApplicationListener) Class.forName("com.codex.gravitytilt.GravityTiltGame")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot create GravityTiltGame", exception);
        }
    }

    private DesktopLauncher() {
    }
}
