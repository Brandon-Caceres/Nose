package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Paddle {
    private int x = 20;
    private int y = 20;
    private int width = 100;
    private int height = 10;

    // Velocidad en píxeles por segundo (ajústala para reducir la velocidad)
    private float speedPixelsPerSecond = 200f;

    public Paddle(int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        width = ancho;
        height = alto;
    }

    // Setter para cambiar la velocidad desde el juego/dificultad
    public void setSpeedPixelsPerSecond(float speed) {
        this.speedPixelsPerSecond = speed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Mueve la paleta usando delta time (suave y configurable)
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

    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.BLUE);
        shape.rect(x, y, width, height);
    }
}