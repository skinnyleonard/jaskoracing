package tools;

import com.badlogic.gdx.InputProcessor;
import pantallas.MenuScreen;

public class Input implements InputProcessor {
    private boolean down = false, up = false;
    private boolean enter = false;
    MenuScreen app;

    public Input(MenuScreen app){
        this.app = app;
    }

    public boolean isDown(){ return down; }
    public boolean isUp(){ return up; }
    public boolean isEnter(){ return enter; }

    @Override
    public boolean keyDown(int keycode) {
        app.time = 0.05f;
        if (keycode == com.badlogic.gdx.Input.Keys.DOWN) down = true;
        if (keycode == com.badlogic.gdx.Input.Keys.UP)   up = true;
        if (keycode == com.badlogic.gdx.Input.Keys.ENTER) enter = true;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.DOWN) down = false;
        if (keycode == com.badlogic.gdx.Input.Keys.UP)   up = false;
        if (keycode == com.badlogic.gdx.Input.Keys.ENTER) enter = false;
        return false;
    }

    @Override public boolean keyTyped(char character) { return false; }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
