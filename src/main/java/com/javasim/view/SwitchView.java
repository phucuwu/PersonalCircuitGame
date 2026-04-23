package com.javasim.view;

import com.javasim.model.Switch;

import javafx.scene.paint.Color;

public class SwitchView extends ComponentView {

    public SwitchView(Switch model) {
        super(model);
        
        // Add click listener to the main body to toggle the switch
        this.shape.setOnMouseClicked(e -> {
            model.Toggle();
            Refresh();
        });
    }

    @Override
    public void Refresh() {
        Switch switchModel = (Switch) model;
        if (switchModel.IsOpen()) {
            this.shape.setFill(Color.LIGHTCORAL); // Visual cue for "Off/Open"
            this.label.setText(model.GetName() + " (OPEN)");
        } else {
            this.shape.setFill(Color.LIGHTGREEN); // Visual cue for "On/Closed"
            this.label.setText(model.GetName() + " (CLOSED)");
        }
    }
}