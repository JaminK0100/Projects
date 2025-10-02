package Tanks;

import processing.core.PApplet;

public class PowerBar extends Bar {

    private int[] colours;


    public PowerBar(int hp, int width, int height, int max) {
        super(hp, width, height);
        this.maxhp = max;
        this.colours = new int[3];
    }

    public void updateBar(int hp, int maxhp) {
        this.hp = hp;
        this.maxhp = maxhp;
    }

    public void setColour(int[] colours) {
        this.colours = colours;
    }

    @Override
    public void display(PApplet app, int x, int y, Tank tank) {
        app.stroke(128,128,128);
        app.strokeWeight(3);

        float powerPercent = (float)this.hp/this.maxhp;

        float maxWidth = this.width * this.maxhp/100;
        float hpWidth = maxWidth * powerPercent;

        int[] colour = tank.getColour();

        if (colour.length != 3)
            colour = new int[3];

        int c1 = colour[0];
        int c2 = colour[1];
        int c3 = colour[2];

        app.fill(c1,c2,c3);
        app.rect(x, y, hpWidth, this.height);

        app.fill(0);
        app.textSize(16);
        app.text("Power:", x - 60, y + 16 + 24);

        app.fill(0);
        app.textSize(16);
        app.text(hp, x, y + 16 + 24);

        app.stroke(255,0,0);
        app.strokeWeight(1);
        app.line(x + hpWidth, y - 2, x + hpWidth, y + 22);
    }

}
