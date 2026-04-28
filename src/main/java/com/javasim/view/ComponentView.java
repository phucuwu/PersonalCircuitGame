package com.javasim.view;

import com.javasim.model.Bulb;
import com.javasim.model.Component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ComponentView extends StackPane {
    protected Component model;
    protected Rectangle shape;
    protected Text label;
    protected Text dataOverlay; // New field for current/voltage display
    protected Circle pinLeft;
    protected Circle pinRight;

    // Fields to track drag offset
    private double mouseAnchorX;
    private double mouseAnchorY;

    public ComponentView(Component model) {
        this.model = model;
        this.shape = new Rectangle(60, 40);
        this.shape.setFill(Color.LIGHTGRAY);
        this.shape.setStroke(Color.BLACK);
        
        this.label = new Text(model.GetName());
        
        this.pinLeft = new Circle(5, Color.BLACK);
        this.pinRight = new Circle(5, Color.BLACK);
        pinLeft.setTranslateX(-30);
        pinRight.setTranslateX(30);

        this.getChildren().addAll(shape, label, pinLeft, pinRight);

        this.dataOverlay = new Text("");
        this.dataOverlay.setFill(Color.BLUE);
        this.dataOverlay.setTranslateY(35); // Position below the component
        
        this.getChildren().add(dataOverlay);
        
        // Enable dragging by default
        EnableDrag();
    }

    private void EnableDrag() {
        this.setOnMousePressed(event -> {
            // Record where the mouse is relative to the component's top-left
            mouseAnchorX = event.getSceneX() - this.getLayoutX();
            mouseAnchorY = event.getSceneY() - this.getLayoutY();
            this.toFront(); // Bring dragged component to top
        });

        this.setOnMouseDragged(event -> {
            // Update position based on mouse movement
            this.setLayoutX(event.getSceneX() - mouseAnchorX);
            this.setLayoutY(event.getSceneY() - mouseAnchorY);
        });
    }

    public Circle GetPin0() { return pinLeft; }
    public Circle GetPin1() { return pinRight; }
    public Component GetModel() { return model; }

    public void Refresh() {
        // Visual feedback based on component state
        if (model instanceof Bulb) {
            Bulb bulb = (Bulb) model;
            shape.setFill(bulb.IsLit() ? Color.YELLOW : Color.LIGHTGRAY); //
        }
        double current = model.GetCalculatedCurrent();
    
        // 2. Update Overlay String
        // We use String.format to keep it to 2 decimal places
        String info = String.format("%.2f A", current);
        dataOverlay.setText(info);

        // 3. Special Logic for Bulbs (if this instance is a bulb)
        if (model instanceof Bulb && ((Bulb) model).IsLit()) {
            this.shape.setFill(Color.YELLOW);
        } else if (model instanceof Bulb) {
            this.shape.setFill(Color.LIGHTGRAY);
    }
        
        // You can extend this to show voltage text or 'broken' states
    }
}