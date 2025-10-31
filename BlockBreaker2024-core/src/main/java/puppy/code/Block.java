package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

public class Block extends GameObject implements Collidable {
    private Color cc;
    private boolean destroyed;
    private int hp;
    private boolean unbreakable;
    
    public Block(int x, int y, int width, int height) {
        super(x, y, width, height);
        destroyed = false;
        hp = 1;
        unbreakable = false;
        Random r = new Random(x+y);
        
        cc = new Color(0.1f + (r.nextFloat() * 0.9f), r.nextFloat(), r.nextFloat(), 1.0f);
    }
    
    public Block(int x, int y, int width, int height, int hp, boolean unbreakable) {
        super(x, y, width, height);
        this.destroyed = false;
        this.hp = hp;
        this.unbreakable = unbreakable;
        Random r = new Random(x+y);
        
        if (unbreakable) {
            // Unbreakable blocks are dark gray
            cc = new Color(0.3f, 0.3f, 0.3f, 1.0f);
        } else if (hp > 1) {
            // Tough blocks are darker/more saturated colors
            cc = new Color(0.6f + (r.nextFloat() * 0.4f), 0.2f + (r.nextFloat() * 0.3f), 0.2f + (r.nextFloat() * 0.3f), 1.0f);
        } else {
            // Regular blocks
            cc = new Color(0.1f + (r.nextFloat() * 0.9f), r.nextFloat(), r.nextFloat(), 1.0f);
        }
    }
    
    @Override
    public void draw(ShapeRenderer shape){
        if (destroyed) return;
        shape.setColor(cc);
        shape.rect(x, y, width, height);
    }

    // Encapsulation methods
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public void destroy() {
        destroyed = true;
    }
    
    public int getHp() {
        return hp;
    }
    
    public boolean isUnbreakable() {
        return unbreakable;
    }
    
    public void hit() {
        if (unbreakable) return;
        hp--;
        if (hp <= 0) {
            destroyed = true;
        }
    }
    
    // Collidable interface implementation
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    @Override
    public void onHitByBall(PingBall ball) {
        hit();
    }
}