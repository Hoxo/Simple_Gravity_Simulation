package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxo on 07.01.2018.
 */
public class RepulsiveCollider implements Collider {
    @Override
    public void collide(GravityObject o1, GravityObject o2) {
        Vector2D vector2D = o1.accelerationVectorTo(o2);
        vector2D.scaleLength(2);
        o1.getAcceleration().sub(vector2D);
    }
}
