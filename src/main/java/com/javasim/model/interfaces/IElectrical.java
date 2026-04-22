package com.javasim.model.interfaces;

public interface IElectrical {
    double GetValue();
    void SetValue(double value);
    int[] GetNodeIds(); 
    void ApplyToMatrix(double[][] matrix, double[] rhsVector, double deltaTime);
}