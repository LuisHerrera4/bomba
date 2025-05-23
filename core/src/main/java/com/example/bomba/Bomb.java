package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Bomb {
    private float x, y;
    private int radius;
    private float fuseTime;   // Tiempo hasta explosión (en segundos)
    private boolean exploded;
    private Texture bombTexture;
    private float explosionTime;   // Tiempo transcurrido desde la explosión
    public static final float EXPLOSION_DURATION = 0.5f; // Duración de la animación de explosión

    private Animation<TextureRegion> explosionAnimation;
    private Texture[] explosionTextures;  // Texturas de la animación de explosión
    public int getRadius() {
        return radius;
    }

    public Bomb(float x, float y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.fuseTime = 3.0f; // Explota a los 3 segundos
        this.exploded = false;
        bombTexture = new Texture("btn_bomb.png");
        explosionTime = 0f;

        // Cargar imágenes de la animación de explosión desde la carpeta "bomba"
        Texture exp1 = new Texture("bomba/explosion_derecha_izquierda.png");
        Texture exp2 = new Texture("bomba/explosion_derechaYizquierda_sinFinal.png");
        Texture exp3 = new Texture("bomba/explosion_final_abajo.png");
        Texture exp4 = new Texture("bomba/explosion_final_arriba.png");
        Texture exp5 = new Texture("bomba/explosion_medio.png");
        Texture exp6 = new Texture("bomba/explosion_SinFinal_arribaYabajo.png");

        explosionTextures = new Texture[] { exp1, exp2, exp3, exp4, exp5, exp6 };
        TextureRegion[] expFrames = new TextureRegion[explosionTextures.length];
        for (int i = 0; i < explosionTextures.length; i++) {
            expFrames[i] = new TextureRegion(explosionTextures[i]);
        }
        explosionAnimation = new Animation<TextureRegion>(0.1f, expFrames);
    }

    public void update(float delta) {
        if (!exploded) {
            fuseTime -= delta;
            // Se espera a que GameScreen invoque triggerExplosion cuando fuseTime ≤ 0.
        } else {
            explosionTime += delta;
        }
    }

    public void triggerExplosion(GameMap map, Array<PowerUp> powerUps) {
        if (!exploded) {
            exploded = true;
            System.out.println("Bomb exploded at (" + x + ", " + y + ") with radius " + radius);
            int bombCol = (int)(x / GameMap.TILE_SIZE);
            int bombRow = (int)(y / GameMap.TILE_SIZE);
            int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
            for (int[] dir : directions) {
                for (int i = 1; i <= radius; i++) {
                    int r = bombRow + i * dir[0];
                    int c = bombCol + i * dir[1];
                    if (r < 0 || r >= map.getRows() || c < 0 || c >= map.getCols())
                        break;
                    int cell = map.getCell(r, c);
                    if (cell == 1) { // Muro indestructible: detiene la propagación.
                        break;
                    }
                    if (cell == 2) {
                        map.setCell(r, c, 0);
                        if (MathUtils.random() < 0.3f) {
                            float cellX = c * GameMap.TILE_SIZE;
                            float cellY = r * GameMap.TILE_SIZE;
                            int randomIndex = MathUtils.random(PowerUp.PowerUpType.values().length - 1);
                            powerUps.add(new PowerUp(PowerUp.PowerUpType.values()[randomIndex], cellX, cellY));
                        }
                        break;
                    }
                    // Si la celda es piso, la explosión continúa.
                }
            }
        }
    }

    public float getFuseTime() { return fuseTime; }
    public boolean hasExploded() { return exploded; }
    public float getX() { return x; }
    public float getY() { return y; }

    public boolean isFinished() {
        return exploded && explosionTime > EXPLOSION_DURATION;
    }

    public void render(SpriteBatch batch) {
        if (!exploded) {
            batch.draw(bombTexture, x, y, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
        } else {
            TextureRegion expFrame = explosionAnimation.getKeyFrame(explosionTime, false);
            batch.draw(expFrame, x, y, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
        }
    }

    public void dispose() {
        bombTexture.dispose();
        for (Texture t : explosionTextures) {
            t.dispose();
        }
    }
}
