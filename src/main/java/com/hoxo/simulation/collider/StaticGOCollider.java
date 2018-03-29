package com.hoxo.simulation.collider;

import com.hoxo.simulation.GravityObject;
import com.hoxo.simulation.GravitySystem;
import com.hoxo.simulation.SimpleGravityObject;

public class  StaticGOCollider implements Collider {

    @Override
    public void collide(GravityObject o1, GravityObject o2) {
        SimpleGravityObject.Static _this = ((SimpleGravityObject.Static) o1);
        if (o2 instanceof SimpleGravityObject) {
            if (o2 instanceof SimpleGravityObject.Static)
                return;
            double massSum = _this.getMass() + o2.getMass();
            double density = _this.getMass() / Math.pow(_this.getRadius(), 2);
            _this.setMass(massSum);
            _this.setRadius(Math.sqrt(massSum / density));
            o2.destroy();
        }
        if (o2 instanceof GravitySystem)
            o2.collide(_this);
    }
}
