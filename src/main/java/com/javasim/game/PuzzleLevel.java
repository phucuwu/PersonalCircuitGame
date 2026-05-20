// src/main/java/com/javasim/game/PuzzleLevel.java
package com.javasim.game;

import java.util.Map;

public class PuzzleLevel {
    private int levelId;
    private String title;
    private String description;
    private Map<String, Integer> componentInventory;
    private boolean requiresBulbLit;

    public PuzzleLevel(int levelId, String title, String description, Map<String, Integer> inventory, boolean requiresBulbLit) {
        this.levelId = levelId;
        this.title = title;
        this.description = description;
        this.componentInventory = inventory;
        this.requiresBulbLit = requiresBulbLit;
    }

    public String GetTitle() { return title; }
    public String GetDescription() { return description; }
    public boolean RequiresBulbLit() { return requiresBulbLit; }
    
    // Returns how many of a specific component the player is allowed to use
    public int GetAvailableCount(String componentType) {
        return componentInventory.getOrDefault(componentType, 0);
    }
}