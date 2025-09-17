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

        System.out.println(fixA.getUserData());

        if ( (fixA.getUserData() == "car" && fixB.getUserData() == ("check9") ||
            (fixA.getUserData() == ("check9") && fixB.getUserData() == "car"))) {
            System.out.println("Checkpoint");
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
