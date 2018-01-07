package com.hoxo.simulation;

public class Colliders {
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
