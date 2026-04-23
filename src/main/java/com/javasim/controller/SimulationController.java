package com.javasim.controller;

import com.javasim.model.*;
import com.javasim.model.interfaces.ISimulatable;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    private CircuitGraph circuitGraph;
    private List<ISimulatable> simulatableComponents;
    private final double deltaTime = 0.01; // 10ms steps for stability
    private boolean isRunning = false;

    public SimulationController(CircuitGraph graph) {
        this.circuitGraph = graph;
        this.simulatableComponents = new ArrayList<>();
    }

    public void StartSimulation() {
        if (isRunning) return;
        isRunning = true;

        // AnimationTimer runs once every frame (approx 60 times per second)
        AnimationTimer simulationLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isRunning) {
                    this.stop();
                    return;
                }

                // 1. Solve the Physics
                // We run multiple steps per frame if needed for high precision
                circuitGraph.SolveCircuit(deltaTime);

                // 2. Update logic (pops, etc.)
                for (ISimulatable comp : simulatableComponents) {
                    comp.UpdateState(deltaTime);
                }

                // 3. UI Update
                // This is where you call your View to refresh component colors/labels
                UpdateView();
            }
        };
        simulationLoop.start();
    }

    public void StopSimulation() {
        isRunning = false;
    }

    private void UpdateView() {
        // Logic to update JavaFX UI elements based on new Model values
    }
}