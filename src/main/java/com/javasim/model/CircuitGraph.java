// src/main/java/com/javasim/model/CircuitGraph.java

package com.javasim.model;

import java.util.ArrayList;
import java.util.List;

import com.javasim.model.interfaces.IElectrical;

public class CircuitGraph {
    private List<Component> components;
    private NodeManager nodeManager;
    private String lastErrorMessage = "";

    public CircuitGraph() {
        this.components = new ArrayList<>();
        this.nodeManager = new NodeManager();
    }

    public void AddComponent(Component comp) {
        components.add(comp);
        System.out.println("[LOG] Component added: " + comp.GetName()); // cite: 13, 8
    }

    public NodeManager GetNodeManager() {
        return nodeManager;
    }

   /**
     * Validates if the circuit is solvable.
     * @return true if valid, false otherwise.
     */
    public boolean Validate() {
        if (components.isEmpty()) {
            lastErrorMessage = "Circuit is empty.";
            return false;
        }

        // Must have a ground reference for MNA to work
        if (!nodeManager.HasGround()) {
            lastErrorMessage = "Missing Ground! Please connect a component to Node 0.";
            return false;
        }

        // Basic check: Are there any voltage sources or powered elements?
        boolean hasSource = components.stream().anyMatch(c -> c instanceof VoltageSource);
        if (!hasSource) {
            lastErrorMessage = "Warning: No power source detected.";
            // We return true here because a dead circuit is physically "valid," 
            // but the warning helps the user.
        }

        lastErrorMessage = "";
        return true;
    }

    public String GetLastErrorMessage() {
        return lastErrorMessage;
    }

    public void SolveCircuit(double deltaTime) {
        // --- NEW VALIDATION CHECK ---
        if (!Validate()) {
            // If invalid, we set all currents to zero to stop "ghost" animations
            for (Component c : components) c.SetCalculatedCurrent(0);
            return;
        }

        int nodeCount = nodeManager.UpdateComponentNodes(components);
        int sourceCount = 0;
        
        for (Component comp : components) {
            if (comp instanceof VoltageSource) {
                ((VoltageSource) comp).SetSourceIndex(sourceCount++);
            }
        }

        int matrixSize = nodeCount + sourceCount;
        double[][] A = new double[matrixSize][matrixSize];
        double[] b = new double[matrixSize];

        for (Component comp : components) {
            if (comp instanceof IElectrical) {
                ((IElectrical) comp).ApplyToMatrix(A, b, deltaTime);
            }
        }

        double[] solution = MatrixSolver.Solve(A, b);
        
        // If the solver failed due to a singular matrix (e.g., short circuit)
        if (solution == null) {
            lastErrorMessage = "Singular Matrix: Possible short circuit detected!";
            return;
        }

        UpdatePhysicsAndCurrents(solution, nodeCount, deltaTime);
    }

    private void UpdatePhysicsAndCurrents(double[] solution, int nodeCount, double deltaTime) {
        for (Component comp : components) {
            int[] nodes = comp.GetNodeIds();
            double vA = (nodes[0] > 0) ? solution[nodes[0] - 1] : 0.0;
            double vB = (nodes[1] > 0) ? solution[nodes[1] - 1] : 0.0;
            double vDiff = vA - vB;

            if (comp instanceof Resistor) {
                // I = V / R
                double current = (comp.GetValue() != 0) ? vDiff / comp.GetValue() : 0;
                comp.SetCalculatedCurrent(current);

                if (comp instanceof Bulb) {
                    ((Bulb) comp).CheckStatus(vA, vB);
                }
            } 
            else if (comp instanceof VoltageSource) {
                // In MNA, the current of the k-th source is at solution[nodeCount + k]
                int k = ((VoltageSource) comp).GetSourceIndex();
                // We use the negative because the solver usually treats source current as entering the node
                comp.SetCalculatedCurrent(-solution[nodeCount + k]);
            } 
            else if (comp instanceof Capacitor) {
                // I = C * dv/dt (In companion model: I = G * (V_curr - V_prev))
                Capacitor cap = (Capacitor) comp;
                double conductance = cap.GetValue() / deltaTime;
                double current = conductance * (vDiff - cap.GetPreviousVoltageDiff());
                cap.SetCalculatedCurrent(current);
                cap.RecordPhysicsState(vA, vB);
            } 
            else if (comp instanceof Inductor) {
                // Update logic already exists in your Inductor.java
                Inductor ind = (Inductor) comp;
                ind.UpdateCurrent(vA, vB, deltaTime);
                comp.SetCalculatedCurrent(ind.GetPreviousCurrent());
            }
        }
    }
}