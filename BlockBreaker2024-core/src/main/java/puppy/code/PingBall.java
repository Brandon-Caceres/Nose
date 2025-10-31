package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class PingBall extends GameObject {
    private int size;
    private int xSpeed;
    private int ySpeed;
    private Color color = Color.WHITE;
    private boolean estaQuieto;

    public PingBall(int x, int y, int size, int xSpeed, int ySpeed, boolean iniciaQuieto) {
        super(x, y, size * 2, size * 2);
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.estaQuieto = iniciaQuieto;
    }

    public boolean estaQuieto() { return estaQuieto; }
    public void setEstaQuieto(boolean b) { estaQuieto = b; }
    public void setXY(int nx, int ny) { this.x = nx; this.y = ny; }
    public int getY() { return y; }

    public void setColor(Color c) { this.color = c; }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(x, y, size);
    }

    @Override
    public void update() {
        if (estaQuieto) return;
        x += xSpeed;
        y += ySpeed;
        if (x - size < 0) {
            x = size;
            xSpeed = -xSpeed;
        } else if (x + size > Gdx.graphics.getWidth()) {
            x = Gdx.graphics.getWidth() - size;
            xSpeed = -xSpeed;
        }
        if (y + size > Gdx.graphics.getHeight()) {
            y = Gdx.graphics.getHeight() - size;
            ySpeed = -ySpeed;
        }
    }

    private boolean collidesWith(Rectangle r) {
        float closestX = clamp(x, r.x, r.x + r.width);
        float closestY = clamp(y, r.y, r.y + r.height);
        float dx = x - closestX;
        float dy = y - closestY;
        return (dx * dx + dy * dy) <= (size * size);
    }

    private float clamp(float v, float a, float b) {
        if (v < a) return a;
        if (v > b) return b;
        return v;
    }

    public void checkCollision(Collidable c) {
        Rectangle r = c.getBounds();
        if (collidesWith(r)) {
            ySpeed = -ySpeed;
            c.onHitByBall(this);
        }
    }

    public void checkCollision(Paddle p) { checkCollision((Collidable)p); }
    public void checkCollision(Block b) { checkCollision((Collidable)b); }
}