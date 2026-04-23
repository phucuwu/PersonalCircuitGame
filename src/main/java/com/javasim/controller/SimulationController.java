package com.javasim.controller;

import java.util.ArrayList;
import java.util.List;

import com.javasim.model.CircuitGraph;
import com.javasim.model.interfaces.ISimulatable;

import javafx.animation.AnimationTimer;

public class SimulationController {
    private CircuitGraph circuitGraph;
    private List<ISimulatable> simulatableComponents;
    private final double deltaTime = 0.01;
    private boolean isRunning = false;

    public SimulationController(CircuitGraph graph) {
        this.circuitGraph = graph;
        this.simulatableComponents = new ArrayList<>();
    }

    public void AddSimulatable(ISimulatable comp) {
        this.simulatableComponents.add(comp);
    }

    public void StartSimulation() {
        if (isRunning) return;
        isRunning = true;

        AnimationTimer simulationLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isRunning) {
                    this.stop();
                    return;
                }

                // 1. Solve the Physics
                circuitGraph.SolveCircuit(deltaTime);

                // 2. Update logic for time-dependent components (e.g. Capacitor popping)
                for (ISimulatable comp : simulatableComponents) {
                    comp.UpdateState(deltaTime);
                }

                // 3. UI Update
                UpdateView();
            }
        };
        simulationLoop.start();
    }

    public void StopSimulation() {
        isRunning = false;
    }

    protected void UpdateView() {
        // Overridden in App.java to refresh specific views
    }
}