// src/main/java/com/javasim/game/PuzzleLevel.java
package com.javasim.game;

import java.util.Map;

public class PuzzleLevel {
    private int levelId;
    private String title;
    private String description;
    private Map<String, Integer> componentInventory;
    private boolean requiresBulbLit;

    // Gson needs a default constructor to build the object
    public PuzzleLevel() {}

    public int GetLevelId() { return levelId; }
    public String GetTitle() { return title; }
    public String GetDescription() { return description; }
    public boolean RequiresBulbLit() { return requiresBulbLit; }
    
    public int GetAvailableCount(String componentType) {
        if (componentInventory == null) return 0;
        return componentInventory.getOrDefault(componentType, 0);
    }
}