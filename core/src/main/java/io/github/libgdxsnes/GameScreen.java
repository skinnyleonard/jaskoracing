package io.github.libgdxsnes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import online.Client;
import online.NetManager;

import java.text.DecimalFormat;

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
    private HUD hud;
    DecimalFormat df = new DecimalFormat("0.00");
    private InputManager inputManager;
    private Sprite sprite;

    public GameScreen(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        texture = new Texture(Gdx.files.internal("cars/subaru/1.png"));
        hud = new HUD(batch);
        sprite = new Sprite(texture);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
        camera.update();

        pixmap = new Pixmap3D(GAME_WIDTH, GAME_HEIGHT, Pixmap.Format.RGB565);
        client = new Client(this);
        inputManager = new InputManager(client);
        client.start();
        client.sendMessage("connect");
        Gdx.input.setInputProcessor(inputManager);
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
//        batch.draw(texture, (float) GAME_WIDTH /9, 0, (float) texture.getWidth() /10, (float) texture.getHeight() /10);
        batch.draw(sprite, (float) GAME_WIDTH /9, 0, (float) texture.getWidth() /10, (float) texture.getHeight() /10);
        batch.end();
        hud.stage.draw();
    }

    private void handleInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            client.sendMessage("move$up");
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            client.sendMessage("move$down");
        } else {
//            client.sendMessage("move$afk");
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            client.sendMessage("move$left");
            for(int i=1; i<4; i++) {
                texture = new Texture(Gdx.files.internal("cars/subaru/"+i+".png"));
                sprite = new Sprite(texture);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            client.sendMessage("move$right");
            for(int i=1; i<4; i++) {
                texture = new Texture(Gdx.files.internal("cars/subaru/"+i+".png"));
                sprite = new Sprite(texture);
                sprite.flip(true, false);
            }
        } else {
//            client.sendMessage("move$afk");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
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
    public void updateSprites(String position) {
        pixmap.pos.x = Float.parseFloat(position.split("%")[0]);
        pixmap.pos.y = (pixmap.track.getHeight() - Float.parseFloat(position.split("%")[1]));
        pixmap.angle = -Float.parseFloat(position.split("%")[2]) - Math.toRadians(90);
//        HUD.debugLabel.setText("x: "+df.format(position.x)+"\n"+"y: "+df.format(position.y));
    }

    @Override
    public void createSpritePlayer(String lascosas) {
        for(int i = 0; i < lascosas.split("\\$").length; i++) {
            System.out.println("auto "+i+":");

            pixmap.entities.add(new Sprite3D(new Pixmap(Gdx.files.internal(
                lascosas.split("\\$")[i].split("%")[0])),
                Float.parseFloat(lascosas.split("\\$")[i].split("%")[1]),
                pixmap.track.getHeight() - Float.parseFloat(lascosas.split("\\$")[i].split("%")[2]))
            );

            System.out.println("    path: "+lascosas.split("\\$")[i].split("%")[0]);
            System.out.println("    x: "+lascosas.split("\\$")[i].split("%")[1]);
            System.out.println("    y: "+lascosas.split("\\$")[i].split("%")[2]);
        }
    }
}
