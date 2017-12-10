package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxo on 16.09.2017.
 */
public class SimpleGravityObjectFactory extends GravityObjectFactory {


    public SimpleGravityObject gravityObject(double x, double y, Vector2D v, int r) {
        SimpleGravityObject o = new SimpleGravityObject(x, y,v,r,r*r,false);
        o.setTrailLength(trailLength);
        return o;
    }

    @Override
    public SimpleGravityObject staticGravityObject(double x, double y, int r) {
        return new SimpleGravityObject(x,y,Vector2D.nullVector(),r,r*r*r,true);
    }
}
