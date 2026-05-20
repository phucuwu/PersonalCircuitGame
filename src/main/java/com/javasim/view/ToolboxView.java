// src/main/java/com/javasim/view/ToolboxView.java
package com.javasim.view;

import com.javasim.model.Bulb;
import com.javasim.model.CircuitGraph;
import com.javasim.model.Component;
import com.javasim.model.Resistor;
import com.javasim.model.Switch;
import com.javasim.model.VoltageSource;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ToolboxView extends VBox {
    private CircuitGraph graph;
    private WorkspaceView workspace;

    public ToolboxView(CircuitGraph graph, WorkspaceView workspace) {
        this.graph = graph;
        this.workspace = workspace;

        this.setPrefWidth(150);
        this.setStyle("-fx-background-color: #eeeeee; -fx-padding: 10; -fx-border-color: #cccccc;");
        this.getChildren().add(new Text("Toolbox"));

        // Setup Buttons
        Button addResistor = new Button("Add Resistor");
        addResistor.setOnAction(e -> AddNewComponent(new Resistor("R" + (workspace.GetComponentCount() + 1), 10.0), 100, 100));

        Button addBattery = new Button("Add Battery");
        addBattery.setOnAction(e -> AddNewComponent(new VoltageSource("V" + (workspace.GetComponentCount() + 1), 12.0, 0), 100, 100));

        Button addSwitch = new Button("Add Switch");
        addSwitch.setOnAction(e -> AddNewComponent(new Switch("SW" + (workspace.GetComponentCount() + 1)), 100, 100));

        Button addBulb = new Button("Add Bulb");
        addBulb.setOnAction(e -> AddNewComponent(new Bulb("B" + (workspace.GetComponentCount() + 1), 10.0, 5.0), 100, 100));

        this.getChildren().addAll(addResistor, addBattery, addSwitch, addBulb);
    }

    private void AddNewComponent(Component comp, double x, double y) {
        graph.AddComponent(comp);

        if (comp instanceof VoltageSource) {
            graph.GetNodeManager().SetAsGround(comp, 1);
            System.out.println("[LOG] Auto-grounded " + comp.GetName());
        }

        ComponentView view;
        if (comp instanceof Switch) {
            view = new SwitchView((Switch) comp);
        } else {
            view = new ComponentView(comp);
        }

        workspace.AddComponentToWorkspace(view, x, y);
        System.out.println("[LOG] Spawned " + comp.GetName() + " at " + x + "," + y);
    }
}