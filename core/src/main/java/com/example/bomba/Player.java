package com.example.bomba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private float x, y;
    private float speed; // en píxeles por segundo
    private int bombRadius;
    private boolean shield;

    // Animación
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    // Texturas de las animaciones; idealmente se gestionarían con un AssetManager
    private Texture texture1;
    private Texture texture2;
    private Texture texture3;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 100f;
        this.bombRadius = 1;
        this.shield = false;
        loadAnimations();
        stateTime = 0f;
    }

    private void loadAnimations() {
        texture1 = new Texture("player1.png");
        texture2 = new Texture("player2.png");
        texture3 = new Texture("player3.png");

        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(texture1);
        frames[1] = new TextureRegion(texture2);
        frames[2] = new TextureRegion(texture3);

        walkAnimation = new Animation<TextureRegion>(0.15f, frames);
    }

    public void move(Direction direction, float delta) {
        float distance = speed * delta;
        switch (direction) {
            case UP:
                y += distance;
                break;
            case DOWN:
                y -= distance;
                break;
            case LEFT:
                x -= distance;
                break;
            case RIGHT:
                x += distance;
                break;
        }
        stateTime += delta;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);
    }

    // Getters para la colisión
    public float getX() { return x; }
    public float getY() { return y; }

    // Métodos para activar los power‑ups
    public void increaseSpeed() {
        speed += 20;
    }

    public void increaseBombRadius() {
        bombRadius += 1;
    }

    public void activateShield() {
        shield = true;
    }

    public void dispose() {
        texture1.dispose();
        texture2.dispose();
        texture3.dispose();
    }
}
