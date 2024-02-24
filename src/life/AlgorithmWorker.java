package life;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class AlgorithmWorker extends SwingWorker<boolean[][], boolean[][]> {
    private final boolean[][] universe;

    public AlgorithmWorker(boolean[][] universe) {
        this.universe = universe;
    }

    private static boolean amIAlive(boolean[][] universe, int y, int x) {
        int height = universe.length;
        int width = universe[0].length;
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int neighbourY = (height + y + i) % height;
                int neighbourX = (height + x + j) % width;
                if (universe[neighbourY][neighbourX]) {
                    count++;
                }
            }
        }
        return (universe[y][x] && count == 2) || count == 3;
    }

    @Override
    protected void done() {
        try {
            GameOfLife.universe = get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean[][] doInBackground() throws InterruptedException {
        int height = universe.length;
        int width = universe[0].length;
        boolean[][] nextUniverse = new boolean[height][width];
        for (int y = 0; y < universe.length; y++) {
            for (int x = 0; x < universe[0].length; x++) {
                nextUniverse[y][x] = amIAlive(universe, y, x);
            }
        }
        return nextUniverse;
    }
}
