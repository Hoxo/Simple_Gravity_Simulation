package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxo on 16.09.2017.
 */
public abstract class GravityObjectFactory {

    protected int trailLength = 1000;

    public abstract SimpleGravityObject gravityObject(double x, double y, Vector2D v, int r);
    public abstract SimpleGravityObject staticGravityObject(double x, double y, int r);

    public void setTrailLength(int length) {
        trailLength = length;
    }
    public int getTrailLength() {
        return trailLength;
    }
}
