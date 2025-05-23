package com.example.bomba;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMap {
    private Texture wallTexture;              // Muro indestructible (gris oscuro)
    private Texture destructibleWallTexture;  // Muro destructible (marrón fuerte)
    private Texture floorSafeTexture;         // Piso seguro (blanco)
    private int[][] map;
    public static final int TILE_SIZE = 32;
    private int rows = 15;
    private int cols = 25;

    public GameMap() {
        map = new int[rows][cols];
        // Generamos el mapa:
        // • Los bordes se fijan como muros fijos (valor 1).
        // • En el interior:
        //      - Si la celda es en fila y columna pares, es un muro fijo (valor 1) y se dibuja gris.
        //      - Si la celda cae en la zona segura (definida en isSafeZone) se fuerza piso (valor 0) y se dibuja en blanco.
        //      - En el resto, se colocan muros destructibles (valor 2) y se dibujan en marrón fuerte.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    map[r][c] = 1;
                } else if (isSafeZone(r, c)) {
                    map[r][c] = 0;
                } else if ((r % 2 == 0) && (c % 2 == 0)) {
                    map[r][c] = 1;
                } else {
                    map[r][c] = 2;
                }
            }
        }
        loadTextures();
    }

    /**
     * Define las zonas seguras en forma de L para cada esquina.
     * Se definen como sigue:
     *
     * - Esquina inferior izquierda:
     *     • fila 1, columnas 1 y 2
     *     • fila 2, columna 1
     * - Esquina inferior derecha:
     *     • fila 1, columnas cols-3 y cols-2
     *     • fila 2, columna cols-2
     * - Esquina superior izquierda:
     *     • fila rows-2, columnas 1 y 2
     *     • fila rows-3, columna 1
     * - Esquina superior derecha:
     *     • fila rows-2, columnas cols-3 y cols-2
     *     • fila rows-3, columna cols-2
     */
    private boolean isSafeZone(int r, int c) {
        // Esquina inferior izquierda:
        if (r == 1 && (c == 1 || c == 2)) return true;
        if (r == 2 && c == 1) return true;
        // Esquina inferior derecha:
        if (r == 1 && (c == cols - 3 || c == cols - 2)) return true;
        if (r == 2 && c == cols - 2) return true;
        // Esquina superior izquierda:
        if (r == rows - 2 && (c == 1 || c == 2)) return true;
        if (r == rows - 3 && c == 1) return true;
        // Esquina superior derecha:
        if (r == rows - 2 && (c == cols - 3 || c == cols - 2)) return true;
        if (r == rows - 3 && c == cols - 2) return true;
        return false;
    }

    private void loadTextures() {
        // Piso seguro: blanco
        floorSafeTexture = createColorTexture(TILE_SIZE, TILE_SIZE, Color.WHITE);
        // Muro destructible (valor 2)
        destructibleWallTexture = createColorTexture(TILE_SIZE, TILE_SIZE, new Color(0.55f, 0.27f, 0.07f, 1));
        // Muros fijos (valor 1)
        wallTexture = createColorTexture(TILE_SIZE, TILE_SIZE, Color.DARK_GRAY);
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    /**
     * Renderiza el mapa recorriendo cada celda y asignando la textura según el valor:
     *   0 → piso seguro (blanco),
     *   1 → muro fijo (gris oscuro),
     *   2 → muro destructible (marrón fuerte).
     */
    public void render(SpriteBatch batch) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Texture tex;
                if (map[r][c] == 0) {
                    tex = floorSafeTexture;
                } else if (map[r][c] == 1) {
                    tex = wallTexture;
                } else if (map[r][c] == 2) {
                    tex = destructibleWallTexture;
                } else {
                    tex = destructibleWallTexture;
                }
                batch.draw(tex, c * TILE_SIZE, r * TILE_SIZE);
            }
        }
    }

    public boolean isCellPassable(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols)
            return false;
        return map[row][col] == 0;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getCell(int row, int col) { return map[row][col]; }
    public void setCell(int row, int col, int value) { map[row][col] = value; }

    public void dispose() {
        wallTexture.dispose();
        destructibleWallTexture.dispose();
        floorSafeTexture.dispose();
    }
}
