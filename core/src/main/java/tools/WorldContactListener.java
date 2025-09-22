package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import tools.InteractiveTileObject;

public class WorldContactListener implements ContactListener{
	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

        if ( (fixA.getUserData() == "car" && fixB.getUserData() == "check") ||
            (fixA.getUserData() == "check" && fixB.getUserData() == "car") ) {
            System.out.println("Checkpoint reached!");
        }
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManiFold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
