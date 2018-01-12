package com.hoxo.simulation;

import com.hoxo.geometric.Point;

public class SGOCollider implements Collider {

    @Override
    public void collide(GravityObject o1, GravityObject o2) {
        if (o2 instanceof SimpleGravityObject) {
            if (o2 instanceof SimpleGravityObject.Static) {
                o2.collide(o1);
            } else {
                double massSum = o1.getMass() + o2.getMass();
                double e1 = o1.getMass() * Math.pow(o1.getVelocity().length(), 2);
                double e2 = o2.getMass() * Math.pow(o2.getVelocity().length(), 2);
                double _x = (o1.getVelocity().x * o1.getMass() + o2.getMass() * o2.getVelocity().x) / massSum,
                        _y = (o1.getVelocity().y * o1.getMass() + o2.getMass() * o2.getVelocity().y) / massSum;
                SimpleGravityObject sgo;

                Point center = new Point((o1.getCenter().x * o1.getMass()  + o2.getCenter().x * o2.getMass()) / massSum,
                        (o1.getCenter().y * o1.getMass()  + o2.getCenter().y * o2.getMass()) / massSum);
                if (e1 > e2) {
                    sgo = ((SimpleGravityObject) o1);
                    o2.destroy();
                } else {
                    sgo = ((SimpleGravityObject) o2);
                    o1.destroy();
                }
                sgo.velocity.x = _x;
                sgo.velocity.y = _y;
                sgo.center = center;
                double density = sgo.mass / (Math.pow(sgo.radius, 2));
                sgo.mass = massSum;
                sgo.radius = Math.sqrt(massSum / density);
            }
        }
        if (o2 instanceof GravitySystem)
            o2.collide(o1);
    }
}
