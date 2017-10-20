package com.hoxo.simulation;

import java.io.Serializable;
import java.util.LinkedList;

public class SnapShot implements Serializable {
    private final LinkedList<GravityObject> objects;
    public String name;
    public SnapShot(LinkedList<GravityObject> o, String name) {
        objects = new LinkedList<>();
        for (GravityObject go : o) {
            try {
                objects.add((GravityObject) go.clone());
            } catch (Exception e) {}
        }
        this.name = name;
    }

    public LinkedList<GravityObject> getObjects() {
        return objects;
    }

}
