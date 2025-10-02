package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.*;
import java.util.*;

public class Tank {

    private int x;
    private int y;

    private int width;
    private int height;

    public boolean moveLeft;
    public boolean moveRight;

    public boolean increasePower;
    public boolean decreasePower;

    private int moveSpeed;
    private int fuelLeft;
    private int fallSpeed;
    private boolean isFalling;

    private Turret turret;

    private int hp;
    private final int maxhp = 100;
    private int power;
    private String playerColour;

    private int score;
    private char player;

    private int parachuteCount;

    private boolean alive;
    private boolean deathFromVoid;
    private boolean hasExploded;

    private int[] maxHeights;

    private Bar hpBar;
    private Bar powerBar;

    /**
     * Constructor used to create a tank at a position carrying player identity and score and terrain traversing height.
     * @param x the X position of tank.
     * @param y the Y position of tank.
     * @param colour the colour of tank.
     * @param score the score of tank.
     * @param player the player character of tank.
     * @param maxHeights the max height the tank can walk on.
     */
    public Tank (int x, int y, String colour, int score, char player, int[] maxHeights) {
        this.x = x;
        this.y = y;

        this.moveLeft = false;
        this.moveRight = false;
        this.increasePower = false;
        this.decreasePower = false;
        
        this.moveSpeed = 60;
        this.fuelLeft = 250;
        this.fallSpeed = 120;
        this.isFalling = false;

        this.width = 24;
        this.height = 12;

        this.turret = new Turret(x, y, this);

        this.hp = maxhp;
        this.power = 50;
        this.playerColour = colour;

        this.score = score;
        this.player = player;

        this.parachuteCount = 3;

        this.alive = true;

        this.maxHeights = maxHeights;    

        this.hpBar = new HealthBar(hp, 150, 20, maxhp); // Making new Bars for displaying stats
        this.powerBar = new PowerBar(power, 150, 20, hp);
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Changes the tank's current Y position.
     * @param n new Y position.
     */
    public void changeY(int n) {
        this.y = n;
    }

    /**
     * Changes the tank's current X position.
     * @param n new X position.
     */
    public void changeX(int n) {
        this.x = n;
    }

    /**
     * Uses the tanks fuel by a set amount.
     * @param n the fuel amount to be consumed.
     */
    public void useFuel(int n) {
        this.fuelLeft -= n;
    }

    /**
     * Fuel goes back to 250
     */
    public void resetFuel() {
        this.fuelLeft = 250;
    }

    /**
     * Returns the amount of fuel left a tank has.
     * @return the fuel amount of tank.
     */
    public int getFuel() {
        return fuelLeft;
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Returns the tank's ability to move based on criteria's such as its fuel, life status, and falling state.
     * @return the tank's ability to move.
     */
    public boolean canMove() {
        return this.getFuel() > 0 && this.isAlive() && !this.isFalling;
    }

    /**
     * Checks if tank can move and move the tank accordingly.
     * @param FPS the FPS of the game.
     */
    public void move(int FPS) {
        int moveDistance = moveSpeed / FPS;

        if (moveLeft == true && canMove()) {
            if (this.x - moveDistance < 0) {
                this.useFuel(x);; //if movement is bigger than possible move we just move by the remaining amount.
                this.x = 0;
            }
            else {
                this.x -= moveDistance;
                this.useFuel((int)moveDistance);
            }
            this.changeY(maxHeights[x]);
            this.turret.updatePosition(x, maxHeights[x]);
        }

        if (moveRight == true && canMove()) {
            if (this.x + moveDistance > 863) {
                this.useFuel(863 - x);
                this.x = 863;
            }
            else {
                this.x += moveDistance;
                this.useFuel((int)moveDistance);
            }
            this.changeY(maxHeights[x]);
            this.turret.updatePosition(x, maxHeights[x]);
        }
        checkVoid(); // checking if player moves into the void.
    }

    /**
     * Enables movement towards the left if possible.
     */
    public void moveLeft(){
        if (this.canMove()) {
            this.moveLeft = true;
        }
        else {
            this.moveLeft = false;
        }
    }

    /**
     * Enables movement towards the right if possible.
     */
    public void moveRight() {
        if (this.canMove()) {
            this.moveRight = true;
        }
        else {
            this.moveRight = false;
        }
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Makes the tank go into a falling state if it's position is above the maximum height of surface.
     * @param FPS the FPS of the game.
     * @param app to display the parachute.
     * @param parachute the parachute image.
     */
    public void fall(int FPS, PApplet app, PImage parachute) {
        this.isFalling = true;

        if (parachuteCount > 0) {
            this.y += fallSpeed/(2*FPS);
            app.image(parachute, x - 32/2, y - 32 - height, 32, 32); // loading image
        }
        else {
            this.y += fallSpeed/FPS;
        }

        if (this.y + 1 >= maxHeights[x]) {
            this.y = maxHeights[x];
            this.isFalling = false; // landed on surface
            if (parachuteCount > 0) {
                this.parachuteCount -= 1; // used parachute
                System.out.println(parachuteCount);
            }
        }
        this.turret.updatePosition(x, y);

        this.checkVoid(); // checking if fell into the abyss
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Returns the tank's centre X position.
     * @return the centre X position.
     */
    public int getTankCentreX() {
        return x;
    }

    /**
     * Returns the tank's centre Y position.
     * @return the centre Y position.
     */
    public int getTankCentreY() {
        return y + 1;
    }

    /**
     * Sets the tanks X centre position and updates the turret's location
     * @param x the new X position.
     */
    public void setTankCentreX(int x) {
        this.x = x;
        this.turret.updatePosition(this.x, this.y);
    }

    /**
     * Sets the tanks Y centre position and updates the turret's location
     * @param y the new Y position
     */
    public void setTankCentreY(int y) {
        this.y = y;
        this.turret.updatePosition(this.x, this.y);
    }

    /**
     * Updates the tanks new surface path.
     * @param terrain the max height of terrain.
     */
    public void setTankPath(int[] terrain) {
        this.maxHeights = terrain;
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * updates the tanks Health Bar.
     */
    public void updateHPBar() {
        this.hpBar.updateBar(this.hp, this.maxhp);
    }

    /**
     * Returns the tanks's current health bar.
     * @return the health bar of the tank.
     */
    public Bar getHealthBar() {
        return this.hpBar;
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Changes the power of the tank if it can be done. 
     * @param FPS the FPS of the game.
     */
    public void changePower(int FPS) {
        if (increasePower) {
            this.power += 36/FPS;

            if (power > hp) {
                this.power = hp; // cannot be exceed hp.
            }
        }

        if (decreasePower) {
            this.power -= 36/FPS;

            if (power < 0 || power > hp) {
                this.power = 0; // cannot be below or exceed hp.
            }
        }
        this.updatePowerBar(); //updates power bar display for power
    }

    /**
     * Updates the tank's power bar.
     */
    public void updatePowerBar() {
        this.powerBar.updateBar(this.power, this.hp);
    }

    /**
     * Returns the tank's current power bar
     * @return the power bar of the tank.
     */
    public Bar getPowerBar() {
        return this.powerBar;
    }

    /**
     * Returns the current power of the tank.
     * @return the power of the tank.
     */
    public int getPower() {
        return this.power;
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Returns the colour code in an array list of lenght of 3 to allow for easier use.
     * @return the colour of the tank.
     */
    public int[] getColour() {
        int[] colours = new int[3];

        if (playerColour != "random") {
            String[] cList = playerColour.split(","); // splitting the string
            if (cList.length == 3) {
                colours[0] = Integer.parseInt(cList[0]);
                colours[1] = Integer.parseInt(cList[1]);
                colours[2] = Integer.parseInt(cList[2]);
            }
        } 
        else {
            Random random = new Random();
            colours[0] = random.nextInt(256);
            colours[1] = random.nextInt(256); // randomising colours
            colours[2] = random.nextInt(256);
        }
        return colours;
    }

    /**
     * Returns the player's character identifier.
     * @return the character of the Player.
     */
    public char getPlayer() {
        return this.player;
    }

    /**
     * Returns the tank's current HP.
     * @return the HP of the tank.
     */
    public int getHP() {
        return this.hp;
    }

    /**
     * Returns the maximum HP possible for a tank.
     * @return the max HP of the tank.
     */
    public int getMaxHP() {
        return this.maxhp;
    }

    /**
     * Updates the tank's hp to sustain a certain amount of dmg
     * @param dmg the dmg to be taken by tank.
     */
    public void Damaged(int dmg) {
        this.hp -= dmg;
        if (hp == 0) {
            this.killed();
        }
        this.updateHPBar();
        this.power = hp/2;
        this.updatePowerBar();
    }

    /**
     * Sets the tank to be dead.
     */
    public void killed() {
        this.alive = false;
    }

    /**
     * Causes an explosion from the tank when even it dies or falls into the void.
     * @return an Explosion caused by destruction of tank.
     */
    public Explosion deathExplosion() {
        if (!this.alive && !this.hasExploded) {
            this.hasExploded = true;

            if (this.deathFromVoid) {
                this.deathFromVoid = false;
                return new Explosion(x, 639, 0, 30, this);
            }
            else
                return new Explosion(x, y, 0, 15, this);
        }
        return null;
    }

    /**
     * Checking whether the tank has fallen into the void.
     */
    public void checkVoid() {
        if (y >= 639 || y <= 0) {
            this.killed();
            this.deathFromVoid = true;
        }
    }

    /**
     * Returns whether the tank is alive or not.
     * @return the Tank's life status.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Resets the tank into better shape without resetting it's score.
     */
    public void revive() {
        this.alive = true;
        this.hp = maxhp;
        this.updateHPBar();
        this.power = hp/2;
        this.updatePowerBar();
        this.resetFuel();
        this.parachuteCount = 3;
        this.deathFromVoid = false;
        this.hasExploded = false;
        this.turret.updateRotation(0);
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Returns the turret that is attached to the tank.
     * @return the turret of this tank.
     */
    public Turret getTurret() {
        return turret;
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Increases the score of this tank by a set amount.
     * @param n the score to be added.
     */
    public void increaseScore(int n) {
        this.score += n;
    }

    /**
     * Sets the score of this tank to a completely new score.
     * @param n the new score to be displayed.
     */
    public void setScore(int n) {
        this.score = n;
    }

    /**
     * Returns the score which the tank has acuumulated.
     * @return the score of this tank.
     */
    public int getScore() {
        return score;
    }

    /**
     * Allows the tank to use score points to buy fuel.
     */
    public void buyFuel() {
        if (score >= 10) {
            score -= 10;
            fuelLeft += 200;
        }
    }

    /**
     * Allows the tank to use score points to buy Health.
     */
    public void buyHeal() {
        if (score >= 20) {
            hp += 20;
            if (hp > maxhp) {
                hp = maxhp;
            }
            score -= 20;
            updateHPBar();
        }
    }

// --------------------------------------------------------------------------------------------------------

    /**
     * Draws the icons for parachute and fuel of the tank in the top left.
     * @param app to display icons.
     * @param fuel the fuel image.
     * @param parachute the parachute image.
     */
    public void drawParachuteFuel(PApplet app, PImage fuel, PImage parachute) {
        app.noStroke();

        app.fill(0);
        app.textSize(16);
        app.text(("Player " + player + "'s turn"), 20, 26);

        app.image(fuel, 155, 4, 24, 24);
        app.fill(0);
        app.textSize(16);
        app.text(fuelLeft, 185, 26);

        app.image(parachute, 155, 30, 24, 24);
        app.fill(0);
        app.textSize(16);
        app.text(parachuteCount, 190, 26 + 24);

    }

    /**
     * Draws the tank out by colour.
     * @param app to display the 
     */
    public void draw(PApplet app) {
        app.noStroke();

        int[] colours = this.getColour();

        app.fill(colours[0], colours[1], colours[2]);

        app.rect(x - width/2, y - height, width, height);

        turret.display(app);
    }

}