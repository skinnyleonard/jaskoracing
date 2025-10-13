package tools;

public class contador extends Thread{

    public static int seg = 0;
    public static int min = 0;
    public static int mil = 0;
    @Override
    public void run() {
        try {
        boolean run = true;
        boolean supera = false;
        for (int min = 0; run ; min++) {

            for (seg = 0; seg < 60; seg++) {

                for (mil = 0 ; mil < 1000 ; mil++){
                    supera = false;
                    Thread.sleep(1);
                    if (mil > 999) {
                        mil = 999;
                        supera = true;
                    }
                    HUD.timeLabel.setText( "Tiempo: "+ String.format("%02d", min) + ":" + String.format("%02d", seg) + ":" + String.format("%03d", mil));

                    if (supera)
                    {
                        mil = 1000;
                    }
                }
            }
        }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
