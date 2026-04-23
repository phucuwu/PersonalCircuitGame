package com.javasim;

import java.util.ArrayList;
import java.util.List;

import com.javasim.controller.SimulationController;
import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.NodeManager;
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
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class App extends Application {

    private List<ComponentView> viewList = new ArrayList<>();
    
    // Fields for the wiring tool
    private Circle selectedPin = null;
    private ComponentView selectedView = null;
    private int selectedPinIndex = -1;

    @Override
    public void start(Stage stage) {
        // 1. Setup the Layout
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        // 2. Setup the Model (Physics Engine)
        CircuitGraph graph = new CircuitGraph();
        NodeManager nm = graph.GetNodeManager();

        VoltageSource battery = new VoltageSource("V1", 12.0, 0);
        Switch mainSwitch = new Switch("SW1");
        Resistor resistor = new Resistor("R1", 10.0);
        Bulb bulb = new Bulb("B1", 10.0, 5.0);

        graph.AddComponent(battery);
        graph.AddComponent(mainSwitch);
        graph.AddComponent(resistor);
        graph.AddComponent(bulb);

        // 3. Setup the Views
        ComponentView batteryView = new ComponentView(battery);
        batteryView.setLayoutX(50); batteryView.setLayoutY(150);

        SwitchView switchView = new SwitchView(mainSwitch);
        switchView.setLayoutX(200); switchView.setLayoutY(150);

        ComponentView resistorView = new ComponentView(resistor);
        resistorView.setLayoutX(200); resistorView.setLayoutY(50);

        ComponentView bulbView = new ComponentView(bulb);
        bulbView.setLayoutX(50); bulbView.setLayoutY(50);

        // Synchronize our lists
        viewList.clear();
        viewList.addAll(List.of(batteryView, switchView, resistorView, bulbView));

        root.getChildren().clear(); 
        root.getChildren().addAll(batteryView, switchView, resistorView, bulbView);

        nm.SetAsGround(battery, 1);

        // 5. Setup Controller
        SimulationController controller = new SimulationController(graph) {
            @Override
            public void UpdateView() {
                for (ComponentView v : viewList) {
                    v.Refresh();
                }
            }
        };

        // Initialize the interactive wiring tool
        SetupWiringInteraction(root, viewList, graph);

        Scene scene = new Scene(root);
        stage.setTitle("JavaSim - Series Circuit Test");
        stage.setScene(scene);
        stage.show();

        controller.StartSimulation();
    }

    // --- WIRING TOOL HELPER METHODS ---

    private void SetupWiringInteraction(Pane root, List<ComponentView> viewList, CircuitGraph graph) {
        for (ComponentView view : viewList) {
            view.GetPin0().setOnMouseClicked(e -> handlePinClick(view, 0, root, graph));
            view.GetPin1().setOnMouseClicked(e -> handlePinClick(view, 1, root, graph));
        }
    }
    private void handlePinClick(ComponentView view, int pinIndex, Pane root, CircuitGraph graph) {
    if (selectedPin == null) {
        selectedView = view;
        selectedPinIndex = pinIndex;
        selectedPin = (pinIndex == 0) ? view.GetPin0() : view.GetPin1();
        selectedPin.setFill(Color.RED);
    } else {
        if (selectedView != view) {
            Line wire = new Line();
            wire.setStrokeWidth(2);

            // BIND start of the wire to the first component
            wire.startXProperty().bind(selectedView.layoutXProperty().add(selectedPinIndex == 0 ? 0 : 60));
            wire.startYProperty().bind(selectedView.layoutYProperty().add(20));

            // BIND end of the wire to the second component
            wire.endXProperty().bind(view.layoutXProperty().add(pinIndex == 0 ? 0 : 60));
            wire.endYProperty().bind(view.layoutYProperty().add(20));

            root.getChildren().add(wire);
            wire.toBack(); 

            // Log and connect
            graph.GetNodeManager().Connect(selectedView.GetModel(), selectedPinIndex, view.GetModel(), pinIndex);
        }
        
        selectedPin.setFill(Color.BLACK);
        selectedPin = null;
        selectedView = null;
    }
}

    public static void main(String[] args) {
        launch();
    }
}