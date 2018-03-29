package com.hoxo.simulation.collider;

import com.hoxo.simulation.GravityObject;

public interface Collider {
    void collide(GravityObject o1, GravityObject o2);
}
