package Tanks;

import java.util.*;

import java.io.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet {

    private double bulletX;
    private double bulletY;

    private int d; //diameter
    private double speed;

    private double initialHorizontalSpeed;
    private double intialVerticalSpeed;
    private double rotation;

    private int windSpeed;
    private final double gravityAcceleration = 3.6 * 30;
    
    private Tank tank;

    /**
     * Constructor used to create a bullet at the turret's exit at a certain speed based on power and rotation that is shot by some tank.
     * @param x the initial bullet spawn X position.
     * @param y the initial bullet spawn Y position.
     * @param rotation the rotation angle in which bullet will be shooting at.
     * @param power the power of bullet (speed).
     * @param windSpeed the windSpeed of the map.
     * @param tank the tank that shot the bullet.
     */
    public Bullet (int x, int y, double rotation, int power, int windSpeed, Tank tank) {
        this.bulletX = x;
        this.bulletY = y;
        this.rotation = rotation;
        this.d = 5;

        this.speed = power * 4.8 + 60;
        this.initialHorizontalSpeed = speed * Math.sin(rotation); // getting the speed of both axis
        this.intialVerticalSpeed = Math.ceil(speed * Math.cos(rotation) / 1.0);
        this.windSpeed = windSpeed * 30;

        this.tank = tank;
    }

    /**
     * Updates the position of the bullet to a new coordinate.
     * @param x the new X position of bullet.
     * @param y the new Y position of bullet.
     */
    public void updatePosition(int x, int y) {
        this.bulletX = x;
        this.bulletY = y;
    }

    /**
     * Updates the rotation angle of where the bullet will be shooting towards.
     * @param rotation the new rotation angle.
     */
    public void updateRotation(double rotation) {
        this.rotation = rotation;
        this.initialHorizontalSpeed = speed * Math.sin(rotation);
        this.intialVerticalSpeed = Math.ceil(speed * Math.cos(rotation) / 1.0);
    }

    /**
     * Returns the bullet's current X position.
     * @return the bullet's X position.
     */
    public double getBulletX() {
        return this.bulletX;
    }

    /**
     * Returns the bullet's current Y position.
     * @return the bullet's Y position.
     */
    public double getBulletY() {
        return this.bulletY;
    }

    /**
     * Returns the bullet's current horizontal speed.
     * @return the bullet's horizontal speed.
     */
    public double getInitialHorizontalSpeed() {
        return initialHorizontalSpeed;
    }

    /**
     * updates the wind speed that is affecting this bullet.
     * @param n the new wind speed.
     */
    public void updateWindSpeed(int n) {
        this.windSpeed = n;
    }

    /**
     * Moves the bullets based on its speed and acceleration caused by wind and gravity.
     * @param FPS the FPS of the game.
     */
    public void move(int FPS) {
        this.bulletX += (initialHorizontalSpeed / (double)FPS);
        this.bulletY -= Math.ceil(intialVerticalSpeed / (double)FPS);

        this.initialHorizontalSpeed += 2 * windSpeed * 0.03;
        this.intialVerticalSpeed -= 2 * gravityAcceleration / FPS;
    }

    /**
     * This method causes an explosion where the bullet is.
     * @return an Explosion instance.
     */
    public Explosion explode(){
        return new Explosion(bulletX, bulletY, 0, 30, tank);
    }

    /**
     * Draws the bullet out.
     * @param app to display the bullet.
     */
    public void render(PApplet app) {
        app.noStroke();
        app.pushMatrix();
        app.translate((int)bulletX, (int)bulletY);
        int[] colour = this.tank.getColour();
        app.fill(colour[0],colour[1],colour[2]);
        app.ellipse(0, 0, d, d);
        app.fill(0); // Drawing two circles for bullet shell.
        app.ellipse(0,0,1,1);
        app.popMatrix();
    }

}