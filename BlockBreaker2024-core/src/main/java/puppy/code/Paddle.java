package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Paddle extends GameObject implements Collidable {
    private float speedPixelsPerSecond = 200f;

    public Paddle(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
    }

    public void setSpeedPixelsPerSecond(float speed) { this.speedPixelsPerSecond = speed; }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        int newX = x;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX = (int)(x - speedPixelsPerSecond * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX = (int)(x + speedPixelsPerSecond * delta);
        }
        if (newX < 0) newX = 0;
        if (newX + width > Gdx.graphics.getWidth()) newX = Gdx.graphics.getWidth() - width;
        x = newX;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void onHitByBall(PingBall ball) {
        ball.setColor(Color.GREEN);
        int center = x + width / 2;
        int diff = ball.x - center;
        if (Math.abs(diff) > 0) {
            int sign = diff > 0 ? 1 : -1;
            ball.setXY(ball.x + sign * Math.min(4, Math.abs(diff) / 6), ball.y);
        }
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.glutils.ShapeRenderer shape) {
        shape.setColor(Color.BLUE);
        shape.rect(x, y, width, height);
    }
}