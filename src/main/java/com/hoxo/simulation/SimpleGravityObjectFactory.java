package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

public class SimpleGravityObjectFactory extends GravityObjectFactory {

    public SimpleGravityObject gravityObject(double x, double y, Vector2D v, double r) {
        SimpleGravityObject o = new SimpleGravityObject(x, y,v,r,r*r);
        o.setTrailLength(trailLength);
        return o;
    }

    @Override
    public SimpleGravityObject staticGravityObject(double x, double y, double r) {
        return new SimpleGravityObject.Static(x,y, r,r*r*r);
    }
}
