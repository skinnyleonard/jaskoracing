package io.github.libgdxsnes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import online.Client;
import online.NetManager;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private long lastMessageTime = -1;
    private int packetCount = 0;
    private boolean connected = false;
    private static ConcurrentLinkedQueue<Vector3> pendingPositions = new ConcurrentLinkedQueue<>();
    private Array<Texture> carFrames = new Array<>();
    private int currentFrame = 0;
    public float frameTimer = 0f;
    private float frameDuration = 0.1f;
    private final String carBrand = "renault5";
    private Map<String, Pixmap> pixmapCache = new HashMap<>();
    private int myID;

    public GameScreen(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        texture = new Texture(Gdx.files.internal("cars/"+carBrand+"/1.png"));
        preloadFrames();
        hud = new HUD(batch);
        sprite = new Sprite(texture);
    }

    @Override
    public void show() {
        System.out.println("iniciando camara");
        camera = new OrthographicCamera(GAME_WIDTH, GAME_HEIGHT);
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        camera.position.set(GAME_WIDTH/2, GAME_HEIGHT/2, 0);
        camera.update();
        sprite.setTexture(carFrames.get(0));

        System.out.println("iniciando pixmap");
        pixmap = new Pixmap3D(GAME_WIDTH, GAME_HEIGHT, Pixmap.Format.RGB565);

        System.out.println("iniciando cliente");
        client = new Client(this);

        System.out.println("iniciando input");
        inputManager = new InputManager(client, this);

        System.out.println("Iniciando hilo del cliente");
        client.start();

        System.out.println("se solicita conexion");
        client.sendMessage("connect");

        System.out.println("iniciando input");
        Gdx.input.setInputProcessor(inputManager);
    }

    private void preloadFrames() {
        for(int i=1; i<4; i++) {
            Pixmap orignal = new Pixmap(Gdx.files.internal("cars/"+carBrand+"/"+i+".png"));
            int scaledWidth = orignal.getWidth() / 10;
            int scaledHeight = orignal.getHeight() / 10;

            Pixmap pixelated = new Pixmap(scaledWidth, scaledHeight, orignal.getFormat());

            for(int y = 0; y < scaledHeight; y++) {
                for (int x  = 0; x < scaledWidth; x++) {
                    int scrx = x * 10;
                    int scry = y * 10;
                    int pixel = orignal.getPixel(scrx, scry);
                    pixelated.drawPixel(x, y, pixel);
                }
            }
//
            Texture t = new Texture(pixelated);
            carFrames.add(t);
            System.out.println(i);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        handleInput(delta);
        camera.update();

//        while(!client.positionUpdates.isEmpty()){
//            Vector3 position = client.positionUpdates.poll();
//            updateSprites(position);
//        }
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

//        float alpha = 0.1f;
//        currentAngle += (targetAngle -  currentAngle) * alpha;
//        System.out.printf("🎯 Ángulo actual: %.6f | objetivo: %.6f%n", currentAngle, targetAngle);

        batch.begin();
        pixmap.render(batch);
        batch.draw(sprite, (float) GAME_WIDTH /9, 0, (float) texture.getWidth() /10, (float) texture.getHeight() /10);
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            Vector2 screenPos = pixmap.projectToScreen(652, 2000, new Vector3(652, 2000, 140), 0, new Vector2(300, 300), 40, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            if (screenPos != null) {
                System.err.println("screenPos: " + screenPos.x + ", " + screenPos.y);
            } else {
                System.err.println("No se pudo proyectar el punto.");
            }
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            HUD.scoreLabel.setText(getStatusOfAll());
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

    private String getStatusOfAll() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(Sprite3D pixmap : pixmap.entities) {
            sb.append("\n").append("ID: ").append(String.valueOf(pixmap.id)).append(" PATH:").append(pixmap.file.path()).append("\n");
            i++;
        }
        return "ENTIDADES: "+i+sb.toString();
    }

    @Override
    public void connect(boolean state, int id) {
        connected = state;
        myID = id;
    }

    @Override
    public void updatePlayer(Vector3 position) {
//        HUD.debugLabel.setText("x: "+df.format(position.x)+"\n"+"y: "+df.format(position.y));
        Gdx.app.postRunnable(() -> {
            if(!connected || pixmap.track == null) {
                System.err.println("el track no se inicializo");
                return;
            }

            pixmap.pos.x = position.x;
            pixmap.pos.y = pixmap.track.getHeight() - position.y;
            pixmap.angle = -position.z - Math.toRadians(90);
        });
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
            for(Sprite3D sprite : pixmap.entities) {
                if(sprite.id == id) {
                    existing = sprite;
                    break;
                }
            }
            if(existing != null) {
                existing.position.set(x, y);
//                safeReplacePixmap(existing, path);
                existing.replacePixmap(path, pixmapCache, flip);
            } else {
                System.err.println("no existe el sprite");
            }
        }
    }
//    private void safeReplacePixmap(Sprite3D sprite, String path) {
//        FileHandle newFile = Gdx.files.internal(path);
//
//        if(!newFile.exists()) {
//            System.err.println("no existe el archivo");
//            return;
//        }
//
//        if(sprite.file != null && sprite.file.path().equals(path)) {
//            return;
//        }
//        long now = TimeUtils.millis();
//        if(now - sprite.lastPixmapUpdate < 100) return;
//        sprite.lastPixmapUpdate = now;
//        Gdx.app.postRunnable(() -> {
//            try{
//
//                if(sprite.pixmap != null) {
//                    sprite.pixmap.dispose();
//                }
//                Pixmap cached = getOrLoadPixmap(path);
//                Pixmap copy = new Pixmap(cached.getWidth(), cached.getHeight(), cached.getFormat());
//                copy.drawPixmap(cached, 0, 0);
//                sprite.pixmap = copy;
//                sprite.file = newFile;
//                System.err.println("Reemplazando pixmap de sprite ID: " + sprite.id + " con path: " + path);
//            } catch(Exception e){
//                System.err.println("error al replace sprite: "+ e.getMessage());
//            }
//        });
//    }

//    private Pixmap getOrLoadPixmap(String path) {
//        if(!pixmapCache.containsKey(path)) {
//            pixmapCache.put(path, new Pixmap(Gdx.files.internal(path)));
//        }
//        return pixmapCache.get(path);
//    }

    @Override
    public void dispose() {
//        for(Sprite3D sprite : pixmap.entities) {
//            if(sprite.pixmap != null) {
//                sprite.pixmap.dispose();
//            }
//        }
        for(Pixmap cached : pixmapCache.values()) {
            cached.dispose();
        }
        pixmapCache.clear();
    }
}
