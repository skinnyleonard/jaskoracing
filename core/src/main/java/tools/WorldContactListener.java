package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {

    public static int lapCount = 1;
    public static int maxLap = 3;
    public static int[][] jugadorCount = new int[8][2];

    public WorldContactListener() {
        inicio();
        System.out.println(jugadorCount[0][0]);
        System.out.println(MapLoader.maxCheck);
    }

    public void inicio() {
        long time = System.currentTimeMillis();
        if (time > 100) {
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

        // Variables locales (no estáticas)
        String a = String.valueOf(fixA.getUserData());
        String b = String.valueOf(fixB.getUserData());

        String car = null;
        String check = null;

        // Detectar cuál es el auto y cuál el checkpoint
        if (a.contains("car") && b.contains("check")) {
            car = a;
            check = b;
        } else if (b.contains("car") && a.contains("check")) {
            car = b;
            check = a;
        } else {
            return; // No es contacto relevante
        }

        // Obtener índices correctos
        int carIndex = Integer.parseInt(car.replaceAll("[^0-9]", "")) - 1; // 🔹 FIX principal
        int checkIndex = Integer.parseInt(check.replaceAll("[^0-9]", ""));

        // Validar índice dentro del rango
        if (carIndex < 0 || carIndex >= jugadorCount.length) return;

        // Pasó el siguiente checkpoint
        if (jugadorCount[carIndex][0] + 1 == checkIndex) {
            jugadorCount[carIndex][0]++;
            System.out.println("Auto: " + carIndex + " Check: " + jugadorCount[carIndex][0] + " Vuelta: " + jugadorCount[carIndex][1]);
        }
        // Completó la vuelta
        else if (jugadorCount[carIndex][0] == MapLoader.maxCheck && checkIndex == 1) {
            jugadorCount[carIndex][0] = 1;

            if (jugadorCount[carIndex][1] == maxLap) {
                Timer.stopTimer();
                System.out.println("Auto " + carIndex + " llegó a la meta!");
            } else {
                jugadorCount[carIndex][1]++;
                System.out.println("Auto: " + carIndex + " reinicia vuelta -> Vuelta: " + jugadorCount[carIndex][1]);
            }
        }
    }

    public static void PosCheck(int totalPlayer) {
        int[] pos = new int[totalPlayer];
        boolean[] yaElegido = new boolean[totalPlayer];

        for (int j = 0; j < totalPlayer; j++) {
            int elegido = -1;
            int masLaps = -1;
            int maxCheck = -1;

            for (int i = 0; i < totalPlayer; i++) {
                if (yaElegido[i]) continue;

                int laps = jugadorCount[i][1];
                int checks = jugadorCount[i][0];

                if (laps > masLaps || (laps == masLaps && checks > maxCheck)) {
                    masLaps = laps;
                    maxCheck = checks;
                    elegido = i;
                }
            }

            pos[j] = elegido;
            yaElegido[elegido] = true;
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < totalPlayer; j++) {
            sb.append((j + 1))
                .append("° Auto: ").append(pos[j])
                .append(" | Vueltas: ").append(jugadorCount[pos[j]][1])
                .append(" | Checkpoints: ").append(jugadorCount[pos[j]][0])
                .append("\n");

            System.out.println("Posición: " + (j + 1) +
                " | Auto: " + pos[j] +
                " | Vueltas: " + jugadorCount[pos[j]][1] +
                " | Checkpoints: " + jugadorCount[pos[j]][0]);
        }

        HUD.leaderboard.setText(sb.toString());
    }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManiFold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
