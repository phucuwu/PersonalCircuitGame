// src/main/java/com/javasim/App.java
package com.javasim;

import com.javasim.controller.SimulationController;
import com.javasim.model.CircuitGraph;
import com.javasim.view.InspectorView;
import com.javasim.view.ToolboxView;
import com.javasim.view.WorkspaceView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        HBox mainLayout = new HBox();
        CircuitGraph graph = new CircuitGraph();

        // 1. Initialize Inspector
        InspectorView inspector = new InspectorView();

        // 2. Initialize Workspace (Links to the Inspector via method reference)
        WorkspaceView workspace = new WorkspaceView(graph, inspector::ShowInspector);

        // 3. Initialize Toolbox (Links to the Workspace and Graph)
        ToolboxView toolbox = new ToolboxView(graph, workspace);

        // 4. Assemble the UI Layout
        mainLayout.getChildren().addAll(toolbox, workspace, inspector);

        // 5. Start the Physics Engine Loop
        SimulationController controller = new SimulationController(graph) {
            @Override
            public void UpdateView() {
                workspace.RefreshAllViews(); 
            }
        };

        Scene scene = new Scene(mainLayout);
        stage.setTitle("JavaSim - Circuit Sandbox");
        stage.setScene(scene);
        stage.show();

        controller.StartSimulation();
    }

    public static void main(String[] args) {
        launch();
    }
}