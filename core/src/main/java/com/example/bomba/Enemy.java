package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Enemy {
    private float x, y;
    private float speed;
    private int currentDirection; // 0: UP, 1: DOWN, 2: LEFT, 3: RIGHT
    private Animation<TextureRegion> animUp, animDown, animRight, animLeft;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float moveTimer;
    private float changeDirectionInterval;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    // Bomb throwing fields
    private float bombCooldown;
    private float bombCooldownTime;

    private boolean alive = true;

    // Campo para almacenar la bomba lanzada en esta actualización (si la hay)
    private Bomb enemyBomb = null;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 80f;
        loadAnimations();
        stateTime = 0f;
        chooseNewDirection();
        bombCooldownTime = 3.0f; // El enemigo puede lanzar una bomba cada 3 segundos
        bombCooldown = 0f;
    }

    private void loadAnimations() {
        // Usamos las mismas imágenes que el jugador.
        Texture up1 = new Texture("Movimiento/Atras/player_U1.png");
        Texture up2 = new Texture("Movimiento/Atras/player_U2.png");
        Texture up3 = new Texture("Movimiento/Atras/player_U3.png");
        Texture down1 = new Texture("Movimiento/DeCara/player_D1.png");
        Texture down2 = new Texture("Movimiento/DeCara/player_D2.png");
        Texture down3 = new Texture("Movimiento/DeCara/player_D3.png");
        Texture right1 = new Texture("Movimiento/Derecha/player_R1.png");
        Texture right2 = new Texture("Movimiento/Derecha/player_R2.png");
        Texture right3 = new Texture("Movimiento/Derecha/player_R3.png");
        Texture left1 = new Texture("Movimiento/Izquierda/player_L1.png");
        Texture left2 = new Texture("Movimiento/Izquierda/player_L2.png");
        Texture left3 = new Texture("Movimiento/Izquierda/player_L3.png");

        TextureRegion[] framesUp = new TextureRegion[] {
            new TextureRegion(up1), new TextureRegion(up2), new TextureRegion(up3)
        };
        TextureRegion[] framesDown = new TextureRegion[] {
            new TextureRegion(down1), new TextureRegion(down2), new TextureRegion(down3)
        };
        TextureRegion[] framesRight = new TextureRegion[] {
            new TextureRegion(right1), new TextureRegion(right2), new TextureRegion(right3)
        };
        TextureRegion[] framesLeft = new TextureRegion[] {
            new TextureRegion(left1), new TextureRegion(left2), new TextureRegion(left3)
        };

        animUp = new Animation<TextureRegion>(0.15f, framesUp);
        animDown = new Animation<TextureRegion>(0.15f, framesDown);
        animRight = new Animation<TextureRegion>(0.15f, framesRight);
        animLeft = new Animation<TextureRegion>(0.15f, framesLeft);
    }

    private void chooseNewDirection() {
        currentDirection = MathUtils.random(3);
        changeDirectionInterval = MathUtils.random(1.0f, 3.0f);
        moveTimer = 0f;
        switch(currentDirection) {
            case 0: currentAnimation = animUp; break;
            case 1: currentAnimation = animDown; break;
            case 2: currentAnimation = animLeft; break;
            case 3: currentAnimation = animRight; break;
        }
    }

    /**
     * Se actualiza el enemigo, se mueve según su dirección y, si el jugador está cercano (<5 tiles)
     * y se ha cumplido el cooldown, lanza una bomba.
     * @param delta Tiempo delta.
     * @param map Mapa para colisiones.
     * @param player Referencia al jugador.
     */
    public void update(float delta, GameMap map, Player player) {
        moveTimer += delta;
        if (moveTimer > changeDirectionInterval) {
            chooseNewDirection();
        }

        float d = speed * delta;
        float newX = x, newY = y;

        switch (currentDirection) {
            case 0: newY += d; break;
            case 1: newY -= d; break;
            case 2: newX -= d; break;
            case 3: newX += d; break;
        }

        // Verificación de colisión MEJORADA: el enemigo solo puede moverse si NO hay muros indestructibles.
        int startCol = (int) (newX / GameMap.TILE_SIZE);
        int endCol = (int) ((newX + WIDTH - 1) / GameMap.TILE_SIZE);
        int startRow = (int) (newY / GameMap.TILE_SIZE);
        int endRow = (int) ((newY + HEIGHT - 1) / GameMap.TILE_SIZE);

        boolean canMove = true;
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int cellValue = map.getCell(row, col);
                if (cellValue == 1) {  // Si hay una pared indestructible, bloquea el paso.
                    canMove = false;
                    break;
                }
            }
            if (!canMove) break;
        }

        if (canMove) {
            x = newX;
            y = newY;
        } else {
            chooseNewDirection(); // Si choca con un muro indestructible, elige nueva dirección.
        }

        stateTime += delta;

        // Lógica de bomb throwing: si el jugador está cerca y se cumple el cooldown, lanza bomba.
        bombCooldown -= delta;
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < 5 * GameMap.TILE_SIZE && bombCooldown <= 0f) {
            enemyBomb = placeBomb();
            bombCooldown = bombCooldownTime;
        }
    }





    /**
     * Permite al enemigo colocar una bomba en su celda actual.
     */
    public Bomb placeBomb() {
        return new Bomb(x, y, 1);  // Radio 1, se puede ajustar
    }

    /**
     * Devuelve la bomba lanzada (si se lanzó en esta actualización) y la reinicia.
     */
    public Bomb getBomb() {
        Bomb temp = enemyBomb;
        enemyBomb = null;
        return temp;
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public void dispose() {
        animUp.getKeyFrame(0).getTexture().dispose();
        animDown.getKeyFrame(0).getTexture().dispose();
        animRight.getKeyFrame(0).getTexture().dispose();
        animLeft.getKeyFrame(0).getTexture().dispose();
    }
}
