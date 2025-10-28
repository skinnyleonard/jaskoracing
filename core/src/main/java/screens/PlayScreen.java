package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import entities.Car;
import io.github.jasko.Main;
import online.NetManager;
import online.Server;
import online.User;
import tools.HUD;
import tools.MapLoader;
import static entities.Car.*;
import tools.WorldContactListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PlayScreen implements Screen, NetManager {

    public static SpriteBatch batch;
    private final World world;
    private final Box2DDebugRenderer b2dr;
    private final OrthographicCamera camera;
    private final Viewport viewport;
//    private final Car player;
    public final MapLoader mapLoader;
    private static Main game;
    private HUD hud;
    private Server server;
    DecimalFormat df = new DecimalFormat("0.00");
    private ArrayList<Car> players = new ArrayList<Car>();
    private ArrayList<String> usersSprites = new ArrayList<String>();
    private long lastMessageTime = -1;

    private int turnImage = 1;
    private final String carBrand = "quattro";
    private int currentFrame = 0;
    public float frameTimer = 0f;
    private float frameDuration = 0.1f;

    public PlayScreen() {
        batch = new SpriteBatch();
        world = new World(Constants.GRAVITY, true);
        b2dr = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.zoom = Constants.DEFUALT_ZOOM;
        hud = new HUD(batch);
        viewport = new ExtendViewport(640 / Constants.PPM, 480 / Constants.PPM, camera);
        mapLoader = new MapLoader(world);
//        player = new Car(35.0f, 0.8f, 60, mapLoader, DRIVE_4WD, world, 652, 4912);
        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void show() {
        server = new Server(this);
        server.start();
        server.pingEveryone("hola bellos");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        handleInput();
        if(players.isEmpty()) {
//            System.out.println("pis");
        } else {
            moveCar(Server.move, Server.clientIndexed);
        }
        update(delta);
        draw();
        hud.stage.draw();
    }

//    private void handleInput() {
//        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            player.setDriveDirection(DRIVE_DIRECTION_FORWARD);
//        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            player.setDriveDirection(DRIVE_DIRECTION_BACKWARD);
//        } else {
//            player.setDriveDirection(DRIVE_DIRECTION_NONE);
//        }
//
//        //CON FINES DE PRUEBA
//        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
//            //CON FINES DE TESTEO BORRAR MAS TARDE POR FAVOR
//            getJSON(generatePositionsJSON());
//            //GRACIAS
//        }
//        //GRACIAS POR LEER
//
//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            player.setTurnDirection(TURN_DIRECTION_LEFT);
//        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            player.setTurnDirection(TURN_DIRECTION_RIGHT);
//        } else {
//            player.setTurnDirection(TURN_DIRECTION_NONE);
//        }
//
//        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
//            Gdx.app.exit();
//        }
//
//        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
//            camera.zoom -= 0.4f;
//        } else if(Gdx.input.isKeyPressed(Input.Keys.E)) {
//            camera.zoom += 0.4f;
//        }
//    }

    //ACA ESTOY PROBANDO EL JSON COMO FUNCIONA, PROBABLEMENTE LE DE UN MEJOR USO
    private String generatePositionsJSON() {
        StringWriter sw = new StringWriter();
        JsonWriter writter = new JsonWriter(sw);
        writter.setOutputType(JsonWriter.OutputType.json);

        try{
            writter.object();
            writter.name("players").array();

            int i=0;
            for(Car car : players) {
                writter.object();
                writter.name("id").value(i);
                writter.name("x").value(Float.parseFloat(car.getMetrics().split("%")[0]));
                writter.name("y").value(Float.parseFloat(car.getMetrics().split("%")[1]));
                writter.name("path").value("cars/"+carBrand+"/"+car.imageIterationNumber+".png");
                writter.name("flip").value(car.flip);
                writter.pop();
                i++;
            }
            writter.pop();
            writter.pop();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    private void getJSON(String jsonText) {
        JsonReader reader =  new JsonReader();
        JsonValue root = reader.parse(jsonText);

        JsonValue players = root.get("players");

        System.out.println(root.prettyPrint(JsonWriter.OutputType.json, 1));
        for(JsonValue player : players) {
            String id = player.get("id").asString();
            float x =  player.get("x").asFloat();
            float y =  player.get("y").asFloat();
            String path = player.get("path").asString();

//            System.out.println("id: " +id+", x: "+x+", y: "+y+", path: "+path);
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.combined);
        b2dr.render(world, camera.combined);
    }

    private void update(final float delta) {
//        player.update(delta);
        for(Car car : players) {
            car.update(delta);
        }
//        int i = 0;
//        for(User user : server.users) {
//            server.sendMessage("updatePos;"+players.get(i).getMetrics(), user.getIp(), user.getPort());
//            i++;
//        }
        try{
            for(int i = 0; i<Math.min(server.users.size(), players.size()); i++) {
                User user = server.users.get(i);
                Car car = players.get(i);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);

                float x = Float.parseFloat(car.getMetrics().split("%")[0]);
                float y = Float.parseFloat(car.getMetrics().split("%")[1]);
                float angle = Float.parseFloat(car.getMetrics().split("%")[2]);

//                System.out.println("x: "+x+" y: "+y+" angle: "+angle);

                dos.writeUTF("updatePos");
                dos.writeFloat(x);
                dos.writeFloat(y);
                dos.writeFloat(angle);
                dos.flush();

                byte[] data = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(
                    data, data.length, user.getIp(), user.getPort()
                );
                server.socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.server.pingEveryone("updateOthersPos;"+generatePositionsJSON());
        camera.position.set(new Vector2(12800/2f, 6912/2f), 0);
        camera.setToOrtho(false, 640/Constants.PPM*3, 480/Constants.PPM*3);
        camera.update();
//        player.getMetrics();
//        server.pingEveryone("hola bellos");
//        this.server.pingEveryone("updatePos;"+player.getMetrics());
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

    @Override
    public void connect(boolean state) {

    }

    @Override
    public void moveCar(String move, int client) {
        if(move.equals("up")) {
            players.get(client).setDriveDirection(DRIVE_DIRECTION_FORWARD);
            players.get(client).imageIterationNumber = 1;
            players.get(client).flip = false;
            System.out.println("se mueve up");
        } else if(move.equals("down")) {
            players.get(client).setDriveDirection(DRIVE_DIRECTION_BACKWARD);
            players.get(client).imageIterationNumber = 1;
            players.get(client).flip = false;
            System.out.println("se mueve down");
        } else if(move.equals("afk")) {
            players.get(client).setDriveDirection(DRIVE_DIRECTION_NONE);
            players.get(client).imageIterationNumber = 1;
            players.get(client).flip = false;
        }

        if(move.equals("left")) {
            players.get(client).setTurnDirection(TURN_DIRECTION_LEFT);
            players.get(client).imageIterationNumber = 3;
            players.get(client).flip = false;
            System.out.println("se mueve left");
        } else if(move.equals("right")) {
            players.get(client).setTurnDirection(TURN_DIRECTION_RIGHT);
            players.get(client).imageIterationNumber = 3;
            players.get(client).flip = true;
            System.out.println("se mueve right");
        } else if(move.equals("afk")) {
            players.get(client).setTurnDirection(TURN_DIRECTION_NONE);
            players.get(client).imageIterationNumber = 1;
            players.get(client).flip = false;
        }
    }

    @Override
    public void placeNewPlayer(int connectedUsers) {
        int arrPosition = mapLoader.positions.size() - (connectedUsers);
        float x = mapLoader.positions.get(arrPosition).getPoint().x;
        float y = mapLoader.positions.get(arrPosition).getPoint().y;
        players.add(new Car(35.0f, 0.8f, 60, mapLoader, DRIVE_4WD, world, x, y));
        this.usersSprites.add("cars/"+carBrand+"/1.png");
    }

    @Override
    public String getNewPlayerPos(int connectedUsers) {
        return "newCar;"+generatePositionsJSON();
    }


    @Override
    public String updateMetrics(int indexUser) {
        return "updatePos;"+players.get(indexUser).getMetrics();
    }
}
