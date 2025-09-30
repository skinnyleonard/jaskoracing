package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.Car;
import io.github.jasko.Main;
import online.Server;
import tools.HUD;
import tools.ImageRenderer;
import tools.MapLoader;
import static entities.Car.*;
import tools.WorldContactListener;

import java.security.PublicKey;
import java.text.DecimalFormat;

public class PlayScreen implements Screen {

    public static SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer b2dr;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Car player;
    private final MapLoader mapLoader;
    private static Main game;
    private HUD hud;
    private Server server;

    public PlayScreen() {
        batch = new SpriteBatch();
        world = new World(Constants.GRAVITY, true);
        b2dr = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.zoom = Constants.DEFUALT_ZOOM;
        hud = new HUD(batch);
        viewport = new ExtendViewport(640 / Constants.PPM, 480 / Constants.PPM, camera);
        mapLoader = new MapLoader(world);
        player = new Car(35.0f, 0.8f, 60, mapLoader, DRIVE_4WD, world);
        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void show() {
        server = new Server();
        server.start();
        server.pingEveryone("hola bellos");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        update(delta);
        draw();
        hud.stage.draw();
    }

    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.setDriveDirection(DRIVE_DIRECTION_FORWARD);
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.setDriveDirection(DRIVE_DIRECTION_BACKWARD);
        } else {
            player.setDriveDirection(DRIVE_DIRECTION_NONE);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.setTurnDirection(TURN_DIRECTION_LEFT);
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.setTurnDirection(TURN_DIRECTION_RIGHT);
        } else {
            player.setTurnDirection(TURN_DIRECTION_NONE);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.4f;
        } else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom += 0.4f;
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        b2dr.render(world, camera.combined);
    }
    DecimalFormat df = new DecimalFormat("0.00");
    private void update(final float delta) {
        player.update(delta);
        camera.position.set(player.getBody().getPosition(), 0);
        camera.update();
//        player.getMetrics();
//        server.pingEveryone("hola bellos");
        this.server.pingEveryone("updatePos;"+player.getMetrics());
        HUD.debugLabel.setText("x: "+df.format(Float.parseFloat(player.getMetrics().split(";")[0]))+
            "\ny: "+df.format(Float.parseFloat(player.getMetrics().split(";")[1]))+
            "\nangle: "+df.format(Float.parseFloat(player.getMetrics().split(";")[2])));
        world.step(delta, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        b2dr.dispose();
        mapLoader.dispose();
    }

}
