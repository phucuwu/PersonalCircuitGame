package com.javasim.model;

public class Bulb extends Resistor {
    private double thresholdVoltage;
    private boolean isLit = false;

    public Bulb(String name, double resistance, double threshold) {
        super(name, resistance);
        this.thresholdVoltage = threshold;
    }

    public void CheckStatus(double vA, double vB) {
        double voltageDrop = Math.abs(vA - vB);
        this.isLit = voltageDrop >= thresholdVoltage;
    }

    public boolean IsLit() {
        return isLit;
    }
}