package online;

public interface NetManager {
    void connect(boolean state);
    void moveCar(String move, int client);
    public static String move = "";
    void placeNewPlayer(int connectedUsers, String carBrand);
    void deleteRacer(int index);
}
