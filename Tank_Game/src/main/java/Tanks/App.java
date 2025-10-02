package Tanks;

import org.checkerframework.checker.units.qual.A;

import processing.core.PApplet;
import processing.core.PImage;
import ddf.minim.*;

import processing.data.JSONArray;
import processing.data.JSONObject;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.*;
import java.util.*;

import javax.annotation.Resource;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public static final int TREESIZE = 32;
    public int windSpeed;

    public Config config;

    public HashMap<String, ResourceManager> backgrounds;
    public HashMap<String, ResourceManager> trees;

    public ResourceManager[] wind = new ResourceManager[2];
    public PImage parachute;
    public PImage fuel;

    public char[][] levelLayout;
    public char[][] pixelLayout;

    public ArrayList<Integer> treeCols;
    public HashMap<Character, Integer> playerCols;
    public HashMap<Character, Tank> playerTanks;
    public ArrayList<Character> tankList;
    public int currentPlayerIndex = 0;
    public Tank currentTank;
    public int tanksAlive;
    public HashSet<Character> deadTanks;

    public ArrayList<Bullet> bullets;
    public ArrayList<Explosion> explosions;

    public PImage backgroundImage;

    public JSONObject json;
    
    public LoadLevel loader;
    public JSONObject playerColours;

    public String layout;
    public String gameBackground;
    public String foregroundColour;
    public String tree;

    public int currentLevel = 0;

    public boolean showArrow = true;
    public long arrowStartTime;
    public static final int arrowWaitTime = 2000;

    public boolean moveLevel;
    public boolean gameEnd;
    public boolean manualMoveLevel;
    public long nextLevelStartTime;
    public static final int waitTime = 1000;

    public AudioSnippet pew;
    public AudioSnippet pow;
    public AudioSnippet oof;
    public AudioSnippet back;
    public Minim minim;


    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * loads the level of the game specified by the level param.
     * @param level the level that the level will be loading.
     */
    public void nextLevel(int level) {
        moveLevel = false;
        showArrow = true;
        arrowStartTime = millis();
        manualMoveLevel = false;

        try {
            loader = new LoadLevel(config, currentLevel);
    
            layout = loader.getLevel();
            foregroundColour = loader.getForegroundColour();
            gameBackground = loader.getGameBackground();
            tree = loader.getTree();
    
            playerColours = loader.getPlayerColours();
    
            windSpeed = random.nextInt(71) - 35;
    
            Terrain loadTerrain = new Terrain(layout); // shaping new terrain
            levelLayout = loadTerrain.getLayout();
    
            TerrainPixel loadPixel = new TerrainPixel(20, 28, levelLayout);
            int[] smoothed = loadPixel.movingAverageLayout();
            pixelLayout = loadPixel.smoothTerrain();
    
            treeCols = loadPixel.getTreeList();
            playerCols = loadPixel.getPlayerMap();
    
            tanksAlive = 0; // reseting and retrieving tanks again
            for (char i : playerTanks.keySet()) {
                Tank tank = playerTanks.get(i);
                tank.setTankCentreX(playerCols.get(i) * 32);
                tank.setTankCentreY(smoothed[playerCols.get(i) * 32]);
                tank.setTankPath(smoothed);
                tank.revive();
                tanksAlive += 1;
            }

            bullets.clear();
            explosions.clear(); //clearing all previous stored values.
            deadTanks.clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Swicthes the currentPlayer to the next available tank.
     */
    public void switchNextPlayer() {
        while (true) {
            currentPlayerIndex++;

            if (currentPlayerIndex >= playerTanks.size()) {
                currentPlayerIndex = 0;
            }

            currentTank = playerTanks.get(tankList.get(currentPlayerIndex));
            if (currentTank.isAlive()) {
                break;
            }
        }
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        try {
            json = loadJSONObject(this.configPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        config = new Config(this.json);

        tanksAlive = 0;

        // Loading all resources from the folder
        try {
            backgrounds = new HashMap<String, ResourceManager>();
            backgrounds.put("basic.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/basic.png")));
            backgrounds.put("desert.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/desert.png")));
            backgrounds.put("forest.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/forest.png")));
            backgrounds.put("hills.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/hills.png")));
            backgrounds.put("snow.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/snow.png")));

            trees = new HashMap<String, ResourceManager>();
            trees.put("tree1.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/tree1.png")));
            trees.put("tree2.png", new ResourceManager(this.loadImage("src/main/resources/Tanks/tree2.png")));

            wind[0] = new ResourceManager(this.loadImage("src/main/resources/Tanks/wind.png"));
            wind[1] = new ResourceManager(this.loadImage("src/main/resources/Tanks/wind-1.png"));

            fuel = this.loadImage("src/main/resources/Tanks/fuel.png");
            parachute = this.loadImage("src/main/resources/Tanks/parachute.png");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Loading all images
        
        // initialising the level
        try {

            loader = new LoadLevel(config, currentLevel);

            layout = loader.getLevel();
            foregroundColour = loader.getForegroundColour();
            gameBackground = loader.getGameBackground();
            tree = loader.getTree();

            playerColours = loader.getPlayerColours();
            System.out.println(playerColours.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        windSpeed = random.nextInt(71) - 35;

        Terrain loadTerrain = new Terrain(layout);
        levelLayout = loadTerrain.getLayout();

        TerrainPixel loadPixel = new TerrainPixel(20, 28, levelLayout);
        int[] smoothed = loadPixel.movingAverageLayout();
        pixelLayout = loadPixel.smoothTerrain();

        treeCols = loadPixel.getTreeList();
        
        playerCols = loadPixel.getPlayerMap();

        playerTanks = new HashMap<Character, Tank>();
        for (char i : playerCols.keySet()) {// getting our tanks loaded out.
            playerTanks.put(i, new Tank(playerCols.get(i) * 32, smoothed[playerCols.get(i) * 32], playerColours.getString(String.valueOf(i)), 0, i, smoothed));
            tanksAlive += 1;
            System.out.println(playerColours.getString(String.valueOf(i)));
        }

        tankList = new ArrayList<Character>(playerTanks.keySet());
        currentTank = playerTanks.get(tankList.get(currentPlayerIndex));

        bullets = new ArrayList<Bullet>();
        explosions = new ArrayList<Explosion>();
        deadTanks = new HashSet<Character>();

        minim = new Minim(this);

        // All sounds from https://freesound.org/ and are free to use.

        // https://freesound.org/people/HighPixel/sounds/431174/ By HighPixel 26th MAY 2018
        pew = minim.loadSnippet("src/main/resources/Tanks/431174__highpixel__fireball-explosion.wav");

        // https://freesound.org/people/Logicogonist/sounds/264031/ By Logicogonist 13th FEB 2015
        pow = minim.loadSnippet("src/main/resources/Tanks/264031__logicogonist__low-mid-afar-explosion-1.wav");

        // https://freesound.org/people/Breviceps/sounds/450616/ By Breviceps 5th DEC 2018
        oof = minim.loadSnippet("src/main/resources/Tanks/450616__breviceps__8-bit-error.wav");

        // https://freesound.org/people/Setuniman/sounds/414279/ By Setuniman 27th DEC 2017
        back = minim.loadSnippet("src/main/resources/Tanks/414279__setuniman__preoccupied-1q21.wav");

        back.loop();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){

        if (keyCode == 37 && !gameEnd) {
            currentTank.moveLeft = true;
        }

        if (keyCode == 39 && !gameEnd) {
            currentTank.moveRight = true;
        }

        if (keyCode == 38 && !gameEnd) {
            currentTank.getTurret().rotateLeft();
        }

        if (keyCode == 40 && !gameEnd) {
            currentTank.getTurret().rotateRight();
        }

        if ((key == 'w' || key == 'W') && !gameEnd) {
            currentTank.increasePower = true;
        }

        if ((key == 's' || key == 'S') && !gameEnd) {
            currentTank.decreasePower = true;
        }

        if ((key == 'r' || key == 'R') && !gameEnd) {
            currentTank.buyHeal();
        }

        if ((key == 'f' || key == 'F') && !gameEnd) {
            currentTank.buyFuel();
        }

        if (keyCode == 32 && !gameEnd) {
            if (moveLevel) {
                manualMoveLevel = true;
            } 
            else {
                showArrow = false;
                windSpeed += random.nextInt(11) - 5;
                for (Bullet bullet : bullets) {
                    bullet.updateWindSpeed(windSpeed);
                }
                
                Bullet bullet = currentTank.getTurret().shoot(currentTank.getPower(), windSpeed);
                bullets.add(bullet);

                pew.rewind();
                pew.play();

                switchNextPlayer();

                showArrow = true;
                arrowStartTime = millis();
            }
        }

        if (((key == 'r' || key == 'R') && gameEnd == true) || key == 'l' || key == 'L') {
            bullets.clear();
            explosions.clear();
            deadTanks.clear();

            for (Tank tank : playerTanks.values()) {
                tank.revive();
                tank.setScore(0);
            }

            currentLevel = 0;
            tanksAlive = playerTanks.size();
            moveLevel = false;
            gameEnd = false;
            manualMoveLevel = false;

            nextLevel(currentLevel);
        }

        if (key == 'P' || key == 'p') {
            currentLevel += 1;
            if (currentLevel >= 3) {
                gameEnd = true;
            } else
                nextLevel(currentLevel);
                this.showArrow = true;
                arrowStartTime = millis();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        if (keyCode == 37) {
            currentTank.moveLeft = false;
        }

        if (keyCode == 39) {
            currentTank.moveRight = false;
        }

        if (keyCode == 38) {
            currentTank.getTurret().rotateLeft = false;
        }

        if (keyCode == 40) {
            currentTank.getTurret().rotateRight = false;
        }

        if (key == 'w' || key == 'W') {
            currentTank.increasePower = false;
        }

        if (key == 's' || key == 'S') {
            currentTank.decreasePower = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        this.noStroke();

        background(backgrounds.get(gameBackground).getResource());

        if (moveLevel) {
            if (manualMoveLevel || millis() - nextLevelStartTime > waitTime) { // if bigger than 1 sec
                currentLevel += 1;
                if (currentLevel >= 3) {
                    moveLevel = false;
                    gameEnd = true; // ggs
                }
                else {
                    nextLevel(currentLevel); // moving new level 
                }
            }
        }

        if (gameEnd) {
            bullets.clear();
            explosions.clear(); // no one can move this time around.
            
            String winner = "";
            int highestScore = -1;
            Tank winnerTank = null;
            int[] winnerColour = new int[3];
            for (Tank tank: playerTanks.values()) {
                if (tank.getScore() > highestScore) {
                    highestScore = tank.getScore();
                    winnerTank = tank;
                    winnerColour = tank.getColour();
                    winner = "Player " + tank.getPlayer() + " wins!"; // printing winners
                }
            }

            this.fill(winnerColour[0], winnerColour[1], winnerColour[2]); // winner colour;
            this.textSize(32);
            this.textAlign(CENTER, CENTER);
            this.text(winner, WIDTH/2, 120);
            this.textAlign(BASELINE, BASELINE); //adjusting this text position.

            ArrayList<Tank> sortedTanksScore = new ArrayList<Tank>(playerTanks.values());
            sortedTanksScore.sort(Comparator.comparingInt(Tank::getScore).reversed()); //getting scores sorted

            float delay = 0.7f;

            this.stroke(0);
            this.strokeWeight(5);
            this.fill(winnerColour[0], winnerColour[1], winnerColour[2], 64);
            this.rect(312, 150, 240, 32);

            this.fill(0); //setting win game borders
            this.textSize(24);
            this.text("Final Scores", 322, 175); 
            this.fill(winnerColour[0], winnerColour[1], winnerColour[2], 64);
            this.rect(312, 182, 240, 10 + sortedTanksScore.size() * 30);
            this.noStroke();

            int counter = 1; //Setting counter for spacing when displaying score
            for (Tank tank : sortedTanksScore) {
                int score = tank.getScore();
                int[] colour = tank.getColour();

                this.fill(colour[0],colour[1],colour[2]); //colour of player
                this.textSize(24);
                this.text(("Player " + tank.getPlayer()), 322, 180 + counter * 30);

                this.fill(0);
                this.textSize(24);
                this.text(score, 502, 180 + counter * 30);

                counter++; // ordering tanks from a numerical score
            }
        }

        String[] groundString = foregroundColour.split(",");
        int[] groundColours = new int[3];
        groundColours[0] = Integer.parseInt(groundString[0]);
        groundColours[1] = Integer.parseInt(groundString[1]);
        groundColours[2] = Integer.parseInt(groundString[2]);

        for (int row = 0; row < 20 * 32; row++) {
            for (int col = 0 ; col < 28 * 32; col++) {
                if (pixelLayout[row][col] == 'X') {
                    this.fill(groundColours[0], groundColours[1], groundColours[2]);
                    this.rect(col, row, 1, 1);
                }
            }
        }

        for (Integer col : treeCols) {
            for (int row = 0; row < pixelLayout.length; row++) {
                if (treeCols != null && tree != null && row + 1 < pixelLayout.length) {
                    if (pixelLayout[row+1][col] == 'X' && pixelLayout[row][col] != 'X') {
                        this.image(trees.get(tree).getResource(), col - TREESIZE/2, row - TREESIZE + 4, TREESIZE, TREESIZE);
                    } // spawn trees yay
                }
            }
        }

        if (windSpeed > 0) {
            this.image(wind[0].getResource(), 760, 0, 55, 55);
        }
        if (windSpeed < 0) {
            this.image(wind[1].getResource(), 760, 0, 55, 55);
        }
        this.fill(0); // getting wind direction from image, if 0 no wind!
        this.textSize(16);
        this.text(windSpeed, 820, 30);


        currentTank.move(FPS);
        currentTank.getTurret().rotate(FPS);
        currentTank.changePower(FPS);

        if (showArrow && millis() - arrowStartTime < arrowWaitTime) {
            this.stroke(0);
            this.strokeWeight(5);
            this.fill(0);
            this.line(currentTank.getTankCentreX(), currentTank.getTankCentreY() - 80,
                currentTank.getTankCentreX() - 25, currentTank.getTankCentreY() - 105);
            this.line(currentTank.getTankCentreX(), currentTank.getTankCentreY() - 80,
                currentTank.getTankCentreX() + 25, currentTank.getTankCentreY() - 105); //drawing arrow above head
            this.line(currentTank.getTankCentreX(), currentTank.getTankCentreY() - 80,
                currentTank.getTankCentreX(), currentTank.getTankCentreY() - 160);
        } else {
            showArrow = false;
        }

        if (!currentTank.isAlive()) {
            switchNextPlayer(); // move next player if died
        }

        this.stroke(0);
        this.strokeWeight(3);
        this.fill(0,0,0,0);
        this.rect(720, 55, 120, 20);

        this.fill(0);
        this.textSize(12);
        this.text("Scores", 725, 70);

        int counter = 1;
        for (char i : playerTanks.keySet()) {
            Tank inspectTank = playerTanks.get(i);

            int score = inspectTank.getScore();
            int[] colour = inspectTank.getColour();

            this.fill(colour[0],colour[1],colour[2]);
            this.textSize(12);
            this.text(("Player " + i), 725, 76 + counter * 14);

            this.fill(0);
            this.textSize(12);
            this.text(score, 815, 76 + counter * 14); // again with counter

            if (inspectTank.isAlive()) {
                inspectTank.draw(this);

                if (inspectTank == currentTank) {
                    currentTank.drawParachuteFuel(this, fuel, parachute);
                    currentTank.getHealthBar().display(this, 440, 10, currentTank); // only show when alive.
                    currentTank.getPowerBar().display(this, 440, 10, currentTank);
                }
            }
            else if (!deadTanks.contains(inspectTank.getPlayer())) {
                oof.rewind();
                oof.play(); // death sound
                tanksAlive -= 1; // reducing tanks alive
                deadTanks.add(inspectTank.getPlayer());
                explosions.add(inspectTank.deathExplosion()); // if a tank just died, add a new explosion for tank death
            }
            counter++;
        }

        if (tanksAlive <= 1 && !moveLevel) {
            moveLevel = true;
            nextLevelStartTime = millis();
        }

        this.stroke(0);
        this.strokeWeight(3);
        this.fill(0,0,0,0);
        this.rect(720, 55, 120, 14 + counter * 14); // set frame

        for (int i = bullets.size() - 1 ; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.updateWindSpeed(windSpeed); // update the wind speed of all bullets since wind changes when player moves
            bullet.move(FPS);
            bullet.render(this);

            if (bullet.getBulletX() < 0 || bullet.getBulletX() > 864 - 1 || bullet.getBulletY() > 640 - 1) {
                bullets.remove(i);
            } // out of bounds

            else if (bullet.getBulletY() > 0 && pixelLayout[(int)bullet.getBulletY()][(int)bullet.getBulletX()] == 'X') {
                Explosion explosion = bullet.explode();
                explosions.add(explosion);
                bullets.remove(i);
            } // hit gorund

            else if (bullet.getBulletY() > 625 && pixelLayout[639][(int)bullet.getBulletX()] == 'X') {
                Explosion explosion = bullet.explode();
                explosions.add(explosion);
                bullets.remove(i);
            } // checking if explosion is very close to bottom area (since bullets velocity sometimes causes it to go through 2-3 pixels)
        }

        for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion explosion = explosions.get(i);

            if (explosion == null)
                continue;
            boolean finished = explosion.update();
            if (finished) {
                pow.rewind();
                pow.play();

                explosions.remove(i);
                explosion.destroyTerrain(pixelLayout); //first destroy terrain from explosion.

                int rows = pixelLayout.length;
                int cols = pixelLayout[0].length;

                for (int row = 0 ; row < rows ; row++) {
                    for (int col = 0; col < cols; col++) {
                        if (pixelLayout[row][col] == 'X') {
                            for (int below = rows - 1; below >= row; below--) {
                                if (pixelLayout[below][col] != 'X' && pixelLayout[below][col] == ' ') {
                                    pixelLayout[below][col] = 'X';
                                    pixelLayout[row][col] = ' '; // updating terrain here by shifting above terrain down
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        
            explosion.boom(this);

            ArrayList<Tank> tankList = new ArrayList<Tank>(playerTanks.values());
            explosion.damageTanks(tankList); //hurt them!!

            for (Tank tank: playerTanks.values()) {
                tank.setTankPath(TerrainPixel.maxHeightsPos(pixelLayout)); //update new tank max height paths
            }
        }

        for (Tank tank: playerTanks.values()) {
            if (tank.getTankCentreX() >= 0 && tank.getTankCentreX() < 864 && tank.getTankCentreY() >= 0 && tank.getTankCentreY() < 640) {
                if (pixelLayout[tank.getTankCentreY()][tank.getTankCentreX()] != 'X' && tank.isAlive()) {
                    tank.fall(FPS, this, parachute); // fall if they are floating
                }
            }
            else {
                tank.killed();
            }
        }

    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }
}