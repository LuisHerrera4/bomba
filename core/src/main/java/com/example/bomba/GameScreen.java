package com.example.bomba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
    final MyBombermanGame game;
    OrthographicCamera camera;
    GameMap gameMap;
    Player player;
    Array<Enemy> enemies;
    Array<PowerUp> powerUps;
    Array<Bomb> bombs;

    // Botones táctiles
    private Texture btnUp, btnDown, btnLeft, btnRight, btnBomb;
    private Rectangle rectBtnUp, rectBtnDown, rectBtnLeft, rectBtnRight, rectBtnBomb;

    public GameScreen(final MyBombermanGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Inicializamos el mapa
        gameMap = new GameMap();

        // Colocar a los 4 personajes en cada esquina (del área interior, ya que el borde es pared).
        // Usamos GameMap.TILE_SIZE para calcular la posición según celdas.
        // Esquina inferior izquierda (jugador): (fila 1, columna 1)
        player = new Player(1 * GameMap.TILE_SIZE, 1 * GameMap.TILE_SIZE);

        // Inicializar los enemigos en las otras tres esquinas
        enemies = new Array<>();
        // Esquina inferior derecha: (fila 1, columna 23)
        enemies.add(new Enemy(23 * GameMap.TILE_SIZE, 1 * GameMap.TILE_SIZE));
        // Esquina superior izquierda: (fila 13, columna 1)
        enemies.add(new Enemy(1 * GameMap.TILE_SIZE, 13 * GameMap.TILE_SIZE));
        // Esquina superior derecha: (fila 13, columna 23)
        enemies.add(new Enemy(23 * GameMap.TILE_SIZE, 13 * GameMap.TILE_SIZE));

        powerUps = new Array<>();
        bombs = new Array<>();

        // Cargamos imágenes para botones (estos assets deben estar en tu carpeta de assets)
        btnUp = new Texture("btn_up.png");
        btnDown = new Texture("btn_down.png");
        btnLeft = new Texture("btn_left.png");
        btnRight = new Texture("btn_right.png");
        btnBomb = new Texture("btn_bomb.png");

        int btnSize = 50;
        int margin = 10;
        // Botones de desplazamiento en forma de D-Pad en la esquina inferior izquierda
        rectBtnLeft  = new Rectangle(margin, margin + btnSize, btnSize, btnSize);
        rectBtnDown  = new Rectangle(margin + btnSize, margin, btnSize, btnSize);
        rectBtnUp    = new Rectangle(margin + btnSize, margin + btnSize * 2, btnSize, btnSize);
        rectBtnRight = new Rectangle(margin + btnSize * 2, margin + btnSize, btnSize, btnSize);
        // Botón de bomba en la esquina inferior derecha
        rectBtnBomb  = new Rectangle(800 - margin - btnSize, margin, btnSize, btnSize);
    }


    @Override
    public void render(float delta) {
        update(delta);

        // Mostrar pantalla de derrota si el jugador está muerto
        if (!player.isAlive()) {
            game.setScreen(new LoseScreen(game));
            dispose();
            return;
        }

        // Mostrar pantalla de victoria si todos los enemigos están muertos
        boolean allEnemiesDead = true;
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                allEnemiesDead = false;
                break;
            }
        }
        if (allEnemiesDead) {
            game.setScreen(new WinScreen(game));
            dispose();
            return;
        }

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
        for (Bomb bomb : bombs) {
            bomb.render(game.batch);
        }
        game.batch.draw(btnLeft,  rectBtnLeft.x,  rectBtnLeft.y,  rectBtnLeft.width,  rectBtnLeft.height);
        game.batch.draw(btnDown,  rectBtnDown.x,  rectBtnDown.y,  rectBtnDown.width,  rectBtnDown.height);
        game.batch.draw(btnUp,    rectBtnUp.x,    rectBtnUp.y,    rectBtnUp.width,    rectBtnUp.height);
        game.batch.draw(btnRight, rectBtnRight.x, rectBtnRight.y, rectBtnRight.width, rectBtnRight.height);
        game.batch.draw(btnBomb,  rectBtnBomb.x,  rectBtnBomb.y,  rectBtnBomb.width,  rectBtnBomb.height);
        game.batch.end();
    }


    private void update(float delta) {
        // Actualizamos jugador.
        player.update(delta);

        Array<Enemy> enemiesCopy = new Array<>(enemies); // evitar iteración anidada

        for (Enemy enemy : enemiesCopy) {
            enemy.update(delta, gameMap, player, enemies);
            // sigue usando el array original como referencia
            Bomb enemyBomb = enemy.getBomb();
            if (enemyBomb != null) {
                bombs.add(enemyBomb);
            }
        }


        // Actualizamos bombas.
        for (int i = bombs.size - 1; i >= 0; i--) {
            Bomb b = bombs.get(i);
            b.update(delta);
            // Si el fuse termino y aun no ha explotado, se dispara la explosión.
            if (!b.hasExploded() && b.getFuseTime() <= 0f) {
                b.triggerExplosion(gameMap, powerUps);
                checkExplosionCollision(b);
            }
            if (b.isFinished()) {
                bombs.removeIndex(i);
                b.dispose();
            }
        }

        // Comprobamos colisiones entre jugador y power-ups.
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            if (checkCollision(player, p)) {
                activatePowerUp(p);
                powerUps.removeIndex(i);
            }
        }

        // para mover al jugador y lanzar bomba manualmente
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (rectBtnUp.contains(touchPos.x, touchPos.y)) {
                player.move(Direction.UP, delta, gameMap);
            } else if (rectBtnDown.contains(touchPos.x, touchPos.y)) {
                player.move(Direction.DOWN, delta, gameMap);
            } else if (rectBtnLeft.contains(touchPos.x, touchPos.y)) {
                player.move(Direction.LEFT, delta, gameMap);
            } else if (rectBtnRight.contains(touchPos.x, touchPos.y)) {
                player.move(Direction.RIGHT, delta, gameMap);
            } else if (rectBtnBomb.contains(touchPos.x, touchPos.y)) {
                Bomb newBomb = player.placeBomb();
                if (newBomb != null) {
                    bombs.add(newBomb);
                }
            }
        }

    }


    private void checkExplosionCollision(Bomb b) {
        int bx = (int)(b.getX() / GameMap.TILE_SIZE);
        int by = (int)(b.getY() / GameMap.TILE_SIZE);

        // Verifica si el jugador ha sido alcanzado por la explosión
        int px = (int)(player.getX() / GameMap.TILE_SIZE);
        int py = (int)(player.getY() / GameMap.TILE_SIZE);
        if ((px == bx && Math.abs(py - by) <= b.getRadius()) ||
            (py == by && Math.abs(px - bx) <= b.getRadius())) {
            player.kill();
            System.out.println("El jugador ha sido alcanzado por una bomba.");
        }

        // Verifica cada enemigo
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            int ex = (int)(enemy.getX() / GameMap.TILE_SIZE);
            int ey = (int)(enemy.getY() / GameMap.TILE_SIZE);

            // Si la bomba pertenece a este enemigo, no debe afectarlo
            if (b.getOwnerType() == Bomb.OwnerType.ENEMY && b.getOwnerId() == enemy.getId()) {
                continue; // Ignora esta bomba para el enemigo dueño
            }

            if ((ex == bx && Math.abs(ey - by) <= b.getRadius()) ||
                (ey == by && Math.abs(ex - bx) <= b.getRadius())) {
                enemy.kill();
            }
        }

        // Elimina enemigos muertos
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (!enemies.get(i).isAlive()) {
                enemies.removeIndex(i);
            }
        }
    }




    private boolean checkCollision(Player player, PowerUp p) {
        // Utilizamos las dimensiones originales (32x32) para la comprobación; ajusta si es necesario.
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), 32, 32);
        Rectangle powerRect = new Rectangle(p.getX(), p.getY(), 32, 32);
        return playerRect.overlaps(powerRect);
    }

    private void activatePowerUp(PowerUp p) {
        switch(p.getType()){
            case SPEED: player.increaseSpeed(); break;
            case BOMB_RADIUS: player.increaseBombRadius(); break;
            case SHIELD: player.activateShield(); break;
        }
    }

    private void spawnPowerUp(float x, float y) {
        PowerUp.PowerUpType type = PowerUp.PowerUpType.values()[com.badlogic.gdx.math.MathUtils.random(PowerUp.PowerUpType.values().length - 1)];
        powerUps.add(new PowerUp(type, x, y));
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
        for (Enemy enemy : enemies) enemy.dispose();
        for (PowerUp powerUp : powerUps) powerUp.dispose();
        for (Bomb bomb : bombs) bomb.dispose();
        btnUp.dispose();
        btnDown.dispose();
        btnLeft.dispose();
        btnRight.dispose();
        btnBomb.dispose();
    }



}
