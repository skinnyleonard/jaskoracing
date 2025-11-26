package tools;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Image {
    public Texture t;
    public Sprite s;

    public Image(String route){
        t = new Texture(route);
        s = new Sprite(t);

    }
    public void draw(){
        s.draw(Render.batch);
    }

    public void setSize (float width, float height){
        s.setSize(width, height);
    }
}
