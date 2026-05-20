// src/main/java/com/javasim/game/LevelManager.java
package com.javasim.game;

import com.google.gson.Gson;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;
import com.javasim.model.Bulb;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
            // Locate the file in the resources/levels folder
            String fileName = "/levels/level" + levelId + ".json";
            InputStream inputStream = getClass().getResourceAsStream(fileName);

            if (inputStream == null) {
                System.out.println("[GAME] Could not find " + fileName + ". You beat the game!");
                currentLevel = null;
                return;
            }

            // Read the JSON and magically convert it into our Java object
            Reader reader = new InputStreamReader(inputStream);
            currentLevel = gson.fromJson(reader, PuzzleLevel.class);
            
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