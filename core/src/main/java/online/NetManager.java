package online;

import com.badlogic.gdx.math.Vector3;

public interface NetManager {
    void connect(boolean state);
    void timeOutEnded();
    void updateSprites(Vector3 position);
}
