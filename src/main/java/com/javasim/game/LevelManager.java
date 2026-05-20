// src/main/java/com/javasim/game/LevelManager.java
package com.javasim.game;

import java.util.HashMap;
import java.util.Map;

import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;

public class LevelManager {
    private PuzzleLevel currentLevel;
    private boolean isLevelCompleted = false;

    public void LoadLevel(int levelId) {
        this.isLevelCompleted = false;
        
        if (levelId == 1) {
            Map<String, Integer> inventory = new HashMap<>();
            inventory.put("VoltageSource", 1); // Only 1 Battery allowed
            inventory.put("Resistor", 1);      // Only 1 Resistor allowed
            inventory.put("Switch", 1);        // Only 1 Switch allowed
            inventory.put("Bulb", 1);          // Only 1 Bulb allowed

            currentLevel = new PuzzleLevel(
                1, 
                "First Light", 
                "Wire the components together to turn the light bulb on.", 
                inventory, 
                true
            );
        }
        else if (levelId == 2) {
            Map<String, Integer> inventory = new HashMap<>();
            inventory.put("VoltageSource", 1);
            inventory.put("Resistor", 2); // 2 Resistors!
            inventory.put("Switch", 1);
            inventory.put("Bulb", 1);

            currentLevel = new PuzzleLevel(
                2, 
                "Voltage Divider", 
                "The battery is too strong! Use resistors to drop the voltage so the bulb doesn't pop.", 
                inventory, 
                true
            );
        }
        System.out.println("[GAME] Loaded Level: " + currentLevel.GetTitle());
    }

    public PuzzleLevel GetCurrentLevel() { 
        return currentLevel; 
    }

    public boolean CheckWinCondition(CircuitGraph graph) {
        if (currentLevel == null || isLevelCompleted) return false;

        // Win Condition: Is the bulb lit?
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