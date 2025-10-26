package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GameObject(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
    public int getWidth(){ return width; }
    public int getHeight(){ return height; }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Por defecto, nada. Las subclases pueden sobrescribir.
    }

    public abstract void draw(ShapeRenderer shape);
}