package pantallas;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import online.Client;
import online.NetManager;
import tools.HUD;
import tools.InputManager;
import tools.Pixmap3D;
import tools.Sprite3D;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameScreen extends ScreenAdapter implements NetManager {

    public static final int GAME_HEIGHT = 224;
    public static final int GAME_WIDTH = 256;
    public static final double TURN_ANGLE = 0.02;

    private Game game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Pixmap3D pixmap;
    private Texture texture;
    public Client client;
    private HUD hud;
    private InputManager inputManager;
    private Sprite sprite;
    private Sprite posImage;
    private boolean connected = false;
    private Array<Texture> carFrames = new Array<>();
    private Array<Texture> posSprites = new Array<>();
    public float frameTimer = 0f;
    private float frameDuration = 0.1f;
    private Map<String, Pixmap> pixmapCache = new HashMap<>();
    private int myID;
    private String carBrand = "";
    private String playerName;
    private boolean finished = false;
    private Sprite spriteForAnim;
    private int x = 100;
    private ShapeRenderer shapeRenderer;
    private EndingScreen endingScreen;
    private int position = 1;
    private Viewport viewport;

    public GameScreen(Game game, SpriteBatch batch, String carBrand, String playerName) {
        this.game = game;
        this.batch = batch;
        this.carBrand = carBrand;
        this.playerName = playerName;
        texture = new Texture(Gdx.files.internal("cars/"+carBrand+"/1.png"));
        preloadFrames();
        hud = new HUD(batch);
        sprite = new Sprite(texture);
        spriteForAnim = new Sprite(texture);
        posImage = new Sprite(new Texture(Gdx.files.internal("posiciones/1.png")));
        shapeRenderer = new ShapeRenderer();
        endingScreen = new EndingScreen(game);
    }

    @Override
    public void show() {
        System.out.println("iniciando camara");
        camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
        camera.update();
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        sprite.setTexture(carFrames.get(0));
        spriteForAnim.setTexture(carFrames.get(3));
        spriteForAnim.setPosition(0-spriteForAnim.getTexture().getWidth(), 70);

        System.out.println("iniciando pixmap");
        pixmap = new Pixmap3D(GAME_WIDTH, GAME_HEIGHT, Pixmap.Format.RGB565);

        System.out.println("iniciando cliente");
        client = new Client(this);

        System.out.println("iniciando input");
        inputManager = new InputManager(client, this);

        System.out.println("Iniciando hilo del cliente");
        client.start();

        System.out.println("se solicita conexion");
        client.sendMessage("connect$"+carBrand+"$"+playerName);

        System.out.println("iniciando input");
        Gdx.input.setInputProcessor(inputManager);
    }

    private void preloadFrames() {
        for(int i=1; i<7; i++) {
            Pixmap orignal = new Pixmap(Gdx.files.internal("cars/"+carBrand+"/"+i+".png"));
            int scaledWidth = orignal.getWidth() / 10;
            int scaledHeight = orignal.getHeight() / 10;
            System.out.println(i);
            Pixmap pixelated = new Pixmap(scaledWidth, scaledHeight, orignal.getFormat());

            for(int y = 0; y < scaledHeight; y++) {
                for (int x  = 0; x < scaledWidth; x++) {
                    int scrx = x * 10;
                    int scry = y * 10;
                    int pixel = orignal.getPixel(scrx, scry);
                    pixelated.drawPixel(x, y, pixel);
                }
            }

            Texture t = new Texture(pixelated);
            carFrames.add(t);
        }

        for(int i=1; i<9; i++) {
            Texture t = new Texture(Gdx.files.internal("posiciones/"+i+".png"));
            posSprites.add(t);
        }
    }
    boolean sceneChanged = false;
    float fade = 0.0f;
    private void finishAnimation(float delta) {
        float speed = 400f;
        pixmap.pos.z = 70;
        frameTimer += delta;
        x-=speed * delta;

        if(frameTimer >= frameDuration && sceneChanged==false) {
            pixmap.entities.get(0).position.y -= 20f;
        }
//        System.out.println(pixmap.entities.get(0).position.y);
        if(pixmap.entities.get(0).position.y <= 1600 && !sceneChanged) {
            sceneChanged = true;
            pixmap.angle = Math.toRadians(90);
            pixmap.pos.x = 1000;
            pixmap.entities.get(0).replacePixmap("cars/"+carBrand+"/6.png", pixmapCache, false);
            pixmap.entities.get(0).position.x = 1000;
            pixmap.entities.get(0).position.y = 2400;
        }
        if(frameTimer >= frameDuration && sceneChanged==true) {
            pixmap.entities.get(0).position.y -= 20f;
            if(pixmap.entities.get(0).position.y < 1800) {
                pixmap.angle = -Math.toRadians(90);
                pixmap.entities.get(0).replacePixmap("cars/"+carBrand+"/1.png", pixmapCache, false);
                if(pixmap.entities.get(0).position.y < 900) {
                    fade += 0.04f;
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(0,0,0,fade);
                    shapeRenderer.rect(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
                    shapeRenderer.end();
                    System.out.println("fade: "+fade);
                    if(fade >= 2) {
                        System.err.println("ACA TENDRIA QUE ENTRAR AL PUTISIMO ENDINGSCREEN");
                        endingScreen.passInfo(position, this);
                        game.setScreen(endingScreen);
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        if(finished == false) {
            handleInput(delta);
        }
        camera.update();

        int maxPacketsPerFrame = 5;
        int processed = 0;
        while (!client.positionUpdates.isEmpty() && connected && pixmap.track != null && processed < maxPacketsPerFrame) {
            if(connected && pixmap != null && pixmap.track != null) {
                updatePlayer(client.positionUpdates.poll());
            } else {
                break;
            }
            processed++;
        }

        batch.begin();
        pixmap.render(batch);
        if(finished == true) {
            hud.stage.clear();
            finishAnimation(delta);
        } else {
            batch.draw(sprite, (float) GAME_WIDTH /9, 0, (float) texture.getWidth() /10, (float) texture.getHeight() /10);
        }
        batch.draw(posImage, (GAME_WIDTH-posImage.getWidth()/2)-5, 5, posImage.getWidth()/2, posImage.getHeight()/2);
        batch.end();
        hud.stage.draw();
    }

    private void handleInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            client.sendMessage("move$up");
            sprite.setTexture(carFrames.get(0));
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            client.sendMessage("move$down");
            sprite.setTexture(carFrames.get(0));
        } else {
//            client.sendMessage("move$afk");
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            client.sendMessage("move$left");
            frameTimer += delta;
            sprite.setTexture(carFrames.get(1));
            sprite.setFlip(false, false);
            if(frameTimer >= frameDuration) {
                sprite.setTexture(carFrames.get(2));
                sprite.setFlip(false, false);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            client.sendMessage("move$right");
            frameTimer += delta;
            sprite.setTexture(carFrames.get(1));
            sprite.setFlip(true, false);
            if(frameTimer >= frameDuration) {
                sprite.setTexture(carFrames.get(2));
                sprite.setFlip(true, false);
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            Boolean fullscreen = Gdx.graphics.isFullscreen();
            if(fullscreen == true) {
                Gdx.graphics.setWindowedMode(GAME_WIDTH*3, GAME_HEIGHT*3);
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public void connect(boolean state, int id) {
        if(id >= 0) {
            connected = state;
            myID = id;
            this.client.sendMessage("");
            HUD.timeLabel.setText("MI ID ES: "+myID);
        } else if(id == -1) {
            HUD.timeLabel.setText("El servidor esta lleno!");
        }
    }

    @Override
    public void updatePlayer(Vector3 position) {
        if(finished == false) {
        Gdx.app.postRunnable(() -> {
            if(!connected || pixmap.track == null) {
                System.err.println("el track no se inicializo");
                return;
            }

                pixmap.pos.x = position.x;
                pixmap.pos.y = pixmap.track.getHeight() - position.y;
                pixmap.angle = -position.z - Math.toRadians(90);

//ESTO LUEGO DESCOMENTARLO

// ESTO DE ACA ABAJO ES CON FINES DE PRUEBA, CORREGIR MAS TARDE
//            pixmap.pos.x = (position.x);
//            pixmap.pos.y = (pixmap.track.getHeight() - position.y) + 550;
//            //pixmap.angle = -position.z - Math.toRadians(90);
//
//            pixmap.testposx = position.x;
//            pixmap.testposy = pixmap.track.getHeight() - position.y;
//            pixmap.testangle = -position.z;
        });
        }
    }

    @Override
    public void createSpritePlayer(String data) {
        Gdx.app.postRunnable(() -> {
            try {
                JsonReader reader =  new JsonReader();
                JsonValue root = reader.parse(data);
                JsonValue players = root.get("players");

                for(JsonValue player : players) {
                    int id = player.get("id").asInt();
                    float x =  player.get("x").asFloat();
                    float y =  pixmap.track.getHeight() - player.get("y").asFloat();
                    String path = player.get("path").asString();

                    if(!(myID == id)) {
                        pixmap.entities.add(new Sprite3D(new Pixmap(Gdx.files.internal(path)), x, y, id));
                    } else {
                        Pixmap invisiblePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                        invisiblePixmap.setColor(0,0,0,0);
                        invisiblePixmap.fill();
                        Sprite3D placeholder = new Sprite3D(invisiblePixmap, x, y, id);
                        placeholder.sort = Integer.MAX_VALUE;
                        pixmap.entities.add(placeholder);
                    }

                }
            } catch (Exception e) {
                System.err.println("error al crear el SpritePlayer");
            }
        });
    }

    @Override
    public void updateOtherPos(String jsonText) {
        JsonReader reader =  new JsonReader();
        JsonValue root = reader.parse(jsonText);
        JsonValue players = root.get("players");

        for(JsonValue player : players) {
            int id = player.get("id").asInt();
            float x =  player.get("x").asFloat();
            float y =  pixmap.track.getHeight() - player.get("y").asFloat();
            String path = player.get("path").asString();
            boolean flip = player.get("flip").asBoolean();

            Sprite3D existing = null;
            boolean found = false;
            int i = 0;
            while(i < pixmap.entities.size() && !found) {
                if(pixmap.entities.get(i).id == id) {
                    existing = pixmap.entities.get(i);
                    found = true;
                }
                i++;
            }

            if(existing != null) {
                existing.position.set(x, y);
                if(id != myID) {
                    existing.replacePixmap(path, pixmapCache, flip);
                }
            }
        }
    }

    @Override
    public void updateGrid(String grid) {
        grid = grid.replaceAll("\\s+","");
        int index = 0;
        for(int i=0;i<grid.split("\\$").length;i++) {
            if(Integer.parseInt(grid.split("\\$")[i].split("\\|")[1].split(":")[1]) == myID) {
                index = i;
            }
        }
        position = Integer.parseInt(grid.split("\\$")[index].split("\\|")[0].split(":")[1]);
        posImage.setTexture(posSprites.get(position-1));

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<grid.split("\\$").length;i++) {
            sb.append(grid.split("\\$")[i].split("\\|")[0].split(":")[1])
              .append(" "+grid.split("\\$")[i].split("\\|")[2])
              .append("\n");
        }
        HUD.gridLabel.setText(sb.toString());
    }

    @Override
    public void checkFinished(int id) {
        if(id == myID) {
            pixmap.entities.clear();
            pixmap.entities.add(new Sprite3D(new Pixmap(Gdx.files.internal("cars/"+carBrand+"/4.png")), 1000, 2300, 0));
            pixmap.pos.x = 652;
            pixmap.pos.y = 2000;
            pixmap.angle = 0;
            frameTimer = 0;
            fade = 0;
            finished = true;
        }
    }

    @Override
    public void dispose() {
        for(Pixmap cached : pixmapCache.values()) {
            cached.dispose();
        }
        pixmapCache.clear();
        client.finish();
    }
}
