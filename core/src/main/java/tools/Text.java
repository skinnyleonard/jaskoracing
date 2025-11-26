package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Text {
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    BitmapFont font;
    private float x=0, y=0;
    private String text = "";
    GlyphLayout layout;

    public Text(String fontRoute, int dimension, Color color) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal(fontRoute));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = dimension;
        parameter.color = color;

        font = generator.generateFont(parameter);
        layout = new GlyphLayout();
    }

    public void draw() {
        font.draw(Render.batch, text, x, y);
    }

    public void setColor(Color color){
        font.setColor(color);
    }

    public void setPosition(float x, float y){
        this.x=x;
        this.y=y;
    }

    public void setText(String text) {
        this.text = text;
        layout.setText(font, text);
    }
}

