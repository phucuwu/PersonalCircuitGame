// src/main/java/com/javasim/view/InspectorView.java
package com.javasim.view;

import com.javasim.model.Bulb;
import com.javasim.model.Component;
import com.javasim.model.Resistor;
import com.javasim.model.VoltageSource;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class InspectorView extends VBox {
    private ComponentView inspectorSelectedView = null;

    public InspectorView() {
        this.setPrefWidth(200);
        this.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-border-left: 2px solid #ddd;");        
        this.getChildren().add(new Text("Properties Inspector"));
    }

    public void ShowInspector(ComponentView view) {
        // 1. Clear previous selection visuals
        if (inspectorSelectedView != null && inspectorSelectedView.GetVisualShape() != null) {
            inspectorSelectedView.GetVisualShape().setStroke(Color.BLACK);
            inspectorSelectedView.GetVisualShape().setStrokeWidth(1);
        }

        // 2. Highlight new selection
        inspectorSelectedView = view;
        inspectorSelectedView.GetVisualShape().setStroke(Color.BLUE);
        inspectorSelectedView.GetVisualShape().setStrokeWidth(3);

        // 3. Rebuild UI
        this.getChildren().clear();
        this.getChildren().add(new Text("Editing: " + view.GetModel().GetName()));

        Component model = view.GetModel();

        if (model instanceof Resistor) {
            AddValueSlider("Resistance (Ω)", 1, 1000, model.GetValue(), newValue -> {
                model.SetValue(newValue);
            });
        } 
        else if (model instanceof VoltageSource) {
            AddValueSlider("Voltage (V)", 0, 100, model.GetValue(), newValue -> {
                model.SetValue(newValue);
            });
        } 
        else if (model instanceof Bulb) {
            AddValueSlider("Resistance (Ω)", 1, 500, model.GetValue(), newValue -> {
                model.SetValue(newValue);
            });
        }
    }

    private void AddValueSlider(String label, double min, double max, double start, java.util.function.Consumer<Double> onUpdate) {
        Label title = new Label(label);
        Slider slider = new Slider(min, max, start);
        Label valueDisplay = new Label(String.format("%.1f", start));

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            onUpdate.accept(newVal.doubleValue());
            valueDisplay.setText(String.format("%.1f", newVal.doubleValue()));
        });

        this.getChildren().addAll(title, slider, valueDisplay);
    }
}