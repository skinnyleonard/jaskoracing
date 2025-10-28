package io.github.libgdxsnes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Map;

public class Sprite3D implements Comparable<Sprite3D>{
    public int id;
    public Pixmap pixmap;
    public int sort;
    public Vector2 screen;
    public Vector2 size;
    public Vector2 position;
    public FileHandle file;
    public long lastPixmapUpdate = 0;

    public Sprite3D(Pixmap pixmap, float posx, float posy, int id){
        this.id = id;
        this.file = file;
//        this.pixmap = new Pixmap(file);
        this.pixmap = pixmap;
        screen = new Vector2();
        size = new Vector2();
        position = new Vector2(posx, posy);
    }

    private Pixmap flipPixmap(Pixmap orignal){
        int width = orignal.getWidth();
        int height = orignal.getHeight();
        Pixmap flipped = new Pixmap(width, height, orignal.getFormat());

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = orignal.getPixel(x, y);
                flipped.drawPixel(width-x-1, y, pixel);
            }
        }
        return flipped;
    }

    public void replacePixmap(String path, Map<String, Pixmap> cache, boolean flip){
        FileHandle newFile = Gdx.files.internal(path);
//        if(!newFile.exists()){
//            System.err.println("el path no existe"+path);
//            return;
//        }
        if(this.file != null && this.file.path().equals(path)) return;

        long now = TimeUtils.millis();
        if(now - this.lastPixmapUpdate < 100) return;
        this.lastPixmapUpdate = now;

        Gdx.app.postRunnable(() -> {
            try {
                if(this.pixmap != null) {
                    this.pixmap.dispose();
                }

                Pixmap cached;
                if(cache.containsKey(path)) {
                    cached = cache.get(path);
                } else {
                    cached = new Pixmap(newFile);
                    cache.put(path, cached);
                }

                Pixmap copy;
                if(flip) {
                    copy = flipPixmap(cached);
                } else {
                    copy = new Pixmap(cached.getWidth(), cached.getHeight(), cached.getFormat());
                    copy.drawPixmap(cached, 0, 0);
                }
                this.pixmap = copy;
                this.file = newFile;
            } catch (Exception e){
                System.err.println("error al replace sprite id "+this.id+ e.getMessage());
            }
        });
    }

    @Override
    public int compareTo(Sprite3D other) {
        return Integer.compare(this.sort, other.sort);
    }
}
