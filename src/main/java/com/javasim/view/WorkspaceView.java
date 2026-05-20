// src/main/java/com/javasim/view/WorkspaceView.java
package com.javasim.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.javasim.model.CircuitGraph;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class WorkspaceView extends Pane {
    private CircuitGraph graph;
    private List<ComponentView> viewList;
    
    // Wiring State
    private Circle selectedPin = null;
    private ComponentView wiringSourceView = null;
    private int selectedPinIndex = -1;
    
    // Callback to tell App.java to open the Inspector
    private Consumer<ComponentView> onComponentSelected;

    public WorkspaceView(CircuitGraph graph, Consumer<ComponentView> onComponentSelected) {
        this.graph = graph;
        this.onComponentSelected = onComponentSelected;
        this.viewList = new ArrayList<>();
        this.setPrefSize(800, 600);
    }

    public void AddComponentToWorkspace(ComponentView view, double x, double y) {
        view.setLayoutX(x);
        view.setLayoutY(y);
        
        this.viewList.add(view);
        this.getChildren().add(view);

        // Attach Wiring Listeners
        view.GetPin0().setOnMouseClicked(e -> HandlePinClick(view, 0));
        view.GetPin1().setOnMouseClicked(e -> HandlePinClick(view, 1));

        // Attach Inspector Listener
        view.setOnMouseClicked(e -> {
            if (onComponentSelected != null) {
                onComponentSelected.accept(view);
            }
        });
    }

    private void HandlePinClick(ComponentView view, int pinIndex) {
        if (selectedPin == null) {
            wiringSourceView = view;
            selectedPinIndex = pinIndex;
            selectedPin = (pinIndex == 0) ? view.GetPin0() : view.GetPin1();
            selectedPin.setFill(Color.RED);
        } else {
            if (wiringSourceView != view) {
                Line wire = new Line();
                wire.setStrokeWidth(2);

                // Bind start of the wire
                wire.startXProperty().bind(wiringSourceView.layoutXProperty().add(selectedPinIndex == 0 ? 0 : 60));
                wire.startYProperty().bind(wiringSourceView.layoutYProperty().add(20));

                // Bind end of the wire
                wire.endXProperty().bind(view.layoutXProperty().add(pinIndex == 0 ? 0 : 60));
                wire.endYProperty().bind(view.layoutYProperty().add(20));

                this.getChildren().add(wire);
                wire.toBack(); 

                // Log and connect in the physics engine
                graph.GetNodeManager().Connect(wiringSourceView.GetModel(), selectedPinIndex, view.GetModel(), pinIndex);
            }
            
            selectedPin.setFill(Color.BLACK);
            selectedPin = null;
            wiringSourceView = null;
        }
    }

    public void RefreshAllViews() {
        for (ComponentView v : viewList) {
            v.Refresh();
        }
    }
    
    // Helper to get the count for naming new components
    public int GetComponentCount() {
        return viewList.size();
    }
}