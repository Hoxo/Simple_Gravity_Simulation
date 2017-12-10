package com.hoxo.simulation;

import java.io.Serializable;
import java.util.LinkedList;

public class SnapShot implements Serializable {
    private final LinkedList<SimpleGravityObject> objects;
    public String name;
    public SnapShot(LinkedList<SimpleGravityObject> o, String name) {
        objects = new LinkedList<>();
        for (SimpleGravityObject go : o) {
            try {
                objects.add((SimpleGravityObject) go.clone());
            } catch (Exception e) {}
        }
        this.name = name;
    }

    public LinkedList<SimpleGravityObject> getObjects() {
        return objects;
    }

}
