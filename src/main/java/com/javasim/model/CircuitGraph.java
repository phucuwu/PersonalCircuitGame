package com.javasim.model;

import java.util.ArrayList;
import java.util.List;

import com.javasim.model.interfaces.IElectrical;

public class CircuitGraph {
    private List<Component> components;
    private int nodeCount;    // Total unique nodes (excluding ground)
    private int sourceCount;  // Total voltage sources

    public CircuitGraph(int nodeCount, int sourceCount) {
        this.components = new ArrayList<>();
        this.nodeCount = nodeCount;
        this.sourceCount = sourceCount;
    }

    public void AddComponent(Component comp) {
        components.add(comp);
    }

    /**
     * The primary engine call: Builds the matrix, solves, and syncs data.
     */
    public void SolveCircuit(double deltaTime) {
        int matrixSize = nodeCount + sourceCount;
        double[][] A = new double[matrixSize][matrixSize];
        double[] b = new double[matrixSize];

        // 1. Stamp all components into the matrix
        for (Component comp : components) {
            if (comp instanceof IElectrical) {
                ((IElectrical) comp).ApplyToMatrix(A, b, deltaTime);
            }
        }

        // 2. Solve Ax = b
        double[] solution = MatrixSolver.Solve(A, b);

        // 3. Sync voltages back to components
        // The first 'nodeCount' elements of the solution are the node voltages
        for (Component comp : components) {
            int[] nodes = comp.GetNodeIds();
            if (nodes.length < 2) continue;

            // Extract voltages from the solution (Node 0 is Ground)
            double vA = (nodes[0] > 0) ? solution[nodes[0] - 1] : 0.0;
            double vB = (nodes[1] > 0) ? solution[nodes[1] - 1] : 0.0;

            if (comp instanceof Capacitor) {
                ((Capacitor) comp).RecordPhysicsState(vA, vB); //
            } else if (comp instanceof Inductor) {
                ((Inductor) comp).UpdateCurrent(vA, vB, deltaTime); //
            } else if (comp instanceof Bulb) {
                ((Bulb) comp).CheckStatus(vA, vB); //
            }
        }
    }
}