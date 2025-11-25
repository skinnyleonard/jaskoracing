package tools;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Image {
    private Texture t;
    public Sprite s;

    public Image(String ruta){
        t = new Texture(ruta);
        s = new Sprite(t);

    }
    public void draw(){
        s.draw(Render.batch);
    }

    public void setSize (float width, float height){
        s.setSize(width, height);
    }
}
