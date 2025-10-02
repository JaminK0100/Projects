package Tanks;

import processing.core.PImage;

/**
 * Redundant.
 * Resources are loaded in setup.
 */
public class ResourceManager {

    private PImage resources;

    /**
     * Constructor used to store images as ResourceManager Objects.
     * @param resource the image to be loaded.
     */
    public ResourceManager(PImage resource) {
        this.resources = resource;
    }

    /**
     * Returns the resource stored in this class
     * @return the image to be loaded.
     */
    public PImage getResource() {
        return resources;
    }
}

