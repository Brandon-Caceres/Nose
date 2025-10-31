package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BlockBreakerGame extends ApplicationAdapter {

    private enum State { MENU, PLAYING, PAUSED }

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shape;
    private GlyphLayout layout;

    private PingBall ball;
    private Paddle pad;
    private ArrayList<Block> blocks = new ArrayList<>();

    private int vidas;
    private int puntaje;
    private int nivel;

    private State state = State.MENU;
    private Difficulty difficulty = Difficulty.EASY;

    // Difficulty params
    private int baseRows = 3;
    private int rowsPerLevelIncrement = 0;
    private int padBaseWidth = 120;
    private int currentBallXSpeed = 5;
    private int currentBallYSpeed = 7;
    private float padSpeed = 200f;

    // Block sizing defaults (MEDIUM/HARD)
    private int blockWidth = 70;
    private int blockHeight = 26;
    private int blockHSpacing = 10;
    private int blockVSpacing = 10;
    private int blockMarginLR = 10;
    private int blockTopMargin = 10;

    private int easyTargetCols = 8;

    // block type rates
    private float toughBlocksRate = 0f;
    private float unbreakableRate = 0f;
    private boolean allowUnbreakables = false;

    // pause/menu
    private final String[] pauseOptions = { "Reanudar", "Reiniciar nivel", "Menu principal", "Salir" };
    private int pauseSelected = 0;
    private boolean justEnteredPause = false;
    private long lastPauseToggleMs = 0;

    @Override
    public void create () {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f, 2.0f);
        shape = new ShapeRenderer();
        layout = new GlyphLayout();

        state = State.MENU;
        difficulty = Difficulty.EASY;
        puntaje = 0;
        vidas = 3;
        nivel = 1;
        blocks.clear();
    }

    private boolean canTogglePause() {
        long now = com.badlogic.gdx.utils.TimeUtils.millis();
        if (now - lastPauseToggleMs < 200) return false;
        lastPauseToggleMs = now;
        return true;
    }

    private void applyDifficulty(Difficulty d) {
        difficulty = d;
        switch (d) {
            case EASY:
                padBaseWidth = 160;
                currentBallXSpeed = 2;
                currentBallYSpeed = 3;
                baseRows = 3;
                rowsPerLevelIncrement = 0;
                padSpeed = 700f;

                easyTargetCols = 8;
                blockHSpacing = 16;
                blockVSpacing = 14;
                blockMarginLR = 20;
                blockTopMargin = 20;

                toughBlocksRate = 0.0f;
                unbreakableRate = 0.0f;
                allowUnbreakables = false;
                break;

            case MEDIUM:
                padBaseWidth = 110;
                currentBallXSpeed = 4;
                currentBallYSpeed = 5;
                baseRows = 5;
                rowsPerLevelIncrement = 1;
                padSpeed = 1000f;

                blockWidth = 70;
                blockHeight = 26;
                blockHSpacing = 10;
                blockVSpacing = 10;
                blockMarginLR = 10;
                blockTopMargin = 10;

                toughBlocksRate = 0.25f;
                unbreakableRate = 0.0f;
                allowUnbreakables = false;
                break;

            case HARD:
                padBaseWidth = 90;
                currentBallXSpeed = 5;
                currentBallYSpeed = 6;
                baseRows = 7;
                rowsPerLevelIncrement = 2;
                padSpeed = 1500f;

                blockWidth = 70;
                blockHeight = 26;
                blockHSpacing = 10;
                blockVSpacing = 10;
                blockMarginLR = 10;
                blockTopMargin = 10;

                toughBlocksRate = 0.40f;
                unbreakableRate = 0.15f;
                allowUnbreakables = true;
                break;
        }
    }

    private void startGame() {
        puntaje = 0;
        vidas = 3;
        nivel = 1;

        pad = new Paddle((int)(camera.viewportWidth/2f - padBaseWidth/2f), 40, padBaseWidth, 10);
        pad.setSpeedPixelsPerSecond(padSpeed);

        ball = new PingBall(
            (int)(camera.viewportWidth/2f - 10),
            pad.getY() + pad.getHeight() + 11,
            10,
            currentBallXSpeed,
            currentBallYSpeed,
            true
        );

        crearBloques(filasParaNivel(nivel));
    }

    private int filasParaNivel(int nivelActual) {
        return baseRows + Math.max(0, (nivelActual - 1) * rowsPerLevelIncrement);
    }

    public void crearBloques(int filas) {
        blocks.clear();
        int y = (int)camera.viewportHeight - blockTopMargin;

        if (difficulty == Difficulty.EASY) {
            int cols = Math.max(3, easyTargetCols);
            float worldW = camera.viewportWidth;

            float availableW = worldW - (2 * blockMarginLR) - (blockHSpacing * (cols - 1));
            int bw = Math.max(40, (int)(availableW / cols));
            int bh = Math.max(26, (int)(bw * 0.38f));

            for (int fila = 0; fila < filas; fila++) {
                y -= (bh + blockVSpacing);
                if (y < 0) break;

                float rowWidth = cols * bw + (cols - 1) * blockHSpacing;
                int startX = (int)Math.round((worldW - rowWidth) / 2f);

                for (int c = 0; c < cols; c++) {
                    int x = startX + c * (bw + blockHSpacing);
                    blocks.add(new Block(x, y, bw, bh));
                }
            }
        } else {
            int bw = blockWidth;
            int bh = blockHeight;
            float worldW = camera.viewportWidth;

            float availableW = worldW - (2 * blockMarginLR);
            int cols = Math.max(1, (int)Math.floor((availableW + blockHSpacing) / (bw + blockHSpacing)));
            float rowWidth = cols * bw + (cols - 1) * blockHSpacing;
            int startX = Math.max(blockMarginLR, (int)Math.round((worldW - rowWidth) / 2f));

            for (int fila = 0; fila < filas; fila++) {
                y -= (bh + blockVSpacing);
                if (y < 0) break;

                for (int c = 0; c < cols; c++) {
                    int x = startX + c * (bw + blockHSpacing);

                    boolean makeUnbreakable = allowUnbreakables && Math.random() < unbreakableRate;
                    boolean makeTough = !makeUnbreakable && Math.random() < toughBlocksRate;

                    if (makeUnbreakable) {
                        blocks.add(new Block(x, y, bw, bh, 1, true));
                    } else if (makeTough) {
                        int hp = (difficulty == Difficulty.HARD)
                                ? (Math.random() < 0.5 ? 3 : 2)
                                : 2;
                        blocks.add(new Block(x, y, bw, bh, hp, false));
                    } else {
                        blocks.add(new Block(x, y, bw, bh));
                    }
                }
            }
        }
    }

    private void drawMenu() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float worldW = camera.viewportWidth;
        float worldH = camera.viewportHeight;

        String title = "BLOCKBREAKER 2024";
        String subtitle = "Elige dificultad:";
        String opt1 = "1 (F1) - FACIL   | Paleta grande, bola lenta";
        String opt2 = "2 (F2) - MEDIA   | Mas bloques, duros";
        String opt3 = "3 (F3) - DIFICIL | Mas bloques, duros e irrompibles";
        String controls = "Controles: LEFT/RIGHT, SPACE lanzar, ESC pausa";

        float y = worldH - 60;
        float line = 48f;

        layout.setText(font, title);
        font.draw(batch, title, (worldW - layout.width) / 2f, y);
        y -= line * 1.2f;

        layout.setText(font, subtitle);
        font.draw(batch, subtitle, (worldW - layout.width) / 2f, y);
        y -= line;

        layout.setText(font, opt1);
        font.draw(batch, opt1, (worldW - layout.width) / 2f, y);
        y -= line;

        layout.setText(font, opt2);
        font.draw(batch, opt2, (worldW - layout.width) / 2f, y);
        y -= line;

        layout.setText(font, opt3);
        font.draw(batch, opt3, (worldW - layout.width) / 2f, y);
        y -= line * 1.5f;

        layout.setText(font, controls);
        font.draw(batch, controls, (worldW - layout.width) / 2f, 80);

        batch.end();
    }

    public void dibujaTextos() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "Puntos: " + puntaje, 10, 25);
        font.draw(batch, "Vidas : " + vidas, camera.viewportWidth - 240, 25);
        font.draw(batch, "Nivel : " + nivel, camera.viewportWidth/2f - 60, 25);
        font.draw(batch, "Dif   : " + difficulty, camera.viewportWidth/2f + 120, 25);
        batch.end();
    }

    private void handleMenuInput() {
        boolean easy  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.F1);
        boolean medium= Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.F2);
        boolean hard  = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.F3);

        if (easy) {
            applyDifficulty(Difficulty.EASY);
            startGame();
            state = State.PLAYING;
        } else if (medium) {
            applyDifficulty(Difficulty.MEDIUM);
            startGame();
            state = State.PLAYING;
        } else if (hard) {
            applyDifficulty(Difficulty.HARD);
            startGame();
            state = State.PLAYING;
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (state == State.MENU) {
            drawMenu();
            handleMenuInput();
            return;
        }

        // Toggle pause from PLAYING
        if (state == State.PLAYING && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && canTogglePause()) {
            state = State.PAUSED;
            pauseSelected = 0;
            justEnteredPause = true;
        }

        if (state == State.PLAYING) {
            renderGameFrame(true);
            return;
        }

        if (state == State.PAUSED) {
            renderGameFrame(false);
            drawPauseOverlayAndMenu();
            handlePauseInput();
            return;
        }
    }

    private void renderGameFrame(boolean updating) {
        camera.update();
        shape.setProjectionMatrix(camera.combined);

        if (updating) pad.update();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        pad.draw(shape);

        if (ball.estaQuieto()) {
            ball.setXY(pad.getX() + pad.getWidth()/2 - 5, pad.getY() + pad.getHeight() + 11);
            if (updating && Gdx.input.isKeyPressed(Input.Keys.SPACE)) ball.setEstaQuieto(false);
        } else if (updating) {
            ball.update();
        }

        if (updating && ball.getY() < 0) {
            vidas--;
            ball = new PingBall(
                pad.getX() + pad.getWidth()/2 - 5,
                pad.getY() + pad.getHeight() + 11,
                10,
                currentBallXSpeed,
                currentBallYSpeed,
                true
            );
        }

        if (updating && vidas <= 0) {
            state = State.MENU;
            blocks.clear();
            shape.end();
            return;
        }

        for (Block b : blocks) {
            b.draw(shape);
            if (updating) ball.checkCollision((Collidable)b);
        }

        if (updating) {
            for (int i = 0; i < blocks.size(); i++) {
                Block b = blocks.get(i);
                if (b.isDestroyed()) {
                    puntaje++;
                    blocks.remove(i);
                    i--;
                }
            }
        }

        if (updating) ball.checkCollision((Collidable)pad);
        ball.draw(shape);

        shape.end();
        dibujaTextos();

        if (updating && blocks.size() == 0) {
            nivel++;
            if (difficulty == Difficulty.MEDIUM) {
                currentBallXSpeed += (currentBallXSpeed > 0 ? 1 : -1);
                currentBallYSpeed += (currentBallYSpeed > 0 ? 1 : -1);
                int newWidth = Math.max(70, pad.getWidth() - 8);
                pad = new Paddle(pad.getX(), pad.getY(), newWidth, pad.getHeight());
                pad.setSpeedPixelsPerSecond(padSpeed);
            } else if (difficulty == Difficulty.HARD) {
                currentBallXSpeed += (currentBallXSpeed > 0 ? 1 : -1);
                currentBallYSpeed += (currentBallYSpeed > 0 ? 1 : -1);
                int newWidth = Math.max(60, pad.getWidth() - 12);
                pad = new Paddle(pad.getX(), pad.getY(), newWidth, pad.getHeight());
                pad.setSpeedPixelsPerSecond(padSpeed);
            }

            crearBloques(filasParaNivel(nivel));
            ball = new PingBall(
                pad.getX() + pad.getWidth()/2 - 5,
                pad.getY() + pad.getHeight() + 11,
                10,
                currentBallXSpeed,
                currentBallYSpeed,
                true
            );
        }
    }

    private void drawPauseOverlayAndMenu() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0, 0, 0, 0.55f);
        shape.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float w = camera.viewportWidth;
        float h = camera.viewportHeight;

        String title = "PAUSA";
        layout.setText(font, title);
        font.draw(batch, title, (w - layout.width) / 2f, h - 120);

        float y = h - 200;
        float line = 44f;
        for (int i = 0; i < pauseOptions.length; i++) {
            String prefix = (i == pauseSelected) ? "> " : "  ";
            String text = prefix + pauseOptions[i];
            layout.setText(font, text);
            font.draw(batch, text, (w - layout.width) / 2f, y);
            y -= line;
        }

        String hint = "ESC: Reanudar  |  ENTER: Aceptar  |  UP/DOWN: Navegar";
        layout.setText(font, hint);
        font.draw(batch, hint, (w - layout.width) / 2f, 120);

        batch.end();
    }

    private void handlePauseInput() {
        if (justEnteredPause) {
            // consume the ESC that triggered pause
            justEnteredPause = false;
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && canTogglePause()) {
            state = State.PLAYING;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            pauseSelected = (pauseSelected - 1 + pauseOptions.length) % pauseOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            pauseSelected = (pauseSelected + 1) % pauseOptions.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switch (pauseSelected) {
                case 0:
                    state = State.PLAYING;
                    break;
                case 1:
                    crearBloques(filasParaNivel(nivel));
                    ball = new PingBall(
                        pad.getX() + pad.getWidth()/2 - 5,
                        pad.getY() + pad.getHeight() + 11,
                        10,
                        currentBallXSpeed,
                        currentBallYSpeed,
                        true
                    );
                    state = State.PLAYING;
                    break;
                case 2:
                    state = State.MENU;
                    blocks.clear();
                    break;
                case 3:
                    Gdx.app.exit();
                    break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void dispose () {
        // batch.dispose();
        // shape.dispose();
        // font.dispose();
    }
}