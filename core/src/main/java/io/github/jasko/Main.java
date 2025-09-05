package io.github.jasko;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import screens.PantallaMenu;
import screens.PlayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
        setScreen(new PantallaMenu(this));
    }

    @Override
    public void render() {
    	super.render();
    }

    @Override
    public void dispose() {
    	super.dispose();
    }
}
