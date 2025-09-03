package tools;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import screens.Constants;
import screens.PlayScreen;

public class MapLoader implements Disposable {
    private static final String MAP_WALL = "wall";
    private static final String MAP_PLAYER = "player";

    private final World mWorld;
    private final TiledMap mMap;

    public MapLoader(World world) {
        this.mWorld = world;
        mMap = new TmxMapLoader().load(Constants.MAP_NAME);

        //final Array<RectangleMapObject> walls = mMap.getLayers().get(MAP_WALL).getObjects().getByType(RectangleMapObject.class);
        for (MapObject rObject : mMap.getLayers().get(MAP_WALL).getObjects()) {
            if(rObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) rObject).getRectangle();
                ShapeFactory.createRectangle(
                    new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2),
                    new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2),
                    BodyDef.BodyType.StaticBody, mWorld, 1f, false);
            }

            if(rObject instanceof PolygonMapObject) {
                float[] vertices = ((PolygonMapObject) rObject).getPolygon().getTransformedVertices();
                float[] worldVertices = new float[vertices.length];

                for (int i = 0; i < vertices.length; i++) {
                    worldVertices[i] = vertices[i] / Constants.PPM;
                }
                ShapeFactory.createPolygon(worldVertices, BodyDef.BodyType.StaticBody, mWorld, 1f, false);
            }

            if(rObject instanceof PolylineMapObject) {
                float[] vertices = ((PolylineMapObject)rObject).getPolyline().getTransformedVertices();
                Vector2[] worldVertices = new Vector2[vertices.length / 2];

                for (int i = 0; i < vertices.length / 2; i++) {
                    worldVertices[i] = new Vector2();
                    worldVertices[i].x = vertices[i*2]/Constants.PPM;
                    worldVertices[i].y = vertices[i*2+1]/Constants.PPM;
                }
                ShapeFactory.createPolyline(
                    worldVertices, BodyDef.BodyType.StaticBody, world, 1f, false
                );
            }
        }
//        System.out.println(mMap.getLayers().get("bg").getProperties().get("pijja", String.class));

        for(MapLayer rObject : mMap.getLayers()) {
            if(rObject instanceof TiledMapImageLayer) {
                TiledMapImageLayer textureObj = (TiledMapImageLayer) rObject;
                float x = textureObj.getX() * Constants.PPM;
                System.out.println(textureObj.getTextureRegion().getRegionWidth() + "x" + textureObj.getTextureRegion().getRegionHeight());
                float y = textureObj.getY() * Constants.PPM;
                float width = textureObj.getTextureRegion().getRegionWidth() * Constants.PPM;
                float height = textureObj.getTextureRegion().getRegionHeight() * Constants.PPM;
                PlayScreen.batch.begin();
                PlayScreen.batch.draw(textureObj.getTextureRegion(), x, y, width, height);
                PlayScreen.batch.end();
            }
        }
    }

    public Body getPlayers() {
        final Rectangle rectangle = mMap.getLayers().get(MAP_PLAYER).getObjects().getByType(RectangleMapObject.class).get(0).getRectangle();
        return ShapeFactory.createRectangle(
            new Vector2(rectangle.getX() + rectangle.getWidth() / 2, rectangle.getY() + rectangle.getHeight() / 2),
            new Vector2(rectangle.getWidth() / 2, rectangle.getHeight() / 2),
            BodyDef.BodyType.DynamicBody, mWorld, 0.4f, false);
    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
