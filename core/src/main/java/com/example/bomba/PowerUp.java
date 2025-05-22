package com.example.bomba;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PowerUp {
    public enum PowerUpType {
        SPEED,
        BOMB_RADIUS,
        SHIELD;
    }

    private PowerUpType type;
    private float x, y;
    private Texture texture;

    public PowerUp(PowerUpType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        loadTexture();
    }

    private void loadTexture() {
        switch (type) {
            case SPEED:
                texture = new Texture("power_speed.png");
                break;
            case BOMB_RADIUS:
                texture = new Texture("power_bomb.png");
                break;
            case SHIELD:
                texture = new Texture("power_shield.png");
                break;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public PowerUpType getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
        texture.dispose();
    }
}
