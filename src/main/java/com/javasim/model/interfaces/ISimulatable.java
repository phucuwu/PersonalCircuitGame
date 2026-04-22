package com.javasim.model.interfaces;


public interface ISimulatable {

    /**
     * Updates the internal physical state of the component 
     * based on the current simulation time step.
     * * @param deltaTime The duration of the time step
     */
    void UpdateState(double deltaTime);

    /**
     * Resets the component to its initial state (e.g., discharging a capacitor).
     */
    void ResetState();
}

