package com.javasim;

import java.util.ArrayList;
import java.util.List;

import com.javasim.controller.SimulationController;
import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;
import com.javasim.model.Resistor;
import com.javasim.model.Switch;
import com.javasim.model.VoltageSource;
import com.javasim.view.ComponentView;
import com.javasim.view.SwitchView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
    private ComponentView wiringSourceView = null; // Rename this
    private int selectedPinIndex = -1;
    
    // Field for the Inspector
    private VBox inspectorBox;
    private ComponentView inspectorSelectedView = null; // Add this

    @Override
    public void start(Stage stage) {
    // 1. Root Layout - The HBox that holds our three main sections
    HBox mainLayout = new HBox();
    
    // 2. LEFT: Toolbox (Buttons for spawning)
    VBox toolbox = new VBox(10);
    toolbox.setPrefWidth(150);
    toolbox.setStyle("-fx-background-color: #eeeeee; -fx-padding: 10; -fx-border-color: #cccccc;");
    toolbox.getChildren().add(new Text("Toolbox"));

    // 3. MIDDLE: Drawing Pane (The workspace)
    Pane drawingPane = new Pane();
    drawingPane.setPrefSize(800, 600);

    // 4. RIGHT: Inspector (Property editing)
    inspectorBox = new VBox(15);
    inspectorBox.setPrefWidth(200);
    inspectorBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-border-left: 2px solid #ddd;");
    inspectorBox.getChildren().add(new Text("Properties Inspector"));

    // 5. Initialize the Physics Engine
    CircuitGraph graph = new CircuitGraph();

    // 6. Setup Spawner Buttons in Toolbox
    Button addResistor = new Button("Add Resistor");
    addResistor.setOnAction(e -> AddNewComponent(new Resistor("R" + (viewList.size() + 1), 10.0), 100, 100, drawingPane, graph));

    Button addBattery = new Button("Add Battery");
    addBattery.setOnAction(e -> AddNewComponent(new VoltageSource("V" + (viewList.size() + 1), 12.0, 0), 100, 100, drawingPane, graph));

   

    Button addSwitch = new Button("Add Switch");
    addSwitch.setOnAction(e -> AddNewComponent(new Switch("SW" + (viewList.size() + 1)), 100, 100, drawingPane, graph));

    Button addBulb = new Button("Add Bulb");
    addBulb.setOnAction(e -> AddNewComponent(new Bulb("B" + (viewList.size() + 1), 10.0, 5.0), 100, 100, drawingPane, graph));

        

    // Update the addAll to include addBulb
    toolbox.getChildren().addAll(addResistor, addBattery, addSwitch, addBulb);


    // CRITICAL FIX: Clear and add once
    mainLayout.getChildren().clear(); 
    mainLayout.getChildren().addAll(toolbox, drawingPane, inspectorBox);

    // 7. Setup Controller
    SimulationController controller = new SimulationController(graph) {
        @Override
        public void UpdateView() {
            for (ComponentView v : viewList) {
                v.Refresh();
            }
        }
    };

    // 8. Final Scene Setup
    Scene scene = new Scene(mainLayout);
    stage.setTitle("JavaSim - Circuit Sandbox");
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
        wiringSourceView = view;
        selectedPinIndex = pinIndex;
        selectedPin = (pinIndex == 0) ? view.GetPin0() : view.GetPin1();
        selectedPin.setFill(Color.RED);
    } else {
        if (wiringSourceView != view) {
            Line wire = new Line();
            wire.setStrokeWidth(2);

            // BIND start of the wire to the first component
            wire.startXProperty().bind(wiringSourceView.layoutXProperty().add(selectedPinIndex == 0 ? 0 : 60));
            wire.startYProperty().bind(wiringSourceView.layoutYProperty().add(20));

            // BIND end of the wire to the second component
            wire.endXProperty().bind(view.layoutXProperty().add(pinIndex == 0 ? 0 : 60));
            wire.endYProperty().bind(view.layoutYProperty().add(20));

            root.getChildren().add(wire);
            wire.toBack(); 

            // Log and connect
            graph.GetNodeManager().Connect(wiringSourceView.GetModel(), selectedPinIndex, view.GetModel(), pinIndex);
        }
        
        selectedPin.setFill(Color.BLACK);
        selectedPin = null;
        wiringSourceView = null;
    }
}
    private void AddNewComponent(Component comp, double x, double y, Pane root, CircuitGraph graph) {
    // 1. Add to Physics Model
    graph.AddComponent(comp);

    // 2. Create the correct View
    ComponentView view;

    if (comp instanceof VoltageSource) {
    // If this is the first battery, set pin 1 as ground automatically
    graph.GetNodeManager().SetAsGround(comp, 1); 
    System.out.println("[LOG] Auto-grounded " + comp.GetName());
}

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
    view.setOnMouseClicked(e -> {
    ShowInspector(view);
});
    
    
    System.out.println("[LOG] Spawned " + comp.GetName() + " at " + x + "," + y);
}
    private void ShowInspector(ComponentView view) {
    // 1. Clear previous selection visuals
    if (inspectorSelectedView != null) {
        inspectorSelectedView.GetVisualShape().setStroke(Color.BLACK);
        inspectorSelectedView.GetVisualShape().setStrokeWidth(1);
    }

    // 2. Highlight new selection
    inspectorSelectedView = view;
    inspectorSelectedView.GetVisualShape().setStroke(Color.BLUE);
    inspectorSelectedView.GetVisualShape().setStrokeWidth(3);

    // 3. Rebuild Inspector UI
    inspectorBox.getChildren().clear();
    inspectorBox.getChildren().add(new Text("Editing: " + view.GetModel().GetName()));

    Component model = view.GetModel();

    // Logic for Resistors
    if (model instanceof Resistor) {
        AddValueSlider("Resistance (Ω)", 1, 1000, model.GetValue(), newValue -> {
            model.SetValue(newValue);
        });
    }
    // Logic for Voltage Sources
    else if (model instanceof VoltageSource) {
        AddValueSlider("Voltage (V)", 0, 100, model.GetValue(), newValue -> {
            model.SetValue(newValue);
        });
    }
    else if (model instanceof Bulb) {
    // Bulbs act like resistors in the matrix, so we edit the resistance value
    AddValueSlider("Resistance (Ω)", 1, 500, model.GetValue(), newValue -> {
        model.SetValue(newValue);
    });
}
    // Add logic for other types (Bulbs, etc.) as needed
}

    private void AddValueSlider(String label, double min, double max, double start, java.util.function.Consumer<Double> onUpdate) {
        Label title = new Label(label);
        Slider slider = new Slider(min, max, start);
        Label valueDisplay = new Label(String.format("%.1f", start));

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            onUpdate.accept(newVal.doubleValue());
            valueDisplay.setText(String.format("%.1f", newVal.doubleValue()));
        });

        inspectorBox.getChildren().addAll(title, slider, valueDisplay);
    }

    public static void main(String[] args) {
        launch();
    }
}