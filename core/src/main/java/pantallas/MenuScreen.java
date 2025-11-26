package pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tools.Input;
import tools.Image;
import tools.Text;
import io.github.libgdxsnes.Main;
import tools.Constants;
import tools.Render;

public class MenuScreen implements Screen {
    private Game game;
    private Image bg;
    private SpriteBatch b;

    private Text[] options = new Text[4];
    private String[] textos = {"Jugar", "Online", "Opciones", "Salir"};
    private int opc = 0;
    public float time = 0;

    private Input input;

    @Override
    public void show() {
        b = Render.batch;
        game = (Main) Gdx.app.getApplicationListener();

        bg = new Image(Constants.MENUBG);
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        input = new Input(this);
        Gdx.input.setInputProcessor(input);

        int startY = 400;
        int gap = 90;

        for (int i = 0; i < options.length; i++) {
            options[i] = new Text(Constants.MENUFONT, 60, Color.WHITE);
            options[i].setText(textos[i]);
            options[i].setPosition(400, startY - (i * gap));
        }
    }

    @Override
    public void render(float delta) {
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Render.cleanScreen(0,0,0);
        b.begin();
        bg.draw();

        for (int i = 0; i < options.length; i++) {
            if (i == opc) options[i].setColor(Color.RED);
            else options[i].setColor(Color.WHITE);

            options[i].draw();
        }
        b.end();
        time += delta;


        if (input.isDown() && time > 0.09f) {
            time = 0;
            opc++;
            if (opc > 3) opc = 0;
        }
        if (input.isUp() && time > 0.09f) {
            time = 0;
            opc--;
            if (opc < 0) opc = 3;
        }

        if (input.isEnter()) {
            if (opc == 0) {
                game.setScreen(new CarSelectScreen(game, null));
            } else if (opc == 1) {
                game.setScreen(new UsernameScreen(game));
            } else if (opc == 2) {
                game.setScreen(new ModeSelectScreen(game));
            } else if (opc == 3) {
                Gdx.app.exit();
            }
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
