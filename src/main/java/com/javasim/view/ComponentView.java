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
        
        // You can extend this to show voltage text or 'broken' states
    }
}