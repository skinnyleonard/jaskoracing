package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import tools.*;

public class EndingScreen extends ScreenAdapter {
    private static Game game;
    private SpriteBatch batch;
    private Text[] options = new Text[2];
    private String[] optionStrings = {"Salir del juego", "Volver a jugar"};
    private int opc;
    private float tiempo = 0;
    private GameScreen gameScreen;
    private Sprite sprite;
    private OrthographicCamera camera;
    private Viewport viewport;

    public EndingScreen(Game game) {
        this.game = game;
        batch = Render.batch;
    }

    public void passInfo(int leaderboard, GameScreen gameScreen) {
        this.gameScreen =  gameScreen;
        this.sprite = new Sprite(new Texture(Gdx.files.internal("posiciones/"+leaderboard+".png")));
    }

    @Override
    public void show() {
        int gap = 300;
        int startX = GameScreen.GAME_WIDTH*3 / 2 - 300;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameScreen.GAME_WIDTH*3, GameScreen.GAME_HEIGHT*3);
        camera.position.set(GameScreen.GAME_WIDTH*3/2, GameScreen.GAME_HEIGHT*3/2, 0);
        camera.update();
        viewport = new FitViewport(GameScreen.GAME_WIDTH*3, GameScreen.GAME_HEIGHT*3, camera);

//        this.sprite = new Sprite(new Texture(Gdx.files.internal("posiciones/8.png")));

        for(int i = 0; i < options.length; i++){
            options[i] = new Text("fuentes/speed.otf", 30, Color.WHITE);
            options[i].setText(optionStrings[i]);
            options[i].setPosition(startX+(i*gap),  (GameScreen.GAME_HEIGHT*3/4));
            System.out.println(startX);
        }
    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        camera.update();
        batch.begin();
        for(int i=0; i < options.length; i++){
            if(i==opc)
                options[i].setColor(Color.GREEN);
            else
                options[i].setColor(Color.WHITE);

            options[i].draw();
        }

        batch.draw(sprite, ((GameScreen.GAME_WIDTH*3/2)-(sprite.getWidth()/2)), GameScreen.GAME_HEIGHT*3-sprite.getHeight()-50, sprite.getWidth(), sprite.getHeight());
        batch.end();
        tiempo += delta;

        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT) && tiempo > 0.4f){
            System.out.println(opc);
            tiempo = 0;
            opc++;
            if(opc > options.length-1) opc = 0;
        }
        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT) && tiempo > 0.4f){
            System.out.println(opc);
            tiempo = 0;
            opc--;
            if(opc < 0) opc = options.length-1;
        }

        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ENTER)){
            if(opc == 1) {
                gameScreen.client.finish();
                game.setScreen(new MenuScreen());
                Gdx.graphics.setWindowedMode(GameScreen.GAME_WIDTH*3, GameScreen.GAME_HEIGHT*3);
            }
            if(opc == 0) {
                gameScreen.client.finish();
                System.exit(0);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        gameScreen.client.finish();
    }
}
