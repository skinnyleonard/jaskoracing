package online;

import com.badlogic.gdx.math.Vector3;

public interface NetManager {
    void connect(boolean state, int id);
    void updatePlayer(Vector3 position);
    void createSpritePlayer(String data);
    void updateOtherPos(String json);
    void updateGrid(String grid);
    void checkFinished(int id);
    void deleteUser(String part);
}
