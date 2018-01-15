package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.io.Serializable;
import java.util.Collection;

public abstract class GravityObject implements Destroyable, Serializable, Cloneable {
    protected String name = "";
    protected boolean destroyed = false;
    protected transient Collider collider;

    public abstract double getMass();
    public abstract Point getCenter();
    public abstract Vector2D getVelocity();
    public abstract Vector2D getAcceleration();
    public abstract Trail getTrail();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }
    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
    public abstract double getRadius();
    public abstract int getTrailLength();
    public abstract void moveTo(double x, double y);
    public abstract void move(double deltaT);
    public abstract void collide(GravityObject object);
    public Vector2D accelerationVectorTo(GravityObject object) {
        double R = range(object);
        double M = object.getMass();
        double a = (Constants.G * M)/(R*R);
        Vector2D vec = Vector2D.vector(object.getCenter().x - this.getCenter().x , object.getCenter().y - this.getCenter().y);
        vec.setLength(a);
        return vec;
    }
    public double range(GravityObject object) {
        return getCenter().distance(object.getCenter());
    }

    public void interactWith(Collection<? extends GravityObject> objects) {
        if (isDestroyed())
            return;
        for (GravityObject object : objects)
            if (object != this && !object.isDestroyed())
                if (collideWith(object)) {
                    collide(object);
                    if (isDestroyed())
                        return;
                }
    }

    public void setSatellite(GravityObject object, double apocentre, double pericentre) {
        double ellipseA = (apocentre + getRadius() * 2 + pericentre)/2,
                r = apocentre + getRadius(), mu = Constants.G * getMass(),
                v = Math.sqrt(mu * (2/r - 1/ellipseA));

        object.moveTo(getCenter().x, getCenter().y  + getRadius() + apocentre);
        object.getVelocity().x = 1;
        object.getVelocity().y = 0;
        object.getVelocity().setLength(v);
        object.getVelocity().add(getVelocity());
    }

    public void recalculateVelocityVector(double deltaT) {
        getVelocity().x += getAcceleration().x * deltaT;
        getVelocity().y += getAcceleration().y * deltaT;
    }

    public void resetAcceleration() {
        getAcceleration().x = 0;
        getAcceleration().y = 0;
    }

    public abstract void addAccelerationVector(Collection<? extends GravityObject> objects);
    public abstract boolean collideWith(GravityObject object);
    public abstract void setTrailLength(int length);
    public boolean containsPoint(double x, double y) {
        return getCenter().distance(x, y) < getRadius();
    }
    public abstract GravityObject clone();
    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + ": name = \'" + name + "\' | destroyed = " + destroyed +
                " | mass = " + getMass() + " | radius = " + getRadius() + " | velocity = " + getVelocity() +
                " | acceleration = " + getAcceleration() + " | center = " + getCenter();
    }
}
