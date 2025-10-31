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

    public void setSpeedPixelsPerSecond(float speed) {
        this.speedPixelsPerSecond = speed;
    }

    @Override
    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        int newX = x;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX = (int) (x - speedPixelsPerSecond * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX = (int) (x + speedPixelsPerSecond * delta);
        }

        // Limites de pantalla
        if (newX > 0 && newX + width < Gdx.graphics.getWidth()) {
            x = newX;
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.BLUE);
        shape.rect(x, y, width, height);
    }
    
    // Collidable interface implementation
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    @Override
    public void onHitByBall(PingBall ball) {
        // Change ball color when it hits the paddle
        ball.setColor(Color.GREEN);
    }
}