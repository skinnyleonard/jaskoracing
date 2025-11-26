package io.github.libgdxsnes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pantallas.GameScreen;
import pantallas.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private SpriteBatch batch;
    private GameScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
//        gameScreen = new GameScreen(this, batch, "lanciadeltahf", "jesus");
//        setScreen(gameScreen);
        setScreen(new MenuScreen());
//        setScreen(new EndingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        getScreen().dispose();
    }
}
