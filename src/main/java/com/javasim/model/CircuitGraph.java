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
}