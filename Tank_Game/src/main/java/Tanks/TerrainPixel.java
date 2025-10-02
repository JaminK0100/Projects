package Tanks;

import processing.core.PImage;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.FileReader;


import java.util.*;

public class TerrainPixel {

    public static Random random = new Random();

    public final int WIDTH;
    public final int HEIGHT;
    private char[][] levelLayout;
    public char[][] pixelLayout;

    public ArrayList<Integer> treeCol;
    public HashMap<Character, Integer> playerCol;

    /**
     * Constructor for pixelating the previous loaded levels from layout files to make terrain smooth.
     * @param row the number of rows from the board layout.
     * @param col the number of cols from the board layout.
     * @param level the board layout of the level.
     */
    public TerrainPixel(int row, int col, char[][] level){
        this.WIDTH = row * 32;
        this.HEIGHT = col * 32;
        this.levelLayout = level;
        this.pixelLayout = new char[WIDTH][HEIGHT];

        this.treeCol = new ArrayList<Integer>();
        this.playerCol = new HashMap<Character, Integer>();

        // Setting up all these variables so we can retrieve them later and place elements according to smooth terrain.
    }

    /**
     * A method for pixelating the board. Turns board into 32x32 pixels per each block.
     */
    public void toPixel() {
        for (int row = 0 ; row < levelLayout.length ; row ++) {
            for (int col = 0 ; col < levelLayout[row].length ; col ++) {
                if (levelLayout[row][col] == 'X') {
                    pixelateTerrain(row, col, 'X');
                } 
                else if (levelLayout[row][col] == 'T') {
                    int ran = random.nextInt(31) - 15; //Randomising tree position by 15 radius (horizontally)
                    if (col*32 + ran < 0) {
                        treeCol.add(0);
                    } else if (col*32 + ran > levelLayout[row].length * 32 - 1) {
                        treeCol.add(levelLayout[row].length - 1);
                    } else {
                        treeCol.add(col*32 + ran);
                    } // Checking if the random function would make tree go out of bounds or not.
                }
                else if (levelLayout[row][col] != ' ') {
                    playerCol.put(levelLayout[row][col], col);
                }
                else {
                    pixelateTerrain(row, col, ' ');
                }
            }
        }
    }

    // private method for turning characters into a 32x32 character to put in pixelated board.
    private void pixelateTerrain(int row, int col, char placement) {
        for (int pixelRow = row * 32 ; pixelRow < row * 32 + 32 ; pixelRow++) {
            for (int pixelCol = col * 32 ; pixelCol < col * 32 + 32 ; pixelCol++) {
                pixelLayout[pixelRow][pixelCol] = placement;
            }
        }
    }

    /**
     * Returns the position of trees in terms of their column positions as an ArrayList.
     * @return the ArrayList of trees' column positions.
     */
    public ArrayList<Integer> getTreeList(){
        return treeCol;
    }

    /**
     * Returns the positions of each player in terms of their column positions as a HashMap.
     * @return the HashMap of players' column positions.
     */
    public HashMap<Character, Integer> getPlayerMap(){
        return playerCol;
    }

    /**
     * Returns the pixelated layout which was processed from the other methods from the class.
     * @return the pixelated layout of the level.
     */
    public char[][] getPixelLayout() {
        return pixelLayout;
    }

    /**
     * Static method that returns the surface height of the terrain to enable ground foundation for other objects.
     * @param pixel the pixelated layout of the level.
     * @return the max height of the surface of the terrain generation.
     */
    public static int[] maxHeightsPos (char[][] pixel) {
        int[] max = new int[pixel[0].length];
        boolean[] checked = new boolean[pixel[0].length];

        for (int row = 0 ; row < pixel.length ; row++) {
            for (int col = 0 ; col < pixel[row].length ; col++) {
                if (!checked[col] && pixel[row][col] == 'X') {
                    checked[col] = true;
                    max[col] = row;
                }
            }
        }
        return max;
    }

    // private method for calculating moving average for terrain surface.
    private int[] movingAverage(int[] max) {
        int[] smooth = new int[max.length];
        for (int i = 0 ; i < max.length ; i++) {
            int sum = 0;
            int count = 0;
            for (int k = i ; (k < i + 32 && k < max.length); k++) { // making sure the end values do not get messed up/out of bounds.
                sum += max[k];
                count++;
            }
            int avg = sum/count;
            smooth[i] = avg;
        }
        return smooth;
    }

    /**
     * Returns the terrain height after calculating moving average 2 times so a smooth terrain is achieved.
     * @return the smoothened terrain surface height.
     */
    public int[] movingAverageLayout() {
        toPixel();
        int[] maxHeights = maxHeightsPos(pixelLayout);
        int[] smooth1 = movingAverage(maxHeights);
        int[] smooth2 = movingAverage(smooth1);

        return smooth2;
    }

    /**
     * Returns the complete smoothed layout that is completely filled and pixelated accordingly.
     * @return the pixelated layout of the level.
     */
    public char[][] smoothTerrain() {
        treeCol.clear();
        int[] smoothHeights = movingAverageLayout();

        char[][] pixel = new char[WIDTH][HEIGHT];
        boolean[] isX = new boolean[pixel[0].length];

        for (int row = 0 ; row < pixel.length ; row++) {
            for (int col = 0 ; col < pixel[row].length ; col++) {
                if (isX[col]) {
                    pixel[row][col] = 'X';
                }
                else if (smoothHeights[col] == row) {
                    isX[col] = true;
                    pixel[row][col] = 'X'; // fill X
                }
            }
        }
        return pixel;
    }

}