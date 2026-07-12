package com.codex.gravitytilt.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.codex.gravitytilt.GravityTiltGame;
import com.codex.gravitytilt.objects.Coin;
import com.codex.gravitytilt.objects.Goal;
import com.codex.gravitytilt.objects.Hazard;
import com.codex.gravitytilt.objects.Platform;
import com.codex.gravitytilt.objects.Player;
import com.codex.gravitytilt.objects.PlayerFoot;
import com.codex.gravitytilt.objects.Spring;

public final class GameContactListener implements ContactListener {
    private final GravityTiltGame game;

    public GameContactListener(GravityTiltGame game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        route(a, b, true);
        route(b, a, true);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        route(a, b, false);
        route(b, a, false);
    }

    private void route(Fixture self, Fixture other, boolean began) {
        Object selfData = self.getUserData();
        Object otherData = other.getUserData();
        if (selfData instanceof PlayerFoot && otherData instanceof Platform) {
            game.changeGroundContacts(began ? 1 : -1);
        }
        if ((selfData instanceof Player || selfData instanceof PlayerFoot) && otherData instanceof Goal) {
            game.changeGoalContacts(began ? 1 : -1);
        }
        if (!began || !(selfData instanceof Player || selfData instanceof PlayerFoot)) {
            return;
        }
        if (otherData instanceof Coin coin) {
            game.collect(coin);
        } else if (otherData instanceof Hazard) {
            game.lose();
        } else if (otherData instanceof Goal) {
            game.win();
        } else if (otherData instanceof Spring spring) {
            game.triggerSpring(spring);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
