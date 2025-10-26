package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PingBall {
    private int x;
    private int y;
    private int size;
    private int xSpeed;
    private int ySpeed;
    private Color color = Color.WHITE;
    private boolean estaQuieto;

    public PingBall(int x, int y, int size, int xSpeed, int ySpeed, boolean iniciaQuieto) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.estaQuieto = iniciaQuieto;
    }

    // Setter para permitir que otras clases cambien el color (p. ej., Paddle)
    public void setColor(Color c) {
        this.color = c;
    }

    public boolean estaQuieto() { return estaQuieto; }
    public void setEstaQuieto(boolean b) { estaQuieto = b; }
    public void setXY(int x, int y) { this.x = x; this.y = y; }
    public int getY() { return y; }

    public void draw(ShapeRenderer shape){
        shape.setColor(color);
        shape.circle(x, y, size);
    }

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

    public void checkCollision(Paddle paddle) {
        if (collidesWith(paddle)) {
            color = Color.GREEN;    // feedback por defecto si no cambias desde Paddle
            ySpeed = -ySpeed;
        } else {
            color = Color.WHITE;
        }
    }

    private boolean collidesWith(Paddle pp) {
        boolean intersectaX = (pp.getX() + pp.getWidth() >= x - size) && (pp.getX() <= x + size);
        boolean intersectaY = (pp.getY() + pp.getHeight() >= y - size) && (pp.getY() <= y + size);
        return intersectaX && intersectaY;
    }

    public void checkCollision(Block block) {
        if (collidesWith(block)) {
            ySpeed = -ySpeed;
            // Si encapsulaste Block, usa block.destroy(); si no, deja como estaba
            try {
                block.getClass().getMethod("destroy"); // solo para sugerir en texto
                block.destroy();
            } catch (Exception e) {
                // fallback si no tienes destroy(): block.destroyed = true;
                // Pero lo ideal es tener destroy() e isDestroyed() en Block.
                block.destroy(); // asume que ya lo implementaste
            }
        }
    }

    private boolean collidesWith(Block bb) {
        boolean intersectaX = (bb.x + bb.width >= x - size) && (bb.x <= x + size);
        boolean intersectaY = (bb.y + bb.height >= y - size) && (bb.y <= y + size);
        return intersectaX && intersectaY;
    }
}