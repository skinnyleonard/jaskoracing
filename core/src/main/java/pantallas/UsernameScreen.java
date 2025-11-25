package pantallas;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tools.Image;
import tools.Render;

public class UsernameScreen implements Screen {

    private Game game;
    private SpriteBatch b;
    private Image bg;

    private BitmapFont font;
    private GlyphLayout layout;
    private StringBuilder playerName = new StringBuilder();

    public UsernameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        b = Render.batch;

        bg = new Image("fondos/fondoOnline.png");
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = new BitmapFont();
        layout = new GlyphLayout();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (character >= 32 && character <= 126 && playerName.length() < 12) {
                    playerName.append(character);
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.BACKSPACE && playerName.length() > 0) {
                    playerName.deleteCharAt(playerName.length() - 1);
                }

                if (keycode == Input.Keys.ENTER) {
                    System.out.println("Nombre confirmado: " + playerName.toString());

                    Gdx.input.setInputProcessor(null);
                    Gdx.input.setCursorCatched(false);
                    Gdx.input.setCursorPosition(0, 0);
                    Gdx.app.postRunnable(() -> game.setScreen(new CarSelectScreen(game, playerName.toString())));
                }

                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.input.setInputProcessor(null);
                    game.setScreen(new MenuScreen());
                }

                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Render.cleanScreen(0, 0, 0);

        b.begin();
        bg.draw();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        layout.setText(font, "Ingrese su nombre:");
        font.draw(b, layout, centerX - layout.width / 2, centerY + 100);

        layout.setText(font, playerName.toString());
        font.draw(b, layout, centerX - layout.width / 2, centerY);

        layout.setText(font, "(ENTER para confirmar, ESC para volver)");
        font.draw(b, layout, centerX - layout.width / 2, 100);

        b.end();
    }

    @Override
    public void resize(int width, int height) {
        bg.setSize(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}

