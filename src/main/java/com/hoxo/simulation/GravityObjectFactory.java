package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.collider.Collider;
import com.hoxo.simulation.collider.Colliders;
import com.hoxo.simulation.collider.NullCollider;

import java.util.HashMap;
import java.util.Map;

public abstract class GravityObjectFactory {

    protected static Map<Class<? extends GravityObject>, Collider> map = new HashMap<>();
    static {
        map.put(SimpleGravityObject.class, Colliders.simpleGravityObjectCollider());
        map.put(SimpleGravityObject.Static.class, Colliders.staticGravityObjectCollider());
    }
    public static Collider getCollider(Class<? extends GravityObject> clazz) {
        return map.get(clazz);
    }

    protected int trailLength = 10000;
    protected Collider collider = new NullCollider();

    public Collider getCollider() {
        return collider;
    }
    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public abstract SimpleGravityObject gravityObject(double x, double y, Vector2D v, double r);
    public abstract SimpleGravityObject staticGravityObject(double x, double y, double r);

    public void setTrailLength(int length) {
        trailLength = length;
    }
    public int getTrailLength() {
        return trailLength;
    }
}
