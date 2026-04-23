package com.javasim.model;

import com.javasim.model.interfaces.IElectrical;

public class VoltageSource extends Component implements IElectrical {
    private int sourceIndex; // Which row in the matrix expansion this source owns

    public VoltageSource(String name, double voltage, int sourceIndex) {
        super(name, voltage, 2);
        this.sourceIndex = sourceIndex;
    }

    @Override
    public void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime) {
        int nodeA = nodeIds[0];
        int nodeB = nodeIds[1];
        
        // This index is in the 'extra' part of the MNA matrix
        int k = matrix.length - 1 - sourceIndex;

        if (nodeA > 0) {
            matrix[nodeA - 1][k] = 1;
            matrix[k][nodeA - 1] = 1;
        }
        if (nodeB > 0) {
            matrix[nodeB - 1][k] = -1;
            matrix[k][nodeB - 1] = -1;
        }

        rhsVector[k] = componentValue;
    }

    public void SetSourceIndex(int index) {
        this.sourceIndex = index;
    }
}