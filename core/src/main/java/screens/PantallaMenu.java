package screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.jasko.Main;
import tools.*;

public class PantallaMenu implements Screen {
    private static Game game;
    public OrthographicCamera gamecam;

    Imagen fondo;
    SpriteBatch b;

    Texto[] opciones = new Texto[4];
    String[] textos = {" Jugar", "Online", "Opciones", "Salir"};

    int opc = 0;
    public float tiempo = 0;
    private final Viewport viewport;

    Entradas entradas = new Entradas(this);

    public PantallaMenu(Game game) {
        this.game = game;
        viewport = new ExtendViewport(640, 480, new OrthographicCamera());
    }

    @Override
    public void show() {
        fondo = new Imagen(Constants.FONDOMENU);
        // Ajusta la imagen al tamaño real de la ventana
        fondo.setSize((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/2);


        b = Render.batch;

        Gdx.input.setInputProcessor(entradas);

        int startY = 400;
        int gap = 90;
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = new Texto(Constants.FUENTEMENU, 50, Color.WHITE);
            opciones[i].setTexto(textos[i]);
            opciones[i].setPosition(Gdx.graphics.getHeight()/2, startY - (i * gap));
        }
    }

    @Override
    public void render(float delta) {
        // Actualiza el tamaño del fondo por si cambia la ventana
        fondo.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        b.begin();
        fondo.dibujar();
        for (int i = 0; i < opciones.length; i++) {
            opciones[i].dibujar();
        }
        b.end();

        tiempo += delta;

        if (entradas.isAbajo()) {
            if (tiempo > 0.09f) {
                tiempo = 0;
                opc++;
                if (opc > 3) opc = 0;
            }
        }
        if (entradas.isArriba()) {
            if (tiempo > 0.09f) {
                tiempo = 0;
                opc--;
                if (opc < 0) opc = 3;
            }
        }

        for (int i = 0; i < opciones.length; i++) {
            if (i == opc) {
                opciones[i].setColor(Color.RED);
            } else {
                opciones[i].setColor(Color.WHITE);
            }
        }

        if (entradas.isEnter()) {
            if (opc == 0) {
                game.setScreen(new PlayScreen());
            } else if (opc == 1) {
                System.out.println("Llamar al juego online");
            } else if (opc == 2) {
                System.out.println("Llamar a opciones");
            } else if (opc == 3) {
                System.out.println("Salir del juego");
                Gdx.app.exit();
            }
        }
    }

    @Override public void resize(int width, int height) {
        // Actualizo también acá por si se cambia el tamaño
        fondo.setSize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
