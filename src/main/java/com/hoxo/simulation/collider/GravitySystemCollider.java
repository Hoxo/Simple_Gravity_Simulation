package com.hoxo.simulation.collider;

import com.hoxo.simulation.GravityObject;
import com.hoxo.simulation.GravitySystem;
import com.hoxo.simulation.SimpleGravityObject;

public class GravitySystemCollider implements Collider {
    @Override
    public void collide(GravityObject _this, GravityObject object) {
        GravitySystem gs = ((GravitySystem) _this);
        if (object instanceof SimpleGravityObject) {
            SimpleGravityObject clone = (SimpleGravityObject) object.clone();
            object.destroy();
            gs.add(clone);
//                for (GravityObject gravityObject : objects)
//                    if (gravityObject.collideWith(object))
//                        gravityObject.collide(object);
            gs.removeDestroyed();
        }
        if (object instanceof GravitySystem) {
            for (GravityObject o1 : gs.getGravityObjects()) {
                o1.interactWith(((GravitySystem) object).getGravityObjects());
            }
        }
    }
}
