package com.javasim;

import java.util.ArrayList;
import java.util.List;

import com.javasim.controller.SimulationController;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Resistor;
import com.javasim.model.Switch;
import com.javasim.model.VoltageSource;
import com.javasim.view.ComponentView;
import com.javasim.view.SwitchView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

        // 1. Setup Model
        Switch mainSwitch = new Switch("SW1");
        graph.AddComponent(mainSwitch);

        // 2. Setup View
        SwitchView switchView = new SwitchView(mainSwitch);
        switchView.setLayoutX(200);
        switchView.setLayoutY(150);
        viewList.add(switchView);
        root.getChildren().add(switchView);

        // 3. Setup Interaction (Ensure this is called after adding to viewList)
        SetupWiringInteraction(root, viewList, graph);
        
        viewList.add(batteryView);
        viewList.add(resistorView);
        root.getChildren().addAll(batteryView, resistorView);

        SetupWiringInteraction(root, viewList, graph);


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

    // src/main/java/com/javasim/App.java

    private Circle selectedPin = null;
    private ComponentView selectedView = null;
    private int selectedPinIndex = -1;

    private void SetupWiringInteraction(Pane root, List<ComponentView> viewList, CircuitGraph graph) {
        for (ComponentView view : viewList) {
            // Setup for Pin 0 (Left)
            view.GetPin0().setOnMouseClicked(e -> handlePinClick(view, 0, root, graph));
        
            // Setup for Pin 1 (Right)
            view.GetPin1().setOnMouseClicked(e -> handlePinClick(view, 1, root, graph));
        }
    }

    private void handlePinClick(ComponentView view, int pinIndex, Pane root, CircuitGraph graph) {
        if (selectedPin == null) {
            // First pin clicked: Start the wire
            selectedView = view;
            selectedPinIndex = pinIndex;
            selectedPin = (pinIndex == 0) ? view.GetPin0() : view.GetPin1();
            selectedPin.setFill(Color.RED); // Highlight the start
        } else {
            // Second pin clicked: Finish the wire
            if (selectedView != view) { // Prevent connecting a component to itself
                // 1. Draw the visual wire
                javafx.scene.shape.Line wire = new javafx.scene.shape.Line();
            
                // Calculate global positions for the line
                wire.setStartX(selectedView.getLayoutX() + selectedPin.getTranslateX() + 30);
                wire.setStartY(selectedView.getLayoutY() + selectedPin.getTranslateY() + 20);
                wire.setEndX(view.getLayoutX() + ((pinIndex == 0) ? -30 : 30) + 30);
                wire.setEndY(view.getLayoutY() + 20);
            
                root.getChildren().add(wire);
                wire.toBack(); // Put wires behind components

                // 2. Connect in the Backend Logic
                graph.GetNodeManager().Connect(selectedView.GetModel(), selectedPinIndex, view.GetModel(), pinIndex);
            }   
        
        // Reset selection
        selectedPin.setFill(Color.BLACK);
        selectedPin = null;
        selectedView = null;
    }
}
}