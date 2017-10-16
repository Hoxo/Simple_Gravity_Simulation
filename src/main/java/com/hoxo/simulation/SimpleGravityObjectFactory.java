package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxton on 16.09.2017.
 */
public class SimpleGravityObjectFactory extends GravityObjectFactory {


    public GravityObject gravityObject(double x, double y, Vector2D v, int r) {
        GravityObject o = new GravityObject(x, y,v,r,r*r,false);
        o.setTrailLength(trailsize);
        return o;
    }

    @Override
    public GravityObject staticGravityObject(double x, double y, int r) {
        return new GravityObject(x,y,Vector2D.nullVector(),r,r*r*r,true);
    }
}
