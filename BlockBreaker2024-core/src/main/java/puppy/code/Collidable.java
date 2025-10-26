package puppy.code;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    Rectangle getBounds();
    void onHitByBall(PingBall ball);
}