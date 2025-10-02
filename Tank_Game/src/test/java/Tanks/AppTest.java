package Tanks;

import processing.core.PApplet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Tanks.Tank;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private App app;

    @BeforeEach
    public void setup() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
    }

    @Test
    public void testConfig() {
        assertNotNull(app.config);
    }

    @Test
    public void testInitialSettings() {
        assertEquals(864, app.WIDTH);
        assertEquals(640, app.HEIGHT);
        assertEquals(30, app.FPS);
    }

    @Test
    public void testNextLevelInitialization() {
        app.nextLevel(0);
        assertNotNull(app.levelLayout);
        assertNotNull(app.pixelLayout);
        assertNotNull(app.treeCols);
        assertNotNull(app.playerCols);
        assertEquals(0, app.bullets.size());
        assertEquals(0, app.explosions.size());
        assertEquals(app.playerTanks.size(), app.tanksAlive);
    }

    @Test
    public void testSwitchNextPlayer() {
        app.nextLevel(0);
        char initialPlayer = app.tankList.get(app.currentPlayerIndex);
        app.switchNextPlayer();
        assertNotEquals(initialPlayer, app.tankList.get(app.currentPlayerIndex));
    }

    @Test
    public void testKeyPresses() {
        app.nextLevel(0);
        Tank initialTank = app.currentTank;
        Boolean gameEnd = app.gameEnd;
        
        // Simulate left arrow key press
        app.keyCode = 37;
        app.keyPressed(null);
        assertTrue(initialTank.moveLeft);

        // Simulate right arrow key press
        app.keyCode = 39;
        app.keyPressed(null);
        assertTrue(initialTank.moveRight);

        // Simulate turret rotation left
        app.keyCode = 38;
        app.keyPressed(null);
        assertTrue(initialTank.getTurret().rotateLeft);

        // Simulate turret rotation right
        app.keyCode = 40;
        app.keyPressed(null);
        assertTrue(initialTank.getTurret().rotateRight);

        // Simulate power increase
        app.key = 'w';
        app.keyPressed(null);
        assertTrue(initialTank.increasePower);

        // Simulate power decrease
        app.key = 's';
        app.keyPressed(null);
        assertTrue(initialTank.decreasePower);

        // Simulate space bar press for shooting
        app.keyCode = 32;
        int initialBulletCount = app.bullets.size();
        app.keyPressed(null);
        assertEquals(initialBulletCount + 1, app.bullets.size());
    }

    @Test
    public void testKeyPressesCaps() {
        app.nextLevel(0);
        Tank initialTank = app.currentTank;
        Boolean gameEnd = app.gameEnd;

        // Simulate power increase
        app.key = 'W';
        app.keyPressed(null);
        assertTrue(initialTank.increasePower);

        // Simulate power decrease
        app.key = 'S';
        app.keyPressed(null);
        assertTrue(initialTank.decreasePower);
    }


    @Test
    public void testKeyReleases() {
        app.nextLevel(0);
        Tank initialTank = app.currentTank;

        // Simulate left arrow key release
        app.keyCode = 37;
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.moveLeft);
        assertTrue(!initialTank.moveLeft);

        // Simulate right arrow key release
        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.moveRight);
        assertTrue(!initialTank.moveRight);

        // Simulate turret rotation left release
        app.keyCode = 38;
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.getTurret().rotateLeft);
        assertTrue(!initialTank.getTurret().rotateLeft);

        // Simulate turret rotation right release
        app.keyCode = 40;
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.getTurret().rotateRight);
        assertTrue(!initialTank.getTurret().rotateRight);

        // Simulate power increase release
        app.key = 'w';
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.increasePower);
        assertTrue(!initialTank.increasePower);

        // Simulate power decrease release
        app.key = 's';
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.decreasePower);
        assertTrue(!initialTank.decreasePower);
    }

    @Test
    public void testKeyReleasesCaps() {
        app.nextLevel(0);
        Tank initialTank = app.currentTank;

        // Simulate power increase release
        app.key = 'W';
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.increasePower);
        assertTrue(!initialTank.increasePower);

        // Simulate power decrease release
        app.key = 'S';
        app.keyPressed();
        app.keyReleased();
        assertFalse(initialTank.decreasePower);
        assertTrue(!initialTank.decreasePower);
    }

    @Test
    public void testTankFire() {
        int initialBulletCount = app.bullets.size();
        app.keyCode = 32;
        app.keyPressed(null);
    
        int newBulletCount = app.bullets.size();
        assertEquals(initialBulletCount + 1, newBulletCount);
    }

    @Test
    public void testGameResetAndNextLevel() {
        // Simulate end of game
        app.gameEnd = true;

        // Reset game
        app.key = 'r';
        app.keyPressed(null);

        // Ensure game state is reset
        assertFalse(app.gameEnd);
        assertEquals(0, app.currentLevel);

        // Start next level
        app.key = 'p';
        app.keyPressed(null);
        assertEquals(1, app.currentLevel);
    }

    @Test
    public void testGameResetAndNextLevelCaps() {
        // Simulate end of game
        app.gameEnd = true;

        // Reset game
        app.key = 'R';
        app.keyPressed(null);

        // Ensure game state is reset
        assertFalse(app.gameEnd);
        assertEquals(0, app.currentLevel);

        // Start next level
        app.key = 'P';
        app.keyPressed(null);
        assertEquals(1, app.currentLevel);
    }

    @Test
    public void testTankBuysHealAndFuel() {
        // Assume currentTank is initialized and assigned a score.
        Tank currentTank = app.currentTank;
        currentTank.setScore(50);  // Set an initial score for testing.

        int initialHealth = currentTank.getHP();
        int initialFuel = currentTank.getFuel();

        app.gameEnd = false;

        // Simulate pressing the 'r' key to buy heal
        app.key = 'r';
        app.keyPressed(null);
        
        // Check if the heal was bought
        assertEquals(Math.min(initialHealth + 20, currentTank.getMaxHP()), currentTank.getHP());
        assertEquals(30, currentTank.getScore());

        // Simulate pressing the 'f' key to buy fuel
        app.key = 'f';
        app.keyPressed(null);

        // Check if the fuel was bought
        assertEquals(initialFuel + 200, currentTank.getFuel());
        assertEquals(20, currentTank.getScore());
    }

}