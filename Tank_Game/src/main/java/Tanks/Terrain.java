package Tanks;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.*;


public class Terrain {

    public char[][] layout = new char[20][28];

    private boolean[] check = new boolean[28];

    /**
     * Constructor to set the Layout file so that the level can be read an a board can be set.
     * @param LayoutFile the level's layout file.
     */
    public Terrain(String LayoutFile) {
        setLayout(LayoutFile);
    }

    /**
     * Method for loading and setting up the layout board which stored in this class after being generated.
     * @param file the layout file that is to be processed and transformed into a board layout.
     */
    public void setLayout (String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            int row = 0;

            // If not null we wll mark them accordingly as shown from layout file.
            while ((line = br.readLine()) != null && row < 20) {
                if (line.length() == 28) {
                    for (int col = 0; col < 28 ; col++) {
                        layout[row][col] = line.charAt(col);
                        if (line.charAt(col) == 'X') {
                            check[col] = true;
                        }
                        if (check[col]) {
                            layout[row][col] = 'X';
                        }
                    }
                } else {
                    for (int col = 0; col < line.length() ; col++) {
                        layout[row][col] = line.charAt(col);
                        if (line.charAt(col) == 'X') {
                            check[col] = true;
                        }// Basically filling up all empty spaces below the max height to be X
                        if (check[col]) {
                            layout[row][col] = 'X';
                        }
                    }
                    for (int col = line.length(); col < 28; col++) {
                        if (check[col]) {
                            layout[row][col] = 'X';
                        } else {
                            layout[row][col] = ' ';
                        }
                    }
                }
                row++;
            }
            br.close();

            while (row < 20) {
                for (int col = 0 ; col < 28 ; col ++) {
                    if (check[col]) {
                        layout[row][col] = 'X';
                    } else {
                        layout[row][col] = ' ';
                    }
                }
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter method for the layout array.
     * @return layout array for the level.
     */
    public char[][] getLayout() {
        return this.layout;
    }
} 