package com.javasim;

import java.util.ArrayList;
import java.util.List;

import com.javasim.controller.SimulationController;
import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;
import com.javasim.model.NodeManager;
import com.javasim.model.Resistor;
import com.javasim.model.Switch;
import com.javasim.model.VoltageSource;
import com.javasim.view.ComponentView;
import com.javasim.view.SwitchView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
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

        HBox mainLayout = new HBox();
        VBox toolbox = new VBox(10); // 10px spacing
        toolbox.setPrefWidth(150);
        toolbox.setStyle("-fx-background-color: #eeeeee; -fx-padding: 10; -fx-border-color: #cccccc;");

        Pane drawingPane = new Pane();
        drawingPane.setPrefSize(800, 600);
        
        // Add Buttons
        Button addResistor = new Button("Resistor (10Ω)");
        addResistor.setOnAction(e -> AddNewComponent(new Resistor("R" + (viewList.size()+1), 10.0), 50, 50, drawingPane, graph));

        Button addBattery = new Button("Battery (12V)");
        addBattery.setOnAction(e -> AddNewComponent(new VoltageSource("V" + (viewList.size()+1), 12.0, 0), 50, 50, drawingPane, graph));

        Button addSwitch = new Button("Switch");
        addSwitch.setOnAction(e -> AddNewComponent(new Switch("SW" + (viewList.size()+1)), 50, 50, drawingPane, graph));

        toolbox.getChildren().addAll(new Text("Toolbox"), addResistor, addBattery, addSwitch);
        mainLayout.getChildren().addAll(toolbox, drawingPane);

        Scene view = new Scene(mainLayout);

   

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
    private void AddNewComponent(Component comp, double x, double y, Pane root, CircuitGraph graph) {
    // 1. Add to Physics Model
    graph.AddComponent(comp);

    // 2. Create the correct View
    ComponentView view;
    if (comp instanceof Switch) {
        view = new SwitchView((Switch) comp);
    } else {
        view = new ComponentView(comp);
    }

    // 3. Setup Position and UI
    view.setLayoutX(x);
    view.setLayoutY(y);
    viewList.add(view);
    root.getChildren().add(view);

    // 4. Attach Listeners (Dragging is in constructor, Wiring needs manual attachment)
    view.GetPin0().setOnMouseClicked(e -> handlePinClick(view, 0, root, graph));
    view.GetPin1().setOnMouseClicked(e -> handlePinClick(view, 1, root, graph));
    
    System.out.println("[LOG] Spawned " + comp.GetName() + " at " + x + "," + y);
}

    public static void main(String[] args) {
        launch();
    }
}