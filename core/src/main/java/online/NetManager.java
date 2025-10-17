package online;

import com.badlogic.gdx.math.Vector3;

public interface NetManager {
    void connect(boolean state);
    void updateSprites(String position);
    void createSpritePlayer(String lascosas);
}
