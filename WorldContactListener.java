package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import tools.InteractiveTileObject;
import tools.HUD;

public class WorldContactListener implements ContactListener{

    public static int checkCount = 1;
    public static int lapCount = 1;
    String col = "check";
    public static int maxLap = 3;

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
                HUD.checkLabel.setText(checkCount + " / " + MapLoader.maxCheck );
                HUD.lapLabel.setText(lapCount + " / " + maxLap);
            }
            else if (checkCount == MapLoader.maxCheck && Integer.parseInt(check.replaceAll("[^0-9]", "")) == 1){

                checkCount = 1;
                if (lapCount == maxLap){

                }
                lapCount = lapCount + 1;
                HUD.checkLabel.setText(checkCount + " / " + MapLoader.maxCheck );
                HUD.lapLabel.setText(lapCount + " / " + maxLap);
            }
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
