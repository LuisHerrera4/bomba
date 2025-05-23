package com.example.bomba;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingScreen implements Screen {

    final MyBombermanGame game;
    private AssetManager assetManager;
    private BitmapFont font; // Fuente para mostrar el progreso

    public LoadingScreen(final MyBombermanGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        font = new BitmapFont(); // Fuente por defecto
        loadAssets();
    }

    private void loadAssets() {
        // Cargamos las imágenes de movimiento para el jugador/enemigo
        assetManager.load("Movimiento/Atras/player_U1.png", Texture.class);
        assetManager.load("Movimiento/Atras/player_U2.png", Texture.class);
        assetManager.load("Movimiento/Atras/player_U3.png", Texture.class);
        assetManager.load("Movimiento/DeCara/player_D1.png", Texture.class);
        assetManager.load("Movimiento/DeCara/player_D2.png", Texture.class);
        assetManager.load("Movimiento/DeCara/player_D3.png", Texture.class);
        assetManager.load("Movimiento/Derecha/player_R1.png", Texture.class);
        assetManager.load("Movimiento/Derecha/player_R2.png", Texture.class);
        assetManager.load("Movimiento/Derecha/player_R3.png", Texture.class);
        assetManager.load("Movimiento/Izquierda/player_L1.png", Texture.class);
        assetManager.load("Movimiento/Izquierda/player_L2.png", Texture.class);
        assetManager.load("Movimiento/Izquierda/player_L3.png", Texture.class);
        // Además, cargamos otros assets ya utilizados:
        assetManager.load("btn_bomb.png", Texture.class);
        assetManager.load("btn_up.png", Texture.class);
        assetManager.load("btn_down.png", Texture.class);
        assetManager.load("btn_left.png", Texture.class);
        assetManager.load("btn_right.png", Texture.class);
        assetManager.load("btn_bomb.png", Texture.class);
        assetManager.load("power_speed.png", Texture.class);
        assetManager.load("power_bomb.png", Texture.class);
        assetManager.load("power_shield.png", Texture.class);

        // Imágenes de la animación de explosión (ubicadas en el directorio "bomba")
        assetManager.load("bomba/explosion_derecha_izquierda.png", Texture.class);
        assetManager.load("bomba/explosion_derechaYizquierda_sinFinal.png", Texture.class);
        assetManager.load("bomba/explosion_final_abajo.png", Texture.class);
        assetManager.load("bomba/explosion_final_arriba.png", Texture.class);
        assetManager.load("bomba/explosion_medio.png", Texture.class);
        assetManager.load("bomba/explosion_SinFinal_arribaYabajo.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        if (assetManager.update()) {
            game.setScreen(new GameScreen(game));
        } else {
            float progress = assetManager.getProgress();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            SpriteBatch batch = game.batch;
            batch.begin();
            font.draw(batch, "Loading: " + (int)(progress * 100) + "%",
                Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2);
            batch.end();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
    }
}
