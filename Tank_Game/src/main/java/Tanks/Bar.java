package Tanks;

import processing.core.PApplet;

public abstract class Bar {

    public int width;
    public int height;
    public int hp;
    public int maxhp;
    public int[] colour;

    public Bar(int hp, int width, int height) {
        this.hp = hp;
        this.maxhp = hp;
        this.width = width;
        this.height = height;
        this.colour = new int[3];
    }

    public abstract void updateBar(int hp, int maxhp);

    public abstract void setColour(int[] colour);

    public abstract void display(PApplet app, int x, int y, Tank tank);
}