package life;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class GameOfLife extends JFrame {
    JLabel aliveLabel;
    JLabel genLabel;
    boolean enabled = true;
    private int alive = 0;
    JPanel gridPanel;
    JPanel statusPanel;
    private static final int size = 30;
    private static final int generations = 100;
    static boolean[][] universe;
    JPanel[][] cellArray;
    private int generation = 0;
    JToggleButton play;
    JButton resetButton;
    // refactor so the worker just goes for gold?
    // refactor so that algorithm thread fires off workers, gol just draws forever?

    public GameOfLife(){
        setTitle(String.valueOf(this.getClass().getSimpleName()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 615);
        setVisible(true);
        initUniverse(size);
        setLayout(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(30, 30, 2, 2));
        gridPanel.setBackground(Color.decode("#ff972f"));
        statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.decode("#a25200"));
        genLabel = new JLabel("Generation: ".concat(String.valueOf(generation)));
        genLabel.setForeground(Color.WHITE);
        genLabel.setFont(genLabel.getFont().deriveFont(genLabel.getFont().getSize() + 7f));
        genLabel.setName("GenerationLabel");
        statusPanel.add(genLabel);
        aliveLabel = new JLabel("Alive: ".concat(String.valueOf(alive)));
        aliveLabel.setForeground(Color.WHITE);
        aliveLabel.setFont(aliveLabel.getFont().deriveFont(aliveLabel.getFont().getSize() + 7f));
        aliveLabel.setName("AliveLabel");
        statusPanel.add(aliveLabel);
        play = new JToggleButton("Play");
        play.setName("PlayToggleButton");
        play.addActionListener(actionEvent -> {
            play.setText(play.isSelected() ? "Pause" : "Play");
        });
        statusPanel.add(play);
        resetButton = new JButton("Reset");
        resetButton.setName("ResetButton");
        resetButton.addActionListener(actionEvent -> reset());
        statusPanel.add(resetButton);
        cellArray = new JPanel[size][size];
        add(statusPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
        cellArray = initCells();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                gridPanel.add(cellArray[y][x]);
            }
        }
        ThreadRunner runner = new ThreadRunner();
        runner.start();

        /*for (int i = 1; i <= generations; i++) {
            AlgorithmWorker algorithm = new AlgorithmWorker(universe);
            algorithm.execute();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }*/

    }

    private void reset() {
        /*Arrays.stream(universe).iterator().forEachRemaining(e -> {
            Arrays.fill(e, false);
        });*/
        initUniverse(size);
        generation = 0;
        alive = 0;
        updateFields();
    }

    private JPanel[][] initCells() {
        JPanel[][] arr = new JPanel[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                arr[y][x] = new JPanel();
                arr[y][x].setBackground(util.DEAD_COLOUR);
            }
        }
        return arr;
    }

    private static int getAlive(boolean[][] universe) {
        int count = 0;
        for (int y = 0; y < universe.length; y++) {
            for (int x = 0; x < universe[0].length; x++) {
                if (universe[y][x]) {
                    count++;
                }
            }
        }
        return count;
    }

    static void initUniverse(int size) {
        if (universe == null) {
            universe = new boolean[size][size];
        }
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                universe[i][j] = random.nextBoolean();// ? 'O' : ' ';
            }
        }
    }

    static void printUniverse(boolean[][] universe) {
        for (boolean[] row : universe) {
            for (int x = 0; x < universe[0].length; x++) {
                System.out.print(row[x] ? 'O' : ' ');
            }
            System.out.println();
        }
    }

    class ThreadRunner extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println(play.isSelected());
                if (play.isSelected()) {
                    AlgorithmWorker worker = new AlgorithmWorker(universe);
                    try {
                        worker.execute();
                        universe = worker.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    generation++;
                    updateFields();
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void updateFields() {
        //update cells
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                cellArray[y][x].setBackground(universe[y][x] ? util.ALIVE_COLOUR : util.DEAD_COLOUR);
            }
        }
        alive = getAlive(universe);
        aliveLabel.setText("Alive: ".concat(String.valueOf(alive)));

        genLabel.setText("Generation: ".concat(String.valueOf(generation)));
    }
}

