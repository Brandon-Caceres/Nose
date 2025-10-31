package puppy.code;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Block extends GameObject implements Collidable {
    private Color baseColor;
    private boolean destroyed;
    private boolean unbreakable;
    private int hp;

    public Block(int x, int y, int width, int height) {
        this(x, y, width, height, 1, false);
    }

    public Block(int x, int y, int width, int height, int hp, boolean unbreakable) {
        super(x, y, width, height);
        this.unbreakable = unbreakable;
        this.hp = Math.max(1, hp);
        this.destroyed = false;

        Random r = new Random(x + y);
        this.baseColor = new Color(0.15f + (r.nextFloat() * 0.8f), r.nextFloat(), r.nextFloat(), 1.0f);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void onHitByBall(PingBall ball) {
        hit();
    }

    public void hit() {
        if (destroyed) return;
        if (unbreakable) return;
        hp--;
        if (hp <= 0) destroyed = true;
    }

    public boolean isDestroyed() { return destroyed; }
    public boolean isUnbreakable() { return unbreakable; }
    public int getHp() { return hp; }

    public void destroy() {
        if (!unbreakable) {
            destroyed = true;
            hp = 0;
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        if (destroyed) return;
        if (unbreakable) {
            shape.setColor(Color.DARK_GRAY);
        } else if (hp >= 2) {
            float factor = Math.max(0.45f, 1.0f - 0.18f * (hp - 1));
            shape.setColor(baseColor.r * factor, baseColor.g * factor, baseColor.b * factor, 1f);
        } else {
            shape.setColor(baseColor);
        }
        shape.rect(x, y, width, height);
    }
}