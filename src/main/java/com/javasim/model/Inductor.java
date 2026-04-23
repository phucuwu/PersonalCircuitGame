package com.javasim.model;

import com.javasim.model.interfaces.IElectrical;
import com.javasim.model.interfaces.ISimulatable;

public class Inductor extends Component implements IElectrical, ISimulatable {
    private double previousCurrent = 0.0;

    public Inductor(String name, double inductance) {
        super(name, inductance, 2);
    }

    @Override
    public void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime) {
        // Conductance G = dt / L
        double conductance = deltaTime / componentValue;
        
        int nodeA = nodeIds[0];
        int nodeB = nodeIds[1];

        if (nodeA > 0) matrix[nodeA - 1][nodeA - 1] += conductance;
        if (nodeB > 0) matrix[nodeB - 1][nodeB - 1] += conductance;
        if (nodeA > 0 && nodeB > 0) {
            matrix[nodeA - 1][nodeB - 1] -= conductance;
            matrix[nodeB - 1][nodeA - 1] -= conductance;
        }

        // RHS current is the current through the inductor from the last step
        if (nodeA > 0) rhsVector[nodeA - 1] -= previousCurrent;
        if (nodeB > 0) rhsVector[nodeB - 1] += previousCurrent;
    }

    public void UpdateCurrent(double vA, double vB, double deltaTime) {
        double conductance = deltaTime / componentValue;
        this.previousCurrent += conductance * (vA - vB);
    }

    @Override
    public void UpdateState(double deltaTime) {}

    @Override
    public void ResetState() {
        this.previousCurrent = 0.0;
    }

    public double GetPreviousCurrent() {
        return this.previousCurrent;
    }
}