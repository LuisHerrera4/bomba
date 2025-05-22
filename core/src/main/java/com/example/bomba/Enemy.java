package com.example.bomba;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Enemy {
    private float x, y;
    private float speed;
    private int currentDirection; // 0: arriba, 1: abajo, 2: izquierda, 3: derecha
    private Texture texture;
    private float moveTimer;
    private float changeDirectionInterval;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 80f;
        texture = new Texture("enemy.png");
        chooseNewDirection();
    }

    private void chooseNewDirection() {
        currentDirection = MathUtils.random(3);
        changeDirectionInterval = MathUtils.random(1.0f, 3.0f);
        moveTimer = 0;
    }

    public void update(float delta, GameMap map) {
        moveTimer += delta;
        if (moveTimer > changeDirectionInterval) {
            chooseNewDirection();
        }
        float distance = speed * delta;
        switch (currentDirection) {
            case 0:
                y += distance;
                break;
            case 1:
                y -= distance;
                break;
            case 2:
                x -= distance;
                break;
            case 3:
                x += distance;
                break;
        }
        // Aquí se podría agregar validación de colisión con el mapa utilizando map.isCellPassable(...)
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
