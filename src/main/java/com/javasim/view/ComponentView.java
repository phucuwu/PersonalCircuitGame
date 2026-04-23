package com.javasim.view;

import com.javasim.model.Bulb;
import com.javasim.model.Component;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class ComponentView extends StackPane {
    protected Component model;
    protected Rectangle shape;
    protected Text label;

    public ComponentView(Component model) {
        this.model = model;
        this.shape = new Rectangle(60, 40);
        this.shape.setFill(Color.LIGHTGRAY);
        this.shape.setStroke(Color.BLACK);
        
        this.label = new Text(model.GetName());
        
        this.getChildren().addAll(shape, label);
    }

    public void Refresh() {
        // Visual feedback based on component state
        if (model instanceof Bulb) {
            Bulb bulb = (Bulb) model;
            shape.setFill(bulb.IsLit() ? Color.YELLOW : Color.LIGHTGRAY); //
        }
        
        // You can extend this to show voltage text or 'broken' states
    }
}