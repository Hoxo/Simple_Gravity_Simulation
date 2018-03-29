package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.collider.Collider;

public class SimpleGravityObjectFactory extends GravityObjectFactory {

    public SimpleGravityObjectFactory() {}

    public SimpleGravityObjectFactory(Collider collider) {
        this.collider = collider;
    }

    public SimpleGravityObject gravityObject(double x, double y, Vector2D v, double r) {
        SimpleGravityObject o = new SimpleGravityObject(x, y,v,r,r*r);
        o.setCollider(collider);
        o.setTrailLength(trailLength);
        return o;
    }

    @Override
    public SimpleGravityObject staticGravityObject(double x, double y, double r) {
        SimpleGravityObject.Static s = new SimpleGravityObject.Static(x,y, r,r*r*r);
        s.setCollider(collider);
        return s;
    }
}
