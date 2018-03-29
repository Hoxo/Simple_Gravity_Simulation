package com.hoxo.simulation.collider;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.GravityObject;
import com.hoxo.simulation.GravitySystem;
import com.hoxo.simulation.SimpleGravityObject;
import com.hoxo.simulation.collider.Collider;

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
                sgo.setVelocity(new Vector2D(_x, _y));
                sgo.setCenter(center);
                double density = Math.max(o1.getMass() / (Math.pow(o1.getRadius(), 2)),
                        o2.getMass() / (Math.pow(o2.getRadius(), 2)));
                sgo.setMass(massSum);
                sgo.setRadius(Math.sqrt(massSum / density));
            }
        }
        if (o2 instanceof GravitySystem)
            o2.collide(o1);
    }
}
