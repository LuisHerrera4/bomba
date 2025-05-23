// LoseScreen.java
package com.example.bomba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoseScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture loseTexture;

    public LoseScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        loseTexture = new Texture("you_lose.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(loseTexture, Gdx.graphics.getWidth() / 2f - loseTexture.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - loseTexture.getHeight() / 2f);
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
        loseTexture.dispose();
    }
}
