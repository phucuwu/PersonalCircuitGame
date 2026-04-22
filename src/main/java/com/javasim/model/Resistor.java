package com.javasim.model;

import com.javasim.model.interfaces.IElectrical;

public class Resistor extends Component implements IElectrical {

    public Resistor(String name, double resistance) {
        super(name, resistance, 2);
    }

    @Override
    public void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime) {
        if (componentValue == 0) return; // Prevent division by zero
        
        double conductance = 1.0 / componentValue;
        int nodeA = nodeIds[0];
        int nodeB = nodeIds[1];

        // We subtract 1 from node IDs because the matrix is 0-indexed,
        // but Node 0 in the circuit is Ground (ignored in matrix).
        
        if (nodeA > 0) {
            matrix[nodeA - 1][nodeA - 1] += conductance;
        }
        if (nodeB > 0) {
            matrix[nodeB - 1][nodeB - 1] += conductance;
        }
        if (nodeA > 0 && nodeB > 0) {
            matrix[nodeA - 1][nodeB - 1] -= conductance;
            matrix[nodeB - 1][nodeA - 1] -= conductance;
        }
    }
}