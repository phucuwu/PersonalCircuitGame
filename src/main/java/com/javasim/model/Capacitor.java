package com.javasim.model;

import com.javasim.model.interfaces.IElectrical;
import com.javasim.model.interfaces.ISimulatable;

public class Capacitor extends Component implements IElectrical, ISimulatable {
    private double previousVoltageDiff = 0.0;
    private boolean isBroken = false;
    private static final double VOLTAGE_LIMIT = 25.0;
    public Capacitor(String name, double capacitance) {
        super(name, capacitance, 2);
    }

    @Override
    public void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime) {
        // Conductance G = C / dt
        double conductance = componentValue / deltaTime;
        // Equivalent current Ieq = G * V_previous
        double equivalentCurrent = conductance * previousVoltageDiff;

        int nodeA = nodeIds[0];
        int nodeB = nodeIds[1];

        if (nodeA > 0) matrix[nodeA - 1][nodeA - 1] += conductance;
        if (nodeB > 0) matrix[nodeB - 1][nodeB - 1] += conductance;
        if (nodeA > 0 && nodeB > 0) {
            matrix[nodeA - 1][nodeB - 1] -= conductance;
            matrix[nodeB - 1][nodeA - 1] -= conductance;
        }

        // Add the history current to the RHS vector
        if (nodeA > 0) rhsVector[nodeA - 1] += equivalentCurrent;
        if (nodeB > 0) rhsVector[nodeB - 1] -= equivalentCurrent;
    }

    public void RecordPhysicsState(double vA, double vB) {
        this.previousVoltageDiff = vA - vB;
    }

    @Override
    public void UpdateState(double deltaTime) {
        // If it's already broken, we stop processing it
        if (isBroken) return;

        // Instant pop logic
        if (Math.abs(previousVoltageDiff) > VOLTAGE_LIMIT) {
            this.isBroken = true;
            // You might trigger a visual change here, e.g., set image to 'broken.png'
            System.out.println(this.componentName + " popped instantly!");
        }
    }

    @Override
    public void ResetState() {
        this.previousVoltageDiff = 0.0;
    }
}