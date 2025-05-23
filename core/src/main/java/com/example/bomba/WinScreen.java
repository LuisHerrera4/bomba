// WinScreen.java
package com.example.bomba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WinScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture winTexture;

    public WinScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        winTexture = new Texture("you_win.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(winTexture,
            Gdx.graphics.getWidth() / 2f - winTexture.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - winTexture.getHeight() / 2f);
        batch.end();
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
    }
}
