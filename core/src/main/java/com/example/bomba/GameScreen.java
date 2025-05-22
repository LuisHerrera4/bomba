package com.example.bomba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
    final MyBombermanGame game;
    OrthographicCamera camera;
    GameMap gameMap;
    Player player;
    Array<Enemy> enemies;
    Array<PowerUp> powerUps;

    public GameScreen(final MyBombermanGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // Ejemplo de viewport

        gameMap = new GameMap();
        player = new Player(64, 64); // Posición inicial del jugador

        // Creamos dos enemigos con movimiento aleatorio básico
        enemies = new Array<Enemy>();
        enemies.add(new Enemy(200, 200));
        enemies.add(new Enemy(300, 300));

        // Añadimos tres power-ups: velocidad, aumento de radio y escudo
        powerUps = new Array<PowerUp>();
        powerUps.add(new PowerUp(PowerUp.PowerUpType.SPEED, 100, 100));
        powerUps.add(new PowerUp(PowerUp.PowerUpType.BOMB_RADIUS, 150, 150));
        powerUps.add(new PowerUp(PowerUp.PowerUpType.SHIELD, 200, 100));
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        gameMap.render(game.batch);
        player.render(game.batch);
        for (Enemy enemy : enemies) {
            enemy.render(game.batch);
        }
        for (PowerUp powerUp : powerUps) {
            powerUp.render(game.batch);
        }
        game.batch.end();
    }

    private void update(float delta) {
        handleInput(delta);

        player.update(delta);
        for (Enemy enemy : enemies) {
            enemy.update(delta, gameMap);
        }
        // Comprobación simple de colisión entre jugador y power-ups (asumiendo 32x32 píxeles)
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            if (checkCollision(player, p)) {
                activatePowerUp(p);
                powerUps.removeIndex(i);
            }
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.move(Direction.UP, delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.move(Direction.DOWN, delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.move(Direction.LEFT, delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.move(Direction.RIGHT, delta);
        }
    }

    private boolean checkCollision(Player player, PowerUp p) {
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), 32, 32);
        Rectangle powerRect = new Rectangle(p.getX(), p.getY(), 32, 32);
        return playerRect.overlaps(powerRect);
    }

    private void activatePowerUp(PowerUp p) {
        switch(p.getType()){
            case SPEED:
                player.increaseSpeed();
                break;
            case BOMB_RADIUS:
                player.increaseBombRadius();
                break;
            case SHIELD:
                player.activateShield();
                break;
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        gameMap.dispose();
        player.dispose();
        for (Enemy enemy : enemies)
            enemy.dispose();
        for (PowerUp powerUp : powerUps)
            powerUp.dispose();
    }
}
