package io.github.libgdxsnes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;

public class Pixmap3D extends Pixmap {
    private Texture bg;
    private Texture pixmapTex;
    private int horizon;
    public double angle;
    private Pixmap grass;
    public Pixmap track;
    public Vector3 pos;
    private Vector3 scale;
    public float bgPos;

    public ArrayList<Sprite3D> entities;
    public Vector2 entScale;

    public Pixmap3D(int width, int height, Format format) {
        super(width, height, format);
        setFilter(Filter.NearestNeighbour);

        pixmapTex = new Texture(this, getFormat(), true);
        horizon = 40;
        bg = new Texture("bg.png");
        grass = new Pixmap(Gdx.files.internal("grass.png"));
        track = new Pixmap(Gdx.files.internal("mapa.png"));
        pos = new Vector3(652, 2000, 32);
        scale = new Vector3(300, 300, 0);
        angle = 0;

        bgPos = -256;

        entities = new ArrayList<Sprite3D>();
        entScale = new Vector2(0.20f, 0.52f);

        entities.add(new Sprite3D(new Pixmap(Gdx.files.internal("propaganda.png")), 839, 3010));
    }

    public void render(SpriteBatch batch) {
        drawGround();
        drawEntities(batch);
        pixmapTex.draw(this, 0, 0);
        batch.draw(pixmapTex, 0, 0);
        batch.draw(bg, bgPos, GameScreen.GAME_HEIGHT - 40);
        System.out.println("x: "+pos.x+", y: "+pos.y+" angle: "+angle);
    }

    private void drawGround() {
        double dirx = Math.cos(angle);
        double diry = Math.sin(angle);

        for(int screeny = horizon; screeny < getHeight(); screeny++) {
            double distanceInWorldSpace = pos.z*scale.y / ((double)screeny-horizon);
            double deltax = -diry * (distanceInWorldSpace/scale.x);
            double deltay = dirx * (distanceInWorldSpace/scale.y);

            double spacex = pos.x + dirx * distanceInWorldSpace - getWidth() /2 * deltax;
            double spacey = pos.y + diry * distanceInWorldSpace - getHeight() /2 * deltay;

            for(int screenx=0; screenx < getWidth(); screenx++) {
                setColor(grass.getPixel(((int) Math.abs(spacex % grass.getWidth())), (int) Math.abs(spacey % grass.getHeight())));
                drawPixel(screenx, screeny);

                setColor(track.getPixel((int)spacex, (int)spacey));
                drawPixel(screenx, screeny);

                spacex += deltax;
                spacey += deltay;
            }
        }
    }

    private void drawEntities(SpriteBatch batch) {
        double dirx = Math.cos(angle);
        double diry = Math.sin(angle);

        ArrayList<Sprite3D> entitiesSorted = new ArrayList<>();

        for (Sprite3D entity : entities) {
            double diffBetweenPlayerposAndSpriteposX = entity.position.x - pos.x;
            double diffBetweenPlayerposAndSpriteposY = entity.position.y - pos.y;

            double rotx = diffBetweenPlayerposAndSpriteposX * dirx + diffBetweenPlayerposAndSpriteposY * diry;
            double roty = diffBetweenPlayerposAndSpriteposY * dirx - diffBetweenPlayerposAndSpriteposX * diry;

            int w = entity.pixmap.getWidth();
            int h = entity.pixmap.getHeight();
            int projectedKartWidth = (int)(w * scale.x / rotx * entScale.x);
            int projectedKartHeight = (int)(h * scale.y / rotx * entScale.y);

            if(projectedKartHeight < 1 || projectedKartWidth < 1) {
                continue;
            }

            int spriteScreenX = (int)(scale.x / rotx * roty) + getWidth() /2;
            int spriteScreenY = (int) ((pos.z * scale.y) / rotx + horizon);

            entity.screen.x = spriteScreenX - projectedKartWidth / 2;
            entity.screen.y = spriteScreenY - projectedKartHeight;
            entity.size.x = projectedKartWidth;
            entity.size.y = projectedKartHeight;
            entity.sort = spriteScreenY;

            entitiesSorted.add(entity);
        }

        Collections.sort(entitiesSorted);

        for(Sprite3D kart : entitiesSorted) {
            int sw = kart.pixmap.getWidth();
            int sh = kart.pixmap.getHeight();
            int x = (int) kart.screen.x;
            int y = (int) kart.screen.y;
            int w = (int) kart.size.x;
            int h = (int) kart.size.y;

            drawPixmap(kart.pixmap, 0, 0, sw, sh, x, y, w, h);
        }
    }
}
