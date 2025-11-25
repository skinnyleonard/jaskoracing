package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tools.Image;
import tools.Render;

public class CarSelectScreen implements Screen {

    private Game game;
    private SpriteBatch b;
    private Image bg;

    private Image[] cars;
    private int carIndex = 0;

    private float time = 0;
    private boolean canConfirm = false;
    private float delayChangeScreen = -1;
    private String playerName;

    private String[] carsNames = {
            "alpine", "quattro", "porsche", "minicooper",
            "lanciadeltahf", "escort", "celica", "subaru"
    };

    String carBrand;

    public CarSelectScreen(Game game, String playerName) {
        this.game = game;
        this.playerName = playerName;
    }

    @Override
    public void show() {
        b = Render.batch;

        bg = new Image("fondos/fondoAutos.png");
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        cars = new Image[]{
                new Image("cars/alpine/5.png"),
                new Image("cars/quattro/5.png"),
                new Image("cars/porsche/5.png"),
                new Image("cars/minicooper/5.png"),
                new Image("cars/lanciadeltahf/5.png"),
                new Image("cars/escort/5.png"),
                new Image("cars/celica/5.png"),
                new Image("cars/subaru/5.png")
        };

        time = 0;
        canConfirm = false;
        delayChangeScreen = -1;
    }

    @Override
    public void render(float delta) {
        Render.cleanScreen(0, 0, 0);
        time += delta;

        if (time > 0.3f) canConfirm = true;

        b.begin();
        bg.draw();

        Image currentImage = cars[carIndex];
        float width = Gdx.graphics.getWidth() * 0.5f;
        float height = Gdx.graphics.getHeight() * 0.5f;
        float x = (Gdx.graphics.getWidth() - width) / 2;
        float y = (Gdx.graphics.getHeight() - height) / 2;

        currentImage.setSize(width, height);
        currentImage.s.setPosition(x, y);
        currentImage.draw();
        b.end();

        if (delayChangeScreen > 0) {
            delayChangeScreen -= delta;
            if (delayChangeScreen <= 0) {
                game.setScreen(new MapSelectScreen(game, carBrand, playerName));
                return;
            }
        }

        if (time > 0.15f) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                carIndex = (carIndex + 1) % cars.length;
                time = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                carIndex = (carIndex - 1 + cars.length) % cars.length;
                time = 0;
            }
        }

        if (canConfirm && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            carBrand = carsNames[carIndex];
            System.out.println("Elegiste: " + carsNames[carIndex]);
            canConfirm = false;
            delayChangeScreen = 0.4f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen());
        }
    }

    @Override public void resize(int width, int height) { bg.setSize(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
