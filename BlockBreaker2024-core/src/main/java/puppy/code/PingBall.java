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
        super(x, y, size * 2, size * 2);  // width and height are diameter
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.estaQuieto = iniciaQuieto;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public boolean estaQuieto() { return estaQuieto; }
    public void setEstaQuieto(boolean b) { estaQuieto = b; }
    public void setXY(int x, int y) { 
        this.x = x; 
        this.y = y; 
    }

    @Override
    public void draw(ShapeRenderer shape){
        shape.setColor(color);
        shape.circle(x, y, size);
    }

    @Override
    public void update() {
        if (estaQuieto) return;
        x += xSpeed;
        y += ySpeed;
        if (x - size < 0 || x + size > Gdx.graphics.getWidth()) {
            xSpeed = -xSpeed;
        }
        if (y + size > Gdx.graphics.getHeight()) {
            ySpeed = -ySpeed;
        }
    }

    public void checkCollision(Collidable collidable) {
        Rectangle bounds = collidable.getBounds();
        if (collidesWith(bounds)) {
            ySpeed = -ySpeed;
            collidable.onHitByBall(this);
        } else if (collidable instanceof Paddle) {
            // Reset color when not colliding with paddle
            color = Color.WHITE;
        }
    }

    private boolean collidesWith(Rectangle bounds) {
        boolean intersectaX = (bounds.x + bounds.width >= x - size) && (bounds.x <= x + size);
        boolean intersectaY = (bounds.y + bounds.height >= y - size) && (bounds.y <= y + size);
        return intersectaX && intersectaY;
    }
}