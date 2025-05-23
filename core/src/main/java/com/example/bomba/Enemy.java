// Enemy.java
package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private static int nextId = 1;
    private int id;

    private float x, y;
    private float speed;
    private int currentDirection;
    private Animation<TextureRegion> animUp, animDown, animRight, animLeft;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private float moveTimer;
    private float changeDirectionInterval;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    private float bombCooldown;
    private float bombCooldownTime;

    private boolean alive = true;
    private Bomb enemyBomb = null;

    public Enemy(float x, float y) {
        this.id = nextId++;
        this.x = x;
        this.y = y;
        this.speed = 80f;
        loadAnimations();
        stateTime = 0f;
        chooseNewDirection();
        bombCooldownTime = 3.0f;
        bombCooldown = 0f;
    }

    private void loadAnimations() {
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

        animUp = new Animation<>(0.15f, framesUp);
        animDown = new Animation<>(0.15f, framesDown);
        animRight = new Animation<>(0.15f, framesRight);
        animLeft = new Animation<>(0.15f, framesLeft);
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

    public void update(float delta, GameMap map, Player player, Array<Enemy> allEnemies) {
        moveTimer += delta;
        if (moveTimer > changeDirectionInterval) chooseNewDirection();

        float d = speed * delta;
        float newX = x, newY = y;

        switch (currentDirection) {
            case 0: newY += d; break;
            case 1: newY -= d; break;
            case 2: newX -= d; break;
            case 3: newX += d; break;
        }

        int startCol = (int) (newX / GameMap.TILE_SIZE);
        int endCol = (int) ((newX + WIDTH - 1) / GameMap.TILE_SIZE);
        int startRow = (int) (newY / GameMap.TILE_SIZE);
        int endRow = (int) ((newY + HEIGHT - 1) / GameMap.TILE_SIZE);

        boolean canMove = true;
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int cellValue = map.getCell(row, col);
                if (cellValue != 0) {
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
            chooseNewDirection();
        }

        stateTime += delta;

        // Buscar objetivo más cercano (jugador o enemigo diferente)
        float targetX = player.getX();
        float targetY = player.getY();

        for (Enemy other : allEnemies) {
            if (other != this && other.isAlive()) {
                float dEn = Vector2.dst(x, y, other.getX(), other.getY());
                if (dEn < 5 * GameMap.TILE_SIZE) {
                    targetX = other.getX();
                    targetY = other.getY();
                    break;
                }
            }
        }

        bombCooldown -= delta;
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < 5 * GameMap.TILE_SIZE && bombCooldown <= 0f) {
            enemyBomb = placeBomb();
            bombCooldown = bombCooldownTime;
        }
    }

    public Bomb placeBomb() {
        int bombCol = Math.round(x / GameMap.TILE_SIZE);
        int bombRow = Math.round(y / GameMap.TILE_SIZE);
        float bombX = bombCol * GameMap.TILE_SIZE;
        float bombY = bombRow * GameMap.TILE_SIZE;
        return new Bomb(bombX, bombY, 1, Bomb.OwnerType.ENEMY, id);
    }


    public Bomb getBomb() {
        if (enemyBomb != null) {
            Bomb temp = enemyBomb;
            enemyBomb = null;
            return temp;
        }
        return null;
    }


    public void render(SpriteBatch batch) {
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }
    public int getId() { return id; }

    public void dispose() {
        animUp.getKeyFrame(0).getTexture().dispose();
        animDown.getKeyFrame(0).getTexture().dispose();
        animRight.getKeyFrame(0).getTexture().dispose();
        animLeft.getKeyFrame(0).getTexture().dispose();
    }
}
