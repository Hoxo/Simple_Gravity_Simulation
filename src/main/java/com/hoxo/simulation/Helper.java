package com.hoxo.simulation;

import com.hoxo.geometric.Vector2D;

/**
 * Created by Hoxton on 06.09.2017.
 */
class Helper {

    public final static double G = 0.2;

    public static double range(GravityObject from, GravityObject to) {
        return from.point.distance(to.point);
    }

    public static double calculateAcceleration(GravityObject from, GravityObject to) {
        double R = range(from, to);
        double M = to.mass;
        double a = (G * M)/(R*R);
        return a;
    }

    public static Vector2D accelerationVector(GravityObject from, GravityObject to) {
        Vector2D vec = Vector2D.vector(to.point.x - from.point.x, to.point.y - from.point.y);
        double a = calculateAcceleration(from, to);
        vec.setLength(a);
        return vec;
    }

}
