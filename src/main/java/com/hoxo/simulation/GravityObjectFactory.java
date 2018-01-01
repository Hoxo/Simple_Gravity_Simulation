package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

import java.util.HashMap;
import java.util.Map;

public abstract class GravityObjectFactory {

    protected static Map<Class<? extends GravityObject>, Collider> map = new HashMap<>();
    static {
        map.put(SimpleGravityObject.class, Colliders.simpleGravityObjectCollider());
        map.put(SimpleGravityObject.Static.class, Colliders.staticGravityObjectCollider());
    }

    protected int trailLength = 10000;

    public abstract SimpleGravityObject gravityObject(double x, double y, Vector2D v, double r);
    public abstract SimpleGravityObject staticGravityObject(double x, double y, double r);
    public static Collider getCollider(Class<? extends GravityObject> clazz) {
        return map.get(clazz);
    }

    public void setTrailLength(int length) {
        trailLength = length;
    }
    public int getTrailLength() {
        return trailLength;
    }
}
