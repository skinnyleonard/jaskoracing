package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tools.Constants;
import tools.Text;
import tools.Image;
import tools.Render;

public class ModeSelectScreen implements Screen {

    private Game game;
    private SpriteBatch b;
    private Image bg;

    private Text[] options = new Text[3];
    private String[] texts = {"F1", "Rally", "Drift"};
    private int opc = 0;
    private float time = 0;

    public ModeSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        b = Render.batch;


        bg = new Image("fondos/fondoOpciones.png");
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int startX = Gdx.graphics.getWidth() / 2 - 300;
        int y = Gdx.graphics.getHeight() / 2;
        int gap = 300;

        for (int i = 0; i < options.length; i++) {
            options[i] = new Text(Constants.MENUFONT, 80, Color.WHITE);
            options[i].setText(texts[i]);
            options[i].setPosition(startX + i * gap, y);
        }
    }

    @Override
    public void render(float delta) {
        Render.cleanScreen(0, 0, 0);
        time += delta;

        b.begin();
        bg.draw();

        for (int i = 0; i < options.length; i++) {
            if (i == opc) options[i].setColor(Color.RED);
            else options[i].setColor(Color.WHITE);

            options[i].draw();
        }
        b.end();

        if (time > 0.15f) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                opc++;
                if (opc >= options.length) opc = 0;
                time = 0;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                opc--;
                if (opc < 0) opc = options.length - 1;
                time = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            System.out.println("Modo de juego seleccionado: " + texts[opc]);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen());
        }
    }

    @Override
    public void resize(int width, int height) {
        bg.setSize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
