package com.example.bomba;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMap {
    private Texture wallTexture;
    private Texture floorTexture;
    private int[][] map;

    public static final int TILE_SIZE = 32;

    public GameMap() {
        loadTextures();
        // Crea un mapa de 15 filas x 25 columnas
        map = new int[15][25];
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                // Los bordes serán muros
                if (row == 0 || row == map.length - 1 || col == 0 || col == map[0].length - 1)
                    map[row][col] = 1;
                else
                    map[row][col] = 0;
            }
        }
        // Añade muros internos con una periodicidad sencilla
        for (int row = 2; row < map.length - 1; row += 2) {
            for (int col = 2; col < map[0].length - 1; col += 2) {
                map[row][col] = 1;
            }
        }
    }

    private void loadTextures() {
        // Genera una textura para el muro en color gris y otra para el suelo en color blanco.
        wallTexture = createColorTexture(TILE_SIZE, TILE_SIZE, Color.GRAY);
        floorTexture = createColorTexture(TILE_SIZE, TILE_SIZE, Color.WHITE);
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void render(SpriteBatch batch) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                // Si la celda es 1, dibuja muro; si es 0, suelo.
                Texture tex = (map[row][col] == 1) ? wallTexture : floorTexture;
                batch.draw(tex, col * TILE_SIZE, row * TILE_SIZE);
            }
        }
    }

    public boolean isCellPassable(int row, int col) {
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length)
            return false;
        return map[row][col] == 0;
    }

    public void dispose() {
        wallTexture.dispose();
        floorTexture.dispose();
    }
}
