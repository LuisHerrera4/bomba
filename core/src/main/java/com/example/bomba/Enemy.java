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
        float d = speed * delta;

        // Intentar avanzar en la dirección actual
        float newX = x, newY = y;
        switch (currentDirection) {
            case 0: newY += d; break;
            case 1: newY -= d; break;
            case 2: newX -= d; break;
            case 3: newX += d; break;
        }

        if (canMoveTo(newX, newY, map)) {
            x = newX;
            y = newY;
        } else {
            // Si no puede moverse, coloca una bomba para abrir camino
            if (bombCooldown <= 0f) {
                enemyBomb = placeBomb();
                bombCooldown = bombCooldownTime;
            }
            // Esperar a que la explosión ocurra antes de cambiar de dirección
            if (map.getCell((int) (y / GameMap.TILE_SIZE), (int) (x / GameMap.TILE_SIZE)) == 0) {
                chooseNewDirection();
            }
        }

        bombCooldown -= delta;
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

    private boolean canMoveTo(float newX, float newY, GameMap map) {
        int startCol = (int) (newX / GameMap.TILE_SIZE);
        int endCol = (int) ((newX + WIDTH - 1) / GameMap.TILE_SIZE);
        int startRow = (int) (newY / GameMap.TILE_SIZE);
        int endRow = (int) ((newY + HEIGHT - 1) / GameMap.TILE_SIZE);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int cell = map.getCell(row, col);
                if (cell != 0) return false;
            }
        }
        return true;
    }

    private boolean checkCollisionWith(PowerUp p) {
        return x < p.getX() + 32 && x + 32 > p.getX() &&
            y < p.getY() + 32 && y + 32 > p.getY();
    }

    private void activatePowerUp(PowerUp p) {
        switch (p.getType()) {
            case SPEED: speed += 20f; break;
            case BOMB_RADIUS:  break;
            /* puedes guardar un radius personalizado /
/ si quieres agregar protección al enemigo */
            case SHIELD: break;
        }
    }

    private boolean isSafeToPlaceBomb(GameMap map) {
        int col = (int) (x / GameMap.TILE_SIZE);
        int row = (int) (y / GameMap.TILE_SIZE);

        int escapeRoutes = 0;

        if (map.isCellPassable(row + 1, col)) escapeRoutes++;
        if (map.isCellPassable(row - 1, col)) escapeRoutes++;
        if (map.isCellPassable(row, col + 1)) escapeRoutes++;
        if (map.isCellPassable(row, col - 1)) escapeRoutes++;

        return escapeRoutes >= 2;  // Necesita al menos dos direcciones para no quedar atrapado
    }

    private boolean isSafeToMove(GameMap map) {
        int col = (int)(x / GameMap.TILE_SIZE);
        int row = (int)(y / GameMap.TILE_SIZE);

        return map.isCellPassable(row + 1, col) ||
            map.isCellPassable(row - 1, col) ||
            map.isCellPassable(row, col + 1) ||
            map.isCellPassable(row, col - 1);
    }

    private void escapeFromBomb(GameMap map) {
        if (map.isCellPassable((int)(y / GameMap.TILE_SIZE) + 1, (int)(x / GameMap.TILE_SIZE))) {
            currentDirection = 0; // Moverse arriba
        } else if (map.isCellPassable((int)(y / GameMap.TILE_SIZE) - 1, (int)(x / GameMap.TILE_SIZE))) {
            currentDirection = 1; // Moverse abajo
        } else if (map.isCellPassable((int)(y / GameMap.TILE_SIZE), (int)(x / GameMap.TILE_SIZE) - 1)) {
            currentDirection = 2; // Moverse izquierda
        } else if (map.isCellPassable((int)(y / GameMap.TILE_SIZE), (int)(x / GameMap.TILE_SIZE) + 1)) {
            currentDirection = 3; // Moverse derecha
        }
    }


}
