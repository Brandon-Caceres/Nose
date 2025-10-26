package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

public class Block {
    int x,y,width,height;
    Color cc;
    private boolean destroyed; // ahora es privado
    
    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        destroyed = false;
        Random r = new Random(x+y);
        
        cc = new Color(0.1f + (r.nextFloat() * 0.9f), r.nextFloat(), r.nextFloat(), 1.0f);
  
    }
    public void draw(ShapeRenderer shape){
        if (destroyed) return; // no dibujar si ya está destruido
    	shape.setColor(cc);
        shape.rect(x, y, width, height);
    }

    // Encapsulamiento
    public boolean isDestroyed() {
        return destroyed;
    }
    public void destroy() {
        destroyed = true;
    }
}