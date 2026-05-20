// src/main/java/com/javasim/App.java
package com.javasim;

import com.javasim.controller.SimulationController;
import com.javasim.game.LevelManager;
import com.javasim.model.CircuitGraph;
import com.javasim.view.InspectorView;
import com.javasim.view.ToolboxView;
import com.javasim.view.WorkspaceView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // --- 1. INITIALIZE GAME LOGIC ---
        CircuitGraph graph = new CircuitGraph();
        LevelManager levelManager = new LevelManager();
        levelManager.LoadLevel(1); // Load "First Light" puzzle

        // --- 2. ASSEMBLE UI MODULES ---
        InspectorView inspector = new InspectorView();
        WorkspaceView workspace = new WorkspaceView(graph, inspector::ShowInspector);
        ToolboxView toolbox = new ToolboxView(graph, workspace, levelManager);

        // Add Level HUD to Workspace
        VBox hud = new VBox(5);
        hud.setLayoutX(20);
        hud.setLayoutY(20);
        Text levelTitle = new Text("Level 1: " + levelManager.GetCurrentLevel().GetTitle());
        levelTitle.setFont(new Font("Arial", 18));
        Text levelDesc = new Text(levelManager.GetCurrentLevel().GetDescription());
        hud.getChildren().addAll(levelTitle, levelDesc);
        workspace.getChildren().add(hud);

        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(toolbox, workspace, inspector);

        // --- 3. START GAME LOOP ---
        // Replace the SimulationController section in App.java with this:

        SimulationController controller = new SimulationController(graph) {
            @Override
            public void UpdateView() {
                workspace.RefreshAllViews(); 
                
                // Check if puzzle is solved on this frame
                if (levelManager.CheckWinCondition(graph)) {
                    // Pause the simulation so it doesn't keep triggering
                    this.StopSimulation(); 
                    
                    Platform.runLater(() -> {
                        // Create a "Next Level" Menu Overlay
                        VBox winMenu = new VBox(20);
                        winMenu.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 40; -fx-border-color: green; -fx-border-width: 5;");
                        winMenu.setLayoutX(250);
                        winMenu.setLayoutY(200);

                        Text winText = new Text("LEVEL COMPLETE!");
                        winText.setFont(new Font("Arial", 36));
                        winText.setStyle("-fx-fill: green; -fx-font-weight: bold;");

                        Button nextLevelBtn = new Button("Play Next Level");
                        nextLevelBtn.setStyle("-fx-font-size: 18px; -fx-padding: 10 20;");
                        
                        nextLevelBtn.setOnAction(e -> {
                            // 1. Clean up
                            graph.ClearGraph();
                            workspace.ClearWorkspace();
                            
                            // 2. Load next level
                            int nextId = levelManager.GetCurrentLevel().GetLevelId() + 1; // Assuming you add GetLevelId() to PuzzleLevel
                            levelManager.LoadLevel(nextId);
                            
                            // 3. Rebuild HUD
                            Text levelTitle = new Text("Level " + nextId + ": " + levelManager.GetCurrentLevel().GetTitle());
                            levelTitle.setFont(new Font("Arial", 18));
                            Text levelDesc = new Text(levelManager.GetCurrentLevel().GetDescription());
                            VBox hud = new VBox(5, levelTitle, levelDesc);
                            hud.setLayoutX(20);
                            hud.setLayoutY(20);
                            workspace.getChildren().add(hud);

                            // 4. Resume Game
                            this.StartSimulation();
                        });

                        winMenu.getChildren().addAll(winText, nextLevelBtn);
                        workspace.getChildren().add(winMenu);
                    });
                }
            }
        };

        Scene scene = new Scene(mainLayout);
        stage.setTitle("CircuitPuzzle - Level 1");
        stage.setScene(scene);
        stage.show();

        controller.StartSimulation();
    }

    public static void main(String[] args) {
        launch();
    }
}