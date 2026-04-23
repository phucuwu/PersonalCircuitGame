package com.javasim;

import java.util.ArrayList;
import java.util.List;

import com.javasim.controller.SimulationController;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Resistor;
import com.javasim.model.VoltageSource;
import com.javasim.view.ComponentView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    private List<ComponentView> viewList = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        // 1. Setup the Layout (The "Board")
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        // 2. Setup the Model (The "Physics")
        // For a test, let's make a simple circuit: 1 Battery (10V) and 1 Resistor (100 Ohms)
        // Node 1 is between them, Node 0 is Ground.
        CircuitGraph graph = new CircuitGraph();
        
        VoltageSource battery = new VoltageSource("V1", 10.0, 0);
        battery.SetNodeIds(new int[]{1, 0}); // Connected to Node 1 and Ground
        
        Resistor resistor = new Resistor("R1", 100.0);
        resistor.SetNodeIds(new int[]{1, 0}); // Also connected to Node 1 and Ground
        
        graph.AddComponent(battery);
        graph.AddComponent(resistor);

        // 3. Setup the Views (The "Visuals")
        ComponentView batteryView = new ComponentView(battery);
        batteryView.setLayoutX(100);
        batteryView.setLayoutY(100);
        
        ComponentView resistorView = new ComponentView(resistor);
        resistorView.setLayoutX(300);
        resistorView.setLayoutY(100);
        
        viewList.add(batteryView);
        viewList.add(resistorView);
        root.getChildren().addAll(batteryView, resistorView);

        // 4. Setup the Controller (The "Brain")
        SimulationController controller = new SimulationController(graph) {
            @Override
            public void UpdateView() {
                // Every frame, we tell every view to refresh its labels/colors
                for (ComponentView v : viewList) {
                    v.Refresh();
                }
            }
        };

        // 5. Show Time
        Scene scene = new Scene(root);
        stage.setTitle("JavaSim - HUST IT Project");
        stage.setScene(scene);
        stage.show();

        // Start the simulation heartbeat
        controller.StartSimulation();
    }

    public static void main(String[] args) {
        launch();
    }
}