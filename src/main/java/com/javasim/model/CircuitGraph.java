// src/main/java/com/javasim/model/CircuitGraph.java

package com.javasim.model;

import java.util.ArrayList;
import java.util.List;

import com.javasim.model.interfaces.IElectrical;

public class CircuitGraph {
    private List<Component> components;
    private NodeManager nodeManager;

    public CircuitGraph() {
        this.components = new ArrayList<>();
        this.nodeManager = new NodeManager();
    }

    public void AddComponent(Component comp) {
        components.add(comp);
    }

    public NodeManager GetNodeManager() {
        return nodeManager;
    }

    public void SolveCircuit(double deltaTime) {
        // 1. Update IDs and get dynamic counts
        int nodeCount = nodeManager.UpdateComponentNodes(components);
        int sourceCount = 0;
        
        for (Component comp : components) {
            if (comp instanceof VoltageSource) {
                // Update the sourceIndex dynamically to match its position in the 'extra' matrix part
                ((VoltageSource) comp).SetSourceIndex(sourceCount++); 
            }
        }

        int matrixSize = nodeCount + sourceCount;
        if (matrixSize == 0) return;

        double[][] A = new double[matrixSize][matrixSize];
        double[] b = new double[matrixSize];

        // 2. Stamp components
        for (Component comp : components) {
            if (comp instanceof IElectrical) {
                ((IElectrical) comp).ApplyToMatrix(A, b, deltaTime);
            }
        }

        // 3. Solve and Sync
        double[] solution = MatrixSolver.Solve(A, b);
        SyncPhysicsState(solution, deltaTime);
        UpdatePhysicsAndCurrents(solution, nodeCount, deltaTime);
    }

    private void SyncPhysicsState(double[] solution, double deltaTime) {
        for (Component comp : components) {
            int[] nodes = comp.GetNodeIds();
            double vA = (nodes[0] > 0) ? solution[nodes[0] - 1] : 0.0;
            double vB = (nodes[1] > 0) ? solution[nodes[1] - 1] : 0.0;

            if (comp instanceof Capacitor) {
                ((Capacitor) comp).RecordPhysicsState(vA, vB);
            } else if (comp instanceof Inductor) {
                ((Inductor) comp).UpdateCurrent(vA, vB, deltaTime);
            } else if (comp instanceof Bulb) {
                ((Bulb) comp).CheckStatus(vA, vB);
            }
        }
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