package puppy.code;

import com.badlogic.gdx.math.Rectangle;

interface Collidable {
    Rectangle getBounds();
    void onHitByBall(PingBall ball);
}