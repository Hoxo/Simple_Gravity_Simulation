package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Hoxo on 10.12.2017.
 */
public interface GravityObject extends Destroyable, Serializable, Cloneable {
    double getMass();
    Point getCenter();
    Vector2D getVelocity();
    Vector2D getAcceleration();
    Trail getTrail();
    void setName(String name);
    String getName();
    int getRadius();
    int getTrailLength();

    void moveTo(double x, double y);
    void move(double deltaT);
    void collide(GravityObject object);
    Vector2D accelerationVectorTo(GravityObject object);
    double range(GravityObject object);
    void interactWith(Collection<? extends GravityObject> objects);
    boolean collideWith(GravityObject object);
    void setTrailLength(int length);
    void setSatellite(GravityObject object, double apocentre, double pericentre);

    boolean containsPoint(double x, double y);

    GravityObject clone();
}
