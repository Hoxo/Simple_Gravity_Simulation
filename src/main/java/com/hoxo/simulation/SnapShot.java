package com.hoxo.simulation;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public class SnapShot implements Serializable {
    private final LinkedList<GravityObject> objects;
    public String name;
    public SnapShot(Collection<GravityObject> o, String name) {
        objects = new LinkedList<>();
        for (GravityObject go : o)
                objects.add(go.clone());
        this.name = name;
    }

    public LinkedList<GravityObject> getObjects() {
        return objects;
    }

    @Override
    public String toString() {
        return "Snapshot " + objects.toString();
    }
}
