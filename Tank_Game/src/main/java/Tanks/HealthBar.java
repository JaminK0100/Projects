package Tanks;

import processing.core.PApplet;

public class HealthBar extends Bar {
    
    private int[] colours;

    public HealthBar(int hp, int width, int height, int max) {
        super(hp, width, height);
        this.maxhp = max;
        this.colours = new int[3];
    }

    public void updateBar(int hp, int maxhp) {
        this.hp = hp;
        if (maxhp != 0) {
            this.maxhp = maxhp;
        }
    }

    public void setColour(int[] colours) {
        this.colours = colours;
    }

    @Override
    public void display(PApplet app, int x, int y, Tank tank) {
        app.stroke(0);
        app.strokeWeight(2);

        float healthPercent = (float)this.hp/this.maxhp;

        float noHpWidth = (1 - healthPercent) * this.width;
        float hpWidth = this.width - noHpWidth;

        int[] colour = tank.getColour();

        if (colour.length != 3)
            colour = new int[3];

        int c1 = colour[0];
        int c2 = colour[1];
        int c3 = colour[2];

        app.fill(255,255,255);
        app.rect(x + hpWidth, y, noHpWidth, this.height);

        app.fill(c1,c2,c3);
        app.rect(x, y, hpWidth, this.height);

        app.fill(0);
        app.textSize(16);
        app.text("Health:", x - 60, y + 16);

        app.fill(0);
        app.textSize(16);
        app.text(hp, x + width + 5, y + 16);
    }
}

