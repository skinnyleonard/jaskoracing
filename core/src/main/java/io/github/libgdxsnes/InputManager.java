package io.github.libgdxsnes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import online.Client;

public class InputManager implements InputProcessor {


    private Client client;
    private GameScreen gameScreen;

    public InputManager(Client client,  GameScreen gameScreen) {
        this.client = client;
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if((keycode == Input.Keys.UP) || (keycode == Input.Keys.DOWN) || (keycode == Input.Keys.LEFT) || (keycode == Input.Keys.RIGHT)){
            client.sendMessage("move$afk");
            gameScreen.frameTimer = 0;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
