package com.hoxo.simulation.collider;

import com.hoxo.simulation.*;

import java.util.HashMap;
import java.util.Map;

public class Colliders {

    private static Map<Class<? extends GravityObject>, Collider> repulsiveColliderMap;

    static {
        repulsiveColliderMap = new HashMap<>();
        repulsiveColliderMap.put(SimpleGravityObject.class, repulsiveCollider());

    }

    private Colliders() {}

    public static Collider simpleGravityObjectCollider() {
        return new SGOCollider();
    }

    public static Collider staticGravityObjectCollider() {
        return new StaticGOCollider();
    }

    public static Collider gravitySystemCollider() {
        return new GravitySystemCollider();
    }

    public static Collider nullCollider() {
        return new NullCollider();
    }

    public static Collider repulsiveCollider() {
        return new RepulsiveCollider();
    }

}
