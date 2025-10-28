package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener{

    public static int lapCount = 1;
    String col = "check";
    String col2 = "car";
    public static int maxLap = 3;
    public static int[][] jugadorCount = new int [8][2];
    public static String car;
    public WorldContactListener() {
        inicio();

        System.out.println(jugadorCount[0][0]);
        System.out.println(MapLoader.maxCheck);
    }
    public void inicio()
    {
        long time = System.currentTimeMillis();
        if(time > 100){
            for (int i = 0; i < jugadorCount.length; i++) {
                jugadorCount[i][0] = 1; // checkpoint inicial
                jugadorCount[i][1] = 1; // vuelta inicial
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        String check = fixA.getUserData().toString();
        car = String.valueOf(fixB.getUserData());

        if (check.contains(col) && car.contains(col2)) {
            if ( (fixA.getUserData() == (car) && fixB.getUserData() == (check) ||
                (fixA.getUserData() == (check) && fixB.getUserData() == (car)))) {

                if ((jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1][0]+1) == Integer.parseInt(check.replaceAll("[^0-9]", ""))){
                    jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1][0] ++;
                    System.out.println("Auto: " + Integer.parseInt ( car.replaceAll("[^0-9]", "") + " Check: " + jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", ""))][0]+ " Vuelta: " + jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", ""))][1]));
                   // HUD.checkLabel.setText(jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", ""))][0] + " / " + MapLoader.maxCheck );

                }
                else if ((jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1][0]) == MapLoader.maxCheck && Integer.parseInt(check.replaceAll("[^0-9]", "")) == 1) {

                    jugadorCount[Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1][0] = 1;

                    if (jugadorCount[Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1][1] == maxLap) {
                        Timer.stopTimer();
                        System.out.println("llegaste a la meta");
                    } else{

                    jugadorCount[Integer.parseInt(car.replaceAll("[^0-9]", ""))- 1][1] ++;
                   // HUD.checkLabel.setText(jugadorCount[Integer.parseInt(car.replaceAll("[^0-9]", ""))][0] + " / " + MapLoader.maxCheck);
                    //HUD.lapLabel.setText(jugadorCount[Integer.parseInt(car.replaceAll("[^0-9]", ""))][1] + " / " + maxLap);
                        System.out.println("Auto: " + Integer.parseInt ( car.replaceAll("[^0-9]", "")) + " Check: " + jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", ""))][0]+ " Vuelta: " + jugadorCount [Integer.parseInt(car.replaceAll("[^0-9]", ""))][1]);
                }
                }
            }
        }
        else if ( (fixA.getUserData() == "car1" && fixB.getUserData() == ("pared") ||
            (fixA.getUserData() == ("pared") && fixB.getUserData() == "car1"))) {
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

