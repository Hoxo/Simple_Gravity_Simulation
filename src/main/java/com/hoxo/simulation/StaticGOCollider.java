package com.hoxo.simulation;

public class StaticGOCollider implements Collider {
    @Override
    public void collide(GravityObject o1, GravityObject o2) {
        SimpleGravityObject.Static _this = ((SimpleGravityObject.Static) o1);
        if (o2 instanceof SimpleGravityObject) {
            if (o2 instanceof SimpleGravityObject.Static)
                return;
            double massSum = _this.mass + o2.getMass();
            double density = _this.mass / Math.pow(_this.radius, 2);
            _this.mass = massSum;
            _this.radius = (int) Math.sqrt(massSum / density);
            o2.destroy();
        }
        if (o2 instanceof GravitySystem)
            o2.collide(_this);
    }
}
