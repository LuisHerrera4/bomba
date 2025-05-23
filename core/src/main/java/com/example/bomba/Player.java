package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private float x, y;
    private float speed; // píxeles por segundo
    private int bombRadius;
    private boolean shield;

    // Cooldown de bomba
    private float bombCooldown;
    private float bombCooldownTime;

    private boolean alive = true;

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
        System.out.println("El jugador ha sido eliminado.");
    }

    // Animaciones direccionales
    private Animation<TextureRegion> animUp, animDown, animRight, animLeft;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean moving;  // se activa cuando el jugador se mueve
    private Direction currentDirection;

    // Tamaño del sprite (asumido 32×32)
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    // Margen de colisión (se reduce el “hit-box” efectivo)
    private static final int COLLISION_MARGIN = 6; // Puedes ajustar este valor

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 100f;
        this.bombRadius = 1;
        this.shield = false;
        loadAnimations();
        stateTime = 0f;
        moving = false;
        bombCooldown = 0f;
        bombCooldownTime = 2.0f; // 2 segundos de cooldown
        currentDirection = Direction.DOWN; // Por defecto, de cara
        currentAnimation = animDown;
    }

    private void loadAnimations() {
        // Se asume que las imágenes están organizadas en carpetas de movimiento
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

    /**
     * Intenta mover al jugador en la dirección especificada y actualiza la animación actual.
     */
    public void move(Direction direction, float delta, GameMap gameMap) {
        float newX = x;
        float newY = y;
        float d = speed * delta;
        currentDirection = direction;
        switch(direction) {
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
        // Actualizamos la animación actual según la dirección
        switch(direction) {
            case UP: currentAnimation = animUp; break;
            case DOWN: currentAnimation = animDown; break;
            case LEFT: currentAnimation = animLeft; break;
            case RIGHT: currentAnimation = animRight; break;
        }
    }

    /**
     * Verifica si el jugador puede moverse a la posición (newX, newY),
     * utilizando un área de colisión reducida (más fluida).
     */
    private boolean canMoveTo(float newX, float newY, GameMap gameMap) {
        // Se reduce el área de colisión en ambos ejes
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
                if (!gameMap.isCellPassable(row, col))
                    return false;
            }
        }
        return true;
    }

    /**
     * Actualiza la animación (solo avanza si se movió) y descuenta el cooldown de la bomba.
     */
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
    }

    // Métodos para los power-ups y acceso a la posición.
    public void increaseSpeed() { speed += 20; }
    public void increaseBombRadius() { bombRadius++; }
    public void activateShield() { shield = true; }
    public float getX() { return x; }
    public float getY() { return y; }

    public void dispose() {
        // Liberamos las texturas de la animación.
        animUp.getKeyFrame(0).getTexture().dispose();
        animDown.getKeyFrame(0).getTexture().dispose();
        animRight.getKeyFrame(0).getTexture().dispose();
        animLeft.getKeyFrame(0).getTexture().dispose();
    }

    /**
     * Coloca una bomba en la celda actual del jugador, reiniciando el cooldown.
     */
    public Bomb placeBomb() {
        if (bombCooldown <= 0f) {
            // Se redondea la posición a la celda más cercana para que la bomba aparezca donde está el jugador
            int bombCol = Math.round(x / GameMap.TILE_SIZE);
            int bombRow = Math.round(y / GameMap.TILE_SIZE);
            float bombX = bombCol * GameMap.TILE_SIZE;
            float bombY = bombRow * GameMap.TILE_SIZE;
            bombCooldown = bombCooldownTime;
            System.out.println("Bomba colocada en la celda (" + bombCol + ", " + bombRow + ")");
            return new Bomb(bombX, bombY, bombRadius);
        } else {
            System.out.println("Bomba en cooldown. Tiempo restante: " + bombCooldown);
            return null;
        }
    }

}
