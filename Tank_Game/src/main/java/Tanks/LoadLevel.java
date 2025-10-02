package Tanks;

import processing.data.JSONArray;
import processing.data.JSONObject;

public class LoadLevel {

    private Config config;
    private JSONArray levels;
    private JSONObject playerColours;
    private JSONObject level;


    /**
     * Constructor used to get a load the current level that should appear from Config Object.
     * @param config Configuration to get level assets.
     * @param currentLevel The level to be loaded.
     */
    public LoadLevel(Config config, int currentLevel) {
        this.config = config;
        this.levels = config.getLevels();
        this.playerColours = config.getPlayer();
        this.level = levels.getJSONObject(currentLevel);
    }

    /**
     * Returns the String of the level file.
     * @return level file for the current level's layout.
     */
    public String getLevel() {
        String layout = level.getString("layout");
        return layout;
    }

    /**
     * Returns the colour of the ground and terrain.
     * @return the colour which should appear on the terrain.
     */
    public String getForegroundColour() {
        String foregroundColour = level.getString("foreground-colour");
        return foregroundColour;
    }

    /**
     * Returns the String of the background file
     * @return the String format of the background to be loaded.
     */
    public String getGameBackground() {
        String background = level.getString("background");
        return background;
    }

    /**
     * Returns the String of tree file that should be placed on the terrain after the map is generated.
     * @return the String format of the tree to be loaded.
     */
    public String getTree() {
        String tree = level.getString("trees");
        return tree;
    }

    /**
     * Returns a JSON object to be processed further later for the player's colours and characters.
     * @return the JSON object of players to be loaded.
     */
    public JSONObject getPlayerColours() {
        return playerColours;
    }

}