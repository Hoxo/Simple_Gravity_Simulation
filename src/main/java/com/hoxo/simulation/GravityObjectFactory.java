package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxton on 16.09.2017.
 */
public abstract class GravityObjectFactory {

    protected int trailsize = 1000;

    public abstract GravityObject gravityObject(double x, double y, Vector2D v, int r);
    public abstract GravityObject staticGravityObject(double x, double y, int r);

    public void setTrailLength(int size) {
        trailsize = size;
    }
}
