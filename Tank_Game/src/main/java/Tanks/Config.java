package Tanks;

import netscape.javascript.JSObject;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Config extends PApplet {

    private JSONObject json;

    /**
     * Constructor used to get a Config Object. This class is redundant.
     * @param config JSON that needs to be processed.
     */
    public Config(JSONObject config) {
        this.json = config;
    }

    /**
     * Returns the array of levels.
     * @return returns the JSON array of levels
     */
    public JSONArray getLevels() {
        return json.getJSONArray("levels");
    }

    /**
     * Returns the array of player colours.
     * @return returns the possible colours of players.
     */
    public JSONObject getPlayer() {
        return json.getJSONObject("player_colours");
    }

}