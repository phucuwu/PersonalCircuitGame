package com.javasim.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NodeManager {
    private List<Set<PinReference>> nets;
    private Set<PinReference> groundPins;
    private int lastKnownNodeCount = 0;
    public NodeManager() {
        this.nets = new ArrayList<>();
        this.groundPins = new HashSet<>();
    }

    /**
     * Connects two specific pins together.
     */
    public void Connect(Component c1, int p1, Component c2, int p2) {
        System.out.println("[LOG] Connecting " + c1.GetName() + " (Pin " + p1 + ") to " + 
                       c2.GetName() + " (Pin " + p2 + ")"); // cite: 8
        PinReference ref1 = new PinReference(c1, p1);
        PinReference ref2 = new PinReference(c2, p2);

        Set<PinReference> net1 = FindNet(ref1);
        Set<PinReference> net2 = FindNet(ref2);

        if (net1 == null && net2 == null) {
            Set<PinReference> newNet = new HashSet<>();
            newNet.add(ref1);
            newNet.add(ref2);
            nets.add(newNet);
        } else if (net1 != null && net2 == null) {
            net1.add(ref2);
        } else if (net1 == null && net2 != null) {
            net2.add(ref1);
        } else if (net1 != net2) {
            net1.addAll(net2);
            nets.remove(net2);
        }
    }

    /**
     * Designates a specific pin as the Ground reference (Node 0).
     */
    public void SetAsGround(Component comp, int pinIndex) {
        groundPins.add(new PinReference(comp, pinIndex));
        System.out.println("[LOG] " + comp.GetName() + " (Pin " + pinIndex + ") designated as Ground."); // cite: 8
    }

        /**
         * Assigns final integer IDs to all component pins based on connections.
         * Returns the total number of unique nodes (excluding ground).
         */
        public int UpdateComponentNodes(List<Component> components) {
        // 1. Ensure all pins have a default ID (isolated pins get unique IDs)
            for (Component c : components) {
                int[] ids = c.GetNodeIds();
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = -1; 
                }
            }

        // 2. Identify the Ground Net
        Set<PinReference> groundNet = null;
        for (PinReference gPin : groundPins) {
            groundNet = FindNet(gPin);
            if (groundNet != null) break;
        }

        // 3. Assign IDs
        int nextId = 1;
        for (Set<PinReference> net : nets) {
            int assignedId;
            if (net == groundNet || ContainsAny(net, groundPins)) {
                assignedId = 0;
            } else {
                assignedId = nextId++;
            }

            for (PinReference ref : net) {
                ref.ComponentRef.GetNodeIds()[ref.PinIndex] = assignedId;
            }
        }
        
        int totalNodes = nextId - 1;
    
    if (totalNodes > lastKnownNodeCount) {
        System.out.println("[LOG] New nodes created. Total active nodes: " + totalNodes);
    } else if (totalNodes < lastKnownNodeCount) {
        System.out.println("[LOG] Nodes merged or removed. Total active nodes: " + totalNodes);
    }
    
    lastKnownNodeCount = totalNodes;
    return totalNodes;
    }

    private Set<PinReference> FindNet(PinReference ref) {
        for (Set<PinReference> net : nets) {
            if (net.contains(ref)) return net;
        }
        return null;
    }

    private boolean ContainsAny(Set<PinReference> net, Set<PinReference> targets) {
        for (PinReference t : targets) {
            if (net.contains(t)) return true;
        }
        return false;
    }

    /**
     * Helper class to identify a specific pin on a specific component.
     */
    private static class PinReference {
        public Component ComponentRef;
        public int PinIndex;

        public PinReference(Component c, int p) {
            this.ComponentRef = c;
            this.PinIndex = p;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PinReference)) return false;
            PinReference that = (PinReference) o;
            return PinIndex == that.PinIndex && ComponentRef.equals(that.ComponentRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ComponentRef, PinIndex);
        }
    }
    public boolean HasGround() {
    // Check if any pins were explicitly set as ground
    if (!groundPins.isEmpty()) return true;

    // Alternatively, check if any net was assigned ID 0 during UpdateComponentNodes
    for (Set<PinReference> net : nets) {
        for (PinReference ref : net) {
            if (ref.ComponentRef.GetNodeIds()[ref.PinIndex] == 0) {
                return true;
            }
        }
    }
    return false;
}
}