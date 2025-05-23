package com.example.bomba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class WinScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture winTexture;
    private Texture btnRestart;
    private Rectangle rectRestart;

    public WinScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        winTexture = new Texture("you_win.png");
        btnRestart = new Texture("btn_restart.png"); // Agrega esta imagen a la carpeta assets
        rectRestart = new Rectangle(Gdx.graphics.getWidth() / 2f - 50, 100, 100, 40);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(winTexture,
            Gdx.graphics.getWidth() / 2f - winTexture.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - winTexture.getHeight() / 2f);
        batch.draw(btnRestart, rectRestart.x, rectRestart.y, rectRestart.width, rectRestart.height);
        batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            if (rectRestart.contains(touchPos.x, Gdx.graphics.getHeight() - touchPos.y)) {
                game.setScreen(new GameScreen((MyBombermanGame) game)); // Reinicia el juego
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        winTexture.dispose();
        btnRestart.dispose();
    }
}
