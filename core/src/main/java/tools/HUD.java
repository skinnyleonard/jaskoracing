package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable{
    public Stage stage;
    private Viewport viewport;

    public static Label notifLabel;
    public static Label lapLabel;
    public static Label gridLabel;

    public HUD(SpriteBatch sb) {
        viewport = new ExtendViewport(640, 480, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/speed.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        BitmapFont font = generator.generateFont(parameter);

        notifLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.BLUE));
        lapLabel = new Label("lapLabel" ,new Label.LabelStyle(font, Color.YELLOW));
        gridLabel = new Label("cargando posiciones" , new Label.LabelStyle(font, Color.WHITE));

        table.add(notifLabel).expandX().padTop(10);
        table.add(lapLabel).expandX().padTop(10);
        table.row();
        table.add(gridLabel).left().padLeft(10).padTop(50);

        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
