package online;

public interface NetManager {
    void connect(boolean state);
    void moveCar(String move, int client);
    public static String move = "";
    void placeNewPlayer(int connectedUsers);
    String getNewPlayerPos(int connectedUsers);
    String updateMetrics(int indexUser);
}
