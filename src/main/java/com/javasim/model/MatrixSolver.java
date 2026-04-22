package com.javasim.model;

public class MatrixSolver {

    /**
     * Solves the linear system Ax = b using Gaussian Elimination.
     * @param A The square matrix of conductances/constraints
     * @param b The RHS vector (currents/voltage sources)
     * @return The x vector containing Node Voltages and Source Currents
     */
    public static double[] Solve(double[][] A, double[] b) {
        int n = b.length;

        // Augment matrix A with vector b
        double[][] augmented = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = A[i][j];
            }
            augmented[i][n] = b[i];
        }

        // Gaussian elimination
        for (int i = 0; i < n; i++) {
            // Partial pivoting: find max in column to improve accuracy
            int max = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(augmented[k][i]) > Math.abs(augmented[max][i])) {
                    max = k;
                }
            }
            double[] temp = augmented[i];
            augmented[i] = augmented[max];
            augmented[max] = temp;

            // Make pivot 1
            double pivot = augmented[i][i];
            if (Math.abs(pivot) < 1e-12) continue; // Singular matrix check

            for (int j = i; j <= n; j++) {
                augmented[i][j] /= pivot;
            }

            // Eliminate column
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmented[k][i];
                    for (int j = i; j <= n; j++) {
                        augmented[k][j] -= factor * augmented[i][j];
                    }
                }
            }
        }

        // Extract solution
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = augmented[i][n];
        }
        return x;
    }
}