package com.javasim.model;

import com.javasim.model.interfaces.IElectrical;

public class Switch extends Component implements IElectrical {
    private boolean isOpen = true;

    public Switch(String name) {
        // A switch is a 2-pin component with a nominal value of 0 (initial)
        super(name, 0.0, 2);
    }

    public void Toggle() {
        this.isOpen = !this.isOpen;
    }

    public boolean IsOpen() {
        return isOpen;
    }

    @Override
    public void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime) {
        // When closed, we use a very high conductance (effectively a wire)
        double conductance = isOpen ? 0 : 1e9;
        
        int nodeA = nodeIds[0];
        int nodeB = nodeIds[1];

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