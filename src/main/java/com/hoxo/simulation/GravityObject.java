package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;

/**
 * Created by Hoxo on 10.12.2017.
 */
public interface GravityObject {
    double getMass();
    Point getCenter();
    Vector2D getVelocity();
    Vector2D getAcceleration();
    Trail getTrail();
    double getGravityImpactRadius(GravityObject other);
    int getTrailLength();

    Vector2D accelerationVectorTo(GravityObject object);
    double range(GravityObject object);
    void interactWith(double deltaT, GravityObject... objects);
    void interactWith(double deltaT, Collection<GravityObject> objects);

    boolean collideWith(GravityObject object);
    void setTrailLength(int length);
}
