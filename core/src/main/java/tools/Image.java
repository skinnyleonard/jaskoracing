package tools;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Image {
    private Texture t;
    private Sprite s;

    public Image(String ruta){
        t = new Texture(ruta);
        s = new Sprite(t);

    }
    public void draw(){
        s.draw(Render.batch);
    }

    public void setSize (float ancho, float alto){
        s.setSize(ancho, alto);
    }

}
