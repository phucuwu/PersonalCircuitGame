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
    
    // Add two circles to represent Pin 0 and Pin 1
    protected Circle pinLeft;
    protected Circle pinRight;

    public ComponentView(Component model) {
        this.model = model;
        this.shape = new Rectangle(60, 40);
        this.shape.setFill(Color.LIGHTGRAY);
        this.shape.setStroke(Color.BLACK);
        
        this.label = new Text(model.GetName());
        
        // Setup Pins
        this.pinLeft = new Circle(5, Color.BLACK);
        this.pinRight = new Circle(5, Color.BLACK);
        
        // Position pins relative to the center of the 60x40 rectangle
        pinLeft.setTranslateX(-30);
        pinRight.setTranslateX(30);

        this.getChildren().addAll(shape, label, pinLeft, pinRight);
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