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
        SimulationController controller = new SimulationController(graph) {
            @Override
            public void UpdateView() {
                workspace.RefreshAllViews(); 
                
                // Check if puzzle is solved on this frame
                if (levelManager.CheckWinCondition(graph)) {
                    Platform.runLater(() -> {
                        Text winText = new Text("LEVEL COMPLETE!");
                        winText.setFont(new Font("Arial", 48));
                        winText.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                        winText.setLayoutX(200);
                        winText.setLayoutY(300);
                        workspace.getChildren().add(winText);
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