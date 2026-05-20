// src/main/java/com/javasim/view/ToolboxView.java
package com.javasim.view;

import com.javasim.game.LevelManager;
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
    private LevelManager levelManager;

    public ToolboxView(CircuitGraph graph, WorkspaceView workspace, LevelManager levelManager) {
        this.graph = graph;
        this.workspace = workspace;
        this.levelManager = levelManager;

        this.setPrefWidth(150);
        this.setStyle("-fx-background-color: #eeeeee; -fx-padding: 10; -fx-border-color: #cccccc;");
        this.getChildren().add(new Text("Inventory"));

        Button addResistor = new Button("Add Resistor");
        addResistor.setOnAction(e -> AddNewComponent(new Resistor("R" + (workspace.GetComponentCount() + 1), 10.0), 100, 100, "Resistor"));

        Button addBattery = new Button("Add Battery");
        addBattery.setOnAction(e -> AddNewComponent(new VoltageSource("V" + (workspace.GetComponentCount() + 1), 12.0, 0), 100, 100, "VoltageSource"));

        Button addSwitch = new Button("Add Switch");
        addSwitch.setOnAction(e -> AddNewComponent(new Switch("SW" + (workspace.GetComponentCount() + 1)), 100, 100, "Switch"));

        Button addBulb = new Button("Add Bulb");
        addBulb.setOnAction(e -> AddNewComponent(new Bulb("B" + (workspace.GetComponentCount() + 1), 10.0, 5.0), 100, 100, "Bulb"));

        this.getChildren().addAll(addResistor, addBattery, addSwitch, addBulb);
    }

    private void AddNewComponent(Component comp, double x, double y, String typeKey) {
        // Enforce Level Constraint
        int limit = levelManager.GetCurrentLevel().GetAvailableCount(typeKey);
        long currentCount = graph.GetComponents().stream()
            .filter(c -> c.getClass().getSimpleName().equals(typeKey))
            .count();

        if (currentCount >= limit) {
            System.out.println("[GAME] Cannot add " + typeKey + ". Inventory limit (" + limit + ") reached.");
            return; 
        }

        graph.AddComponent(comp);

        if (comp instanceof VoltageSource) {
            graph.GetNodeManager().SetAsGround(comp, 1);
        }

        ComponentView view = (comp instanceof Switch) ? new SwitchView((Switch) comp) : new ComponentView(comp);
        workspace.AddComponentToWorkspace(view, x, y);
    }
}