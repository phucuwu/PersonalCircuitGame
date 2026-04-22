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
            if (comp instanceof Capacitor) {
                // Sync the specific node voltages for this component
                int n1 = comp.GetNodeIds()[0];
                int n2 = comp.GetNodeIds()[1];
                
                double v1 = (n1 > 0) ? solution[n1 - 1] : 0.0;
                double v2 = (n2 > 0) ? solution[n2 - 1] : 0.0;
                
                ((Capacitor) comp).RecordPhysicsState(v1, v2);
            }
        }
    }
}