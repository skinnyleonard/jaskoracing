package io.github.libgdxsnes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import online.Client;
import online.NetManager;

public class GameScreen extends ScreenAdapter implements NetManager {

    public static final int GAME_HEIGHT = 224;
    public static final int GAME_WIDTH = 256;
    public static final double TURN_ANGLE = 0.02;

    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Pixmap3D pixmap;
    private Texture texture;
    private Client  client;

    public GameScreen(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        texture = new Texture(Gdx.files.internal("auto.png"));
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
        camera.update();

        pixmap = new Pixmap3D(GAME_WIDTH, GAME_HEIGHT, Pixmap.Format.RGB565);
        client = new Client(this);
        client.start();
        client.sendMessage("connect");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        handleInput(delta);
        camera.update();

        batch.begin();
        pixmap.render(batch);
        batch.draw(texture, GAME_WIDTH/3.5f, 0, 100, 100);
        batch.end();
    }

    private void handleInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pixmap.pos.x += (float) (20 * Math.cos(pixmap.angle));
            pixmap.pos.y += (float) (20 * Math.sin(pixmap.angle));
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            pixmap.pos.x += (float) (20 * Math.cos(pixmap.angle - Math.toRadians(90)));
            pixmap.pos.y += (float) (20 * Math.sin(pixmap.angle - Math.toRadians(90)));
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            pixmap.pos.x += (float) (20  *Math.cos(pixmap.angle + Math.toRadians(90)));
            pixmap.pos.y += (float) (20 * Math.sin(pixmap.angle + Math.toRadians(90)));
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            pixmap.pos.x -= (float) (20 * Math.cos(pixmap.angle));
            pixmap.pos.y -= (float) (20 * Math.sin(pixmap.angle));
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            pixmap.angle -= TURN_ANGLE;
            pixmap.bgPos += 0.5f;
            if(pixmap.bgPos >= 0) {
                pixmap.bgPos = -256;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            pixmap.angle += TURN_ANGLE;
            pixmap.bgPos -= 0.5f;
            if(pixmap.bgPos <= -512) {
                pixmap.bgPos = -256;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            pixmap.pos.z += 20;
        } else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            pixmap.pos.z -= 20;
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void connect(boolean state) {

    }

    @Override
    public void timeOutEnded() {

    }

    @Override
    public void updateSprites(Vector3 position) {
//        pixmap.pos.x = (float) (pixmap.track.getHeight() - position.x * Math.cos(pixmap.angle));
//        pixmap.pos.y = (float) (pixmap.track.getHeight() - position.y * Math.sin(pixmap.angle));
        pixmap.pos.x = position.x;
        pixmap.pos.y = pixmap.track.getHeight() - position.y;
        pixmap.angle = -Math.toRadians(position.z) - Math.toRadians(90);
    }
}
