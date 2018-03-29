package com.hoxo.simulation.collider;

import com.hoxo.simulation.GravityObject;
import com.hoxo.simulation.collider.Collider;

public class NullCollider implements Collider {
    @Override
    public void collide(GravityObject o1, GravityObject o2) {}
}
