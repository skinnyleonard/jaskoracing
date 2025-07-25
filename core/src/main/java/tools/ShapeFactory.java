package tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import screens.Constants;

public class ShapeFactory {
	private ShapeFactory() {}

	public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density, boolean sensor) {
		final BodyDef bdef = new BodyDef();
		bdef.position.set(position.x / Constants.PPM, position.y / Constants.PPM);
		bdef.type = type;
		final Body body = world.createBody(bdef);

		final PolygonShape shape = new PolygonShape();
		shape.setAsBox(size.x / Constants.PPM, size.y / Constants.PPM);
		final FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.density = density;
        fdef.isSensor = sensor;

		body.createFixture(fdef);
		shape.dispose();

		return body;
	}
}
