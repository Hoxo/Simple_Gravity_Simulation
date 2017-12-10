package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxton on 06.09.2017.
 */
class Helper {

    public final static double G = 0.2;

    public static double range(SimpleGravityObject from, SimpleGravityObject to) {
        return from.point.distance(to.point);
    }

    public static double calculateAcceleration(SimpleGravityObject from, SimpleGravityObject to) {
        double R = range(from, to);
        double M = to.mass;
        return (G * M)/(R*R);
    }

    public static Vector2D accelerationVector(SimpleGravityObject from, SimpleGravityObject to) {
        Vector2D vec = Vector2D.vector(to.point.x - from.point.x, to.point.y - from.point.y);
        double a = calculateAcceleration(from, to);
        vec.setLength(a);
        return vec;
    }

}
