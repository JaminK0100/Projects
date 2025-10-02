package Tanks;

import java.util.*;
import processing.core.PApplet;

public class Explosion {

    private double explosionX;
    private double explosionY;
    private int r;
    private int maxRadius;
    private boolean damageApplied;
    private Tank originTank;

    /**
     * Constructor used to create an explosion at a certain position and a certain size that is caused by some tank.
     * @param x the x position of explosion.
     * @param y the y position of explosion.
     * @param r the beginning radius of explosion.
     * @param max the max radius of explosion.
     * @param tank the tank that caused the explosion.
     */
    public Explosion(double x, double y, int r, int max, Tank tank) {
        this.explosionX = x;
        this.explosionY = y;
        this.r = r;
        this.maxRadius = max;
        this.damageApplied = false;
        this.originTank = tank;
    }

    /**
     * Returns whether the explosion has finished or not as it continues to expand until max radius.
     * @return whether explosion is completed or not.
     */
    public boolean update() {
        r += 6;

        if (r >= maxRadius) {
            return true;
        }
        return false;
    }

    /**
     * Returns the distance of a tank from an explosion after considering the explosion's impact and tank position.
     * @param tank the tank that is alive and could be damaged by an explosion.
     * @return the distance the tank is away from the explosion centre.
     */
    public double distanceWithinBlast(Tank tank) {
        double distance = Math.sqrt(Math.pow(tank.getTankCentreX() - explosionX, 2) + Math.pow(tank.getTankCentreY() - explosionY, 2));
        return distance;
    }

    /**
     * Returns the distance of the terrain from an explosion after considering the explosion's impact and terrain foundation.
     * @param terrainX X position of terrain pixel.
     * @param terrainY Y position of terrain pixel.
     * @return the distance the terrain pixel is away from the explosion centre.
     */
    public double distanceWithinBlast(int terrainX, int terrainY) {
        double distance = Math.sqrt(Math.pow(terrainX - explosionX, 2) + Math.pow(terrainY - explosionY, 2));
        return distance;
    }

    /**
     * Dealing damage to tanks depending on the distance they are away from the explosion centre.
     * @param tanks the list of Tanks that are in the game.
     */
    public void damageTanks(ArrayList<Tank> tanks) {
        if (!damageApplied) {// so the explosion only causes damage once
            for (Tank tank: tanks) {
                double distance = distanceWithinBlast(tank);
                if (distance <= maxRadius) {
                    double damageScale = distance/maxRadius;
                    int damageDealt = (int) (60 * (1 - damageScale));
                    int thisTankHP = tank.getHP();

                    if (damageDealt > thisTankHP) {
                        damageDealt = thisTankHP; // damage dealt should not exceed tank health
                    }
                    tank.Damaged(damageDealt);

                    if (tank != originTank) {
                        originTank.increaseScore(damageDealt); // give points to tank that caused damage
                    }
                }

                if (explosionY > tank.getTankCentreY()) {

                }
            }
        }
        damageApplied = true;
    }

    /**
     * Destorying the terrain depending on the terrain's distance from explosion centre.
     * @param pixelLayout the terrain layout to be altered due to explosion.
     */
    public void destroyTerrain (char[][] pixelLayout) {
        for (int row = 0; row < pixelLayout.length; row++) {
            for (int col = 0; col < pixelLayout[row].length; col++) {
                if (pixelLayout[row][col] == 'X') {
                    double distance = distanceWithinBlast(col, row);
                    if (distance <= maxRadius) {
                        pixelLayout[row][col] = ' '; // Destroy terrain
                    }
                }
            }
        }
    }


    /**
     * Drawing the explosion out.
     * @param app to display the explosion.
     */
    public void boom(PApplet app) {
        app.noStroke();
        
        app.fill(255,0,0); // Large Red blast
        app.ellipse((int) (explosionX), (int) (explosionY), 2 * r, 2 * r);

        int mid_r = (int) (r * 0.5);
        app.fill(255,165,0); // Medium Orange
        app.ellipse((int) (explosionX), (int) (explosionY), 2 * mid_r, 2 * mid_r);

        int inner_r = (int) (r * 0.2);
        app.fill(255,255,0); // Small yellow
        app.ellipse((int) (explosionX), (int) (explosionY), 2 * inner_r, 2 * inner_r);

    }
}

