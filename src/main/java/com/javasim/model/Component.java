package com.javasim.model;

public abstract class Component {
    protected String componentName;
    protected double componentValue;
    protected int[] nodeIds;

    public Component(String name, double value, int pinCount) {
        this.componentName = name;
        this.componentValue = value;
        this.nodeIds = new int[pinCount];
    }

    public String GetName() {
        return componentName;
    }

    public double GetValue() {
        return componentValue;
    }

    public void SetValue(double newValue) {
        this.componentValue = newValue;
    }

    public int[] GetNodeIds() {
        return nodeIds;
    }

    public void SetNodeIds(int[] ids) {
        this.nodeIds = ids;
    }
}