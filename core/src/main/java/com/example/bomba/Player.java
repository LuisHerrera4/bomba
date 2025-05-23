// Player.java
package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private float x, y;
    private float speed;
    private int bombRadius;
    private boolean shield;
    private boolean alive = true;

    private float bombCooldown;
    private float bombCooldownTime;

    private Animation<TextureRegion> animUp, animDown, animRight, animLeft;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean moving;
    private Direction currentDirection;

    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private static final int COLLISION_MARGIN = 6;

    private Texture shieldOverlay;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 100f;
        this.bombRadius = 1;
        this.shield = false;
        this.bombCooldown = 0f;
        this.bombCooldownTime = 2.0f;
        this.shieldOverlay = new Texture("power_shield.png");

        loadAnimations();
        currentDirection = Direction.DOWN;
        currentAnimation = animDown;
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

        animUp = new Animation<>(0.15f, new TextureRegion(up1), new TextureRegion(up2), new TextureRegion(up3));
        animDown = new Animation<>(0.15f, new TextureRegion(down1), new TextureRegion(down2), new TextureRegion(down3));
        animRight = new Animation<>(0.15f, new TextureRegion(right1), new TextureRegion(right2), new TextureRegion(right3));
        animLeft = new Animation<>(0.15f, new TextureRegion(left1), new TextureRegion(left2), new TextureRegion(left3));
    }

    public void move(Direction direction, float delta, GameMap gameMap) {
        float newX = x;
        float newY = y;
        float d = speed * delta;
        currentDirection = direction;

        switch (direction) {
            case UP: newY += d; break;
            case DOWN: newY -= d; break;
            case LEFT: newX -= d; break;
            case RIGHT: newX += d; break;
        }

        if (canMoveTo(newX, newY, gameMap)) {
            x = newX;
            y = newY;
            moving = true;
        } else {
            moving = false;
        }

        switch (direction) {
            case UP: currentAnimation = animUp; break;
            case DOWN: currentAnimation = animDown; break;
            case LEFT: currentAnimation = animLeft; break;
            case RIGHT: currentAnimation = animRight; break;
        }
    }

    private boolean canMoveTo(float newX, float newY, GameMap gameMap) {
        float effectiveX = newX + COLLISION_MARGIN / 2f;
        float effectiveY = newY + COLLISION_MARGIN / 2f;
        int effectiveWidth = WIDTH - COLLISION_MARGIN;
        int effectiveHeight = HEIGHT - COLLISION_MARGIN;

        int startCol = (int)(effectiveX / GameMap.TILE_SIZE);
        int endCol = (int)((effectiveX + effectiveWidth - 1) / GameMap.TILE_SIZE);
        int startRow = (int)(effectiveY / GameMap.TILE_SIZE);
        int endRow = (int)((effectiveY + effectiveHeight - 1) / GameMap.TILE_SIZE);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (!gameMap.isCellPassable(row, col)) return false;
            }
        }

        return true;
    }

    public void update(float delta) {
        if (moving) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }
        moving = false;

        if (bombCooldown > 0f) {
            bombCooldown -= delta;
            if (bombCooldown < 0f) bombCooldown = 0f;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y);
        if (shield) {
            batch.draw(shieldOverlay, x, y, WIDTH, HEIGHT);
        }
    }

    public void increaseSpeed() { speed += 20; }
    public void increaseBombRadius() { bombRadius++; }
    public void activateShield() { shield = true; }
    public void deactivateShield() { shield = false; }
    public boolean hasShield() { return shield; }

    public void kill() {
        if (shield) {
            shield = false; // Solo protege una vez
            System.out.println("El escudo absorbió la explosión, pero se ha destruido.");
        } else {
            alive = false;
            System.out.println("El jugador ha sido eliminado.");
        }
    }


    public boolean isAlive() { return alive; }

    public Bomb placeBomb() {
        if (bombCooldown <= 0f) {
            int bombCol = Math.round(x / GameMap.TILE_SIZE);
            int bombRow = Math.round(y / GameMap.TILE_SIZE);
            float bombX = bombCol * GameMap.TILE_SIZE;
            float bombY = bombRow * GameMap.TILE_SIZE;
            bombCooldown = bombCooldownTime;
            return new Bomb(bombX, bombY, bombRadius, Bomb.OwnerType.PLAYER, 0);
        }
        return null;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void dispose() {
        animUp.getKeyFrame(0).getTexture().dispose();
        animDown.getKeyFrame(0).getTexture().dispose();
        animRight.getKeyFrame(0).getTexture().dispose();
        animLeft.getKeyFrame(0).getTexture().dispose();
        shieldOverlay.dispose();
    }
}
