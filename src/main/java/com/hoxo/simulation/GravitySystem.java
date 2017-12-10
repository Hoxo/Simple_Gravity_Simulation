package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;

/**
 * Created by Hoxo on 10.12.2017.
 */
public class GravitySystem implements GravityObject {

    private Collection<GravityObject> objects;

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public Point getCenter() {
        return null;
    }

    @Override
    public Vector2D getVelocity() {
        return null;
    }

    @Override
    public Vector2D getAcceleration() {
        return null;
    }

    @Override
    public double getGravityImpactRadius(GravityObject other) {
        return 0;
    }

    @Override
    public void interactWith(double deltaT, GravityObject... objects) {

    }

    @Override
    public void interactWith(double deltaT, Collection<GravityObject> objects) {

    }

    @Override
    public boolean collideWith(GravityObject object) {
        return false;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public int getTrailLength() {
        return 0;
    }

    @Override
    public Vector2D accelerationVectorTo(GravityObject object) {
        return null;
    }

    @Override
    public double range(GravityObject object) {
        return 0;
    }

    @Override
    public void setTrailLength(int length) {

    }
}
