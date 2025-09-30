package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import tools.InteractiveTileObject;

public class WorldContactListener implements ContactListener{

    int checkCount = 1;
    int lapCount = 1;
    String col = "check";
    @Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

        String check = fixA.getUserData().toString();

        if (check.contains(col)) {
        if ( (fixA.getUserData() == "car" && fixB.getUserData() == (check) ||
            (fixA.getUserData() == (check) && fixB.getUserData() == "car"))) {

            if (checkCount+1 == Integer.parseInt(check.replaceAll("[^0-9]", ""))){
                checkCount++;
            }
            else if (checkCount == MapLoader.maxCheck && Integer.parseInt(check.replaceAll("[^0-9]", "")) == 1){

                checkCount = 1;
                if (lapCount == 3){
                    System.out.println("Llegaste a la meta papu");
                }
                lapCount = lapCount + 1;
            }
            System.out.println("checkcount: " + checkCount);
            System.out.println("lapcount: " + lapCount);
            }
	    }
        else if ( (fixA.getUserData() == "car" && fixB.getUserData() == ("pared") ||
            (fixA.getUserData() == ("pared") && fixB.getUserData() == "car"))) {
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
