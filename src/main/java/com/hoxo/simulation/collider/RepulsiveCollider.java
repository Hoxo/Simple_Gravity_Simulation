package com.hoxo.simulation.collider;

import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.GravityObject;
import com.hoxo.simulation.collider.Collider;

public class RepulsiveCollider implements Collider {
    @Override
    public void collide(GravityObject o1, GravityObject o2) {
        Vector2D vec = o1.accelerationVectorTo(o2);
        vec.scaleLength(2);
        o1.getAcceleration().sub(vec);
    }
}
