package io.github.libgdxsnes;

import com.badlogic.gdx.Gdx;
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
    public float diffx;
    public float diffy;

    public ArrayList<Sprite3D> entities;
    public Vector2 entScale;

    public Pixmap3D(int width, int height, Format format) {
        super(width, height, format);
        setFilter(Filter.NearestNeighbour);

        pixmapTex = new Texture(this, getFormat(), true);
        horizon = 40;
//        bg = new Texture("bg.png");
        bg = new Texture("anotherbg.png");
        grass = new Pixmap(Gdx.files.internal("grass.png"));
        track = new Pixmap(Gdx.files.internal("mapa.png"));
        pos = new Vector3(652, 2000, 140);
        scale = new Vector3(300, 300, 0);
        angle = 0;

        bgPos = -256;

        entities = new ArrayList<Sprite3D>();
        entScale = new Vector2(0.20f, 0.52f);

//        Pixmap examplePixmap = new Pixmap(Gdx.files.internal("cars/quattro/1.png"));
//        entities.add(new Sprite3D(examplePixmap, 652, 2000, 0));
    }

    public void render(SpriteBatch batch) {
        drawGround();
        drawEntities(batch);
        pixmapTex.draw(this, 0, 0);
        batch.draw(pixmapTex, 0, 0);
        batch.draw(bg, bgPos, GameScreen.GAME_HEIGHT - 40);
//        System.out.println("x: "+pos.x+", y: "+pos.y+" angle: "+angle);
    }
    private void drawGround() {
        double dirx = Math.cos(angle);
        double diry = Math.sin(angle);

        int centerScreenX = getWidth() / 2;
        int targetScreenY = getHeight() - 1;

        for(int screeny = horizon; screeny < getHeight(); screeny++) {
            double distanceInWorldSpace = pos.z*scale.y / ((double)screeny-horizon);
            double deltax = -diry * (distanceInWorldSpace/scale.x);
            double deltay = dirx * (distanceInWorldSpace/scale.y);

            //these numbers calculate the position in the track in 3d mode
            double spacex = pos.x + dirx * distanceInWorldSpace - (double) getWidth() /2 * deltax;
            double spacey = pos.y + diry * distanceInWorldSpace - (double) getHeight() /2 * deltay;

            for(int screenx=0; screenx < getWidth(); screenx++) {
                if (screenx == centerScreenX && screeny == targetScreenY) {
                    HUD.lapLabel.setText("difference (x:" + ((int)spacex - (int)pos.x) + ", y:" + ((int)spacey - (int)pos.y) + ")");
                }
                diffx = ((int)spacex - (int)pos.x);
                diffy = ((int)spacey - (int)pos.y);

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


            if(rotx <= 0.01) {
//                System.out.println("auto descartado por rotx=" + rotx);
                continue;
            }

            int w = entity.pixmap.getWidth()/10;
            int h = entity.pixmap.getHeight()/10;
//            System.out.println("width: "+w+" height: "+h);

            int projectedKartWidth = (int)(w * 300 / rotx * 1);
            int projectedKartHeight = (int)(h * 277 / rotx * 1);

            if(projectedKartHeight < 1 || projectedKartWidth < 1) {
                continue;
            }

            int spriteScreenX = (int)(scale.x / rotx * roty) + getWidth() /2;
            int spriteScreenY = (int) ((pos.z * scale.y) / rotx + horizon);
//            int spriteScreenY = (int) ((120 * scale.y) / rotx + horizon);


            entity.screen.x = spriteScreenX - projectedKartWidth / 2;
            entity.screen.y = spriteScreenY - projectedKartHeight;
            entity.size.x = projectedKartWidth;
            entity.size.y = projectedKartHeight;
            entity.sort = spriteScreenY;
//
//            entity.screen.set(spriteScreenX - projectedKartWidth/2f, spriteScreenY - projectedKartHeight);
//            entity.size.set(projectedKartWidth, projectedKartHeight);
//            entity.sort = spriteScreenY;
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
//            System.out.println("Dibujando auto en pantalla: x=" + x + " y=" + y + " w=" + w + " h=" + h);
//            System.out.println("Render ID: " + kart.id + " | pixmap hash: " + kart.pixmap.hashCode());
            drawPixmap(kart.pixmap, 0, 0, sw, sh, x, y, w, h);
        }
    }

    @Override
    public void dispose() {
        for(Sprite3D sprite : entities) {
            if(sprite.pixmap != null) {
                sprite.pixmap.dispose();
            }
        }
        entities.clear();

        if(track != null) track.dispose();
        if(grass != null) grass.dispose();
        if(pixmapTex != null) pixmapTex.dispose();
    }

    public Vector2 projectToScreen(double worldX, double worldY, Vector3 pos, double angle, Vector2 scale, int horizon, int screenWidth, int screenHeight) {
        double dirx = Math.cos(angle);
        double diry = Math.sin(angle);

        for (int screenY = horizon; screenY < screenHeight; screenY++) {
            double distance = pos.z * scale.y / (screenY - horizon);
            double deltax = -diry * (distance / scale.x);
            double deltay =  dirx * (distance / scale.y);

            double startX = pos.x + dirx * distance - screenWidth / 2.0 * deltax;
            double startY = pos.y + diry * distance - screenHeight / 2.0 * deltay;

            double spacex = startX;
            double spacey = startY;

            for (int screenX = 0; screenX < screenWidth; screenX++) {
                if ((int)spacex == (int)worldX && (int)spacey == (int)worldY) {
                    return new Vector2(screenX, screenY);
                }
                spacex += deltax;
                spacey += deltay;
            }
        }
        return null; // No se encontró
    }
}
