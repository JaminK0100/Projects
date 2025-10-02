package Tanks;

import java.util.*;
import java.io.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Turret {

    private int tankCentreX;
    private int tankCentreY;

    public boolean rotateLeft;
    public boolean rotateRight;

    private int bulletExitX;
    private int bulletExitY;

    private double rotation;
    private double rotationSpeed;

    private Tank tank;

    /**
     * Constructor used to attach turret on top of a tank from the tank centre.
     * @param x centre X position of tank.
     * @param y centre Y position of tank.
     * @param tank the tank with this turret.
     */
    public Turret(int x, int y, Tank tank) {
        this.tankCentreX = x;
        this.tankCentreY = y;

        this.rotationSpeed = 3;
        this.rotation = 0;

        this.tank = tank;
    }

    /**
     * Rotates the turret if the user has inputted a command.
     * @param FPS the FPS of the game.
     */
    public void rotate(int FPS) {
        double rotateAngle = rotationSpeed / FPS;

        if (rotateLeft == true) {
            this.rotation -= rotateAngle;
        }

        if (rotateRight == true) {
            this.rotation += rotateAngle;
        }
    }

    /**
     * Makes rotation towards left side possible.
     */
    public void rotateLeft() {
        this.rotateLeft = true;
    }

    /**
     * Makes rotation towards right side possible.
     */
    public void rotateRight() {
        this.rotateRight = true;
    }

    /**
     * Updates the turret's centre location. Used for follwing tank movements.
     * @param x new X position of turret.
     * @param y new Y position of turret.
     */
    public void updatePosition(int x, int y) {
        this.tankCentreX = x;
        this.tankCentreY = y;
    }

    /**
     * Updates the rotation which the turret as right now.
     * @param n the rotation angle in radians.
     */
    public void updateRotation(int n) {
        this.rotation = n;
    }

    /**
     * Returns the X position of the tip of the turret's head.
     * @return the X position of turret head.
     */
    public int getTurretHeadX() {
        bulletExitX =  (int) (Math.sin(rotation) * 15 + tankCentreX);
        return bulletExitX;
    } 

    /**
     * Returns the Y position of the tip of the turret's head.
     * @return the Y position of turret head.
     */
    public int getTurretHeadY() {
        bulletExitY = (int) (Math.cos(rotation) * -15 + (tankCentreY - 10));
        return bulletExitY;
    }

    /**
     * Shoots a bullet from this tank's turret at an angle with power and wind speed.
     * @param power the power which the tank will be shooting at.
     * @param windSpeed the wind speed of the map.
     * @return the bullet that is shot from this tank.
     */
    public Bullet shoot(int power, int windSpeed) {
        return new Bullet(getTurretHeadX(), getTurretHeadY(), rotation, power, windSpeed, tank);
    }

    /**
     * Draws the turret out.
     * @param app to display the turret.
     */
    public void display(PApplet app) {
        app.noStroke();

        app.pushMatrix();
        int x = tankCentreX;
        int y = tankCentreY;
        app.translate(x, y - 10); // move turret down a bit to match tank height

        app.rotate((float)rotation); // rotates the turret

        app.fill(12);
        app.rect(-2, -15, 4, 15);
        app.popMatrix();
    }
}