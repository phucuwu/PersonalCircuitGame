// src/main/java/com/javasim/game/LevelManager.java
package com.javasim.game;

import com.google.gson.Gson;
import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;

public class LevelManager {
    private PuzzleLevel currentLevel;
    private boolean isLevelCompleted = false;
    private Gson gson;

    public LevelManager() {
        this.gson = new Gson();
    }

    public void LoadLevel(int levelId) {
        this.isLevelCompleted = false;
        
        try {
            String fileName = "/levels/level" + levelId + ".json";
            java.io.InputStream inputStream = getClass().getResourceAsStream(fileName);

            if (inputStream == null) {
                System.out.println("[GAME] Could not find " + fileName + ".");
                currentLevel = null;
                return;
            }

            java.io.Reader reader = new java.io.InputStreamReader(inputStream);
            currentLevel = gson.fromJson(reader, PuzzleLevel.class);
            
            // --- NEW SAFETY CHECK ---
            if (currentLevel == null) {
                System.err.println("[ERROR] " + fileName + " is empty or invalid!");
                return;
            }
            
            System.out.println("[GAME] Loaded Level " + levelId + ": " + currentLevel.GetTitle());

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load level: " + e.getMessage());
        }
    }

    public PuzzleLevel GetCurrentLevel() { 
        return currentLevel; 
    }

    public boolean CheckWinCondition(CircuitGraph graph) {
        if (currentLevel == null || isLevelCompleted) return false;

        if (currentLevel.RequiresBulbLit()) {
            for (Component c : graph.GetComponents()) {
                if (c instanceof Bulb && ((Bulb) c).IsLit()) {
                    isLevelCompleted = true;
                    System.out.println("[GAME] LEVEL COMPLETE!");
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean IsLevelCompleted() {
        return isLevelCompleted;
    }
}