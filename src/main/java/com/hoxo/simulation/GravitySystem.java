package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hoxo on 10.12.2017.
 */
public class GravitySystem extends GravityObject {

    private GravityObject centerOfSystem;
    private LinkedList<GravityObject> objects = new LinkedList<>();
    private Point centerOfMass = new Point(0,0);
    private double mass = 0;
    private List<GravityObject> lost = new LinkedList<>();
    private Vector2D velocity, acceleration = Vector2D.nullVector();

    GravitySystem() {
        collider = Colliders.gravitySystemCollider();
    }

    public GravitySystem(GravityObject centerOfSystem, Vector2D velocity) {
        this.centerOfSystem = centerOfSystem;
        this.velocity = velocity;
        mass = centerOfSystem.getMass();
        centerOfMass = new Point(centerOfSystem.getCenter());
        objects.addLast(centerOfSystem);
        collider = Colliders.gravitySystemCollider();
    }

    public void add(GravityObject object) {
        objects.addLast(object);
        recalculate();
    }

    public void recalculate() {
        mass = 0;
        for (GravityObject object : objects)
            mass += object.getMass();
        calculateCenter();
    }

    public boolean remove(GravityObject object) {
        boolean result = objects.remove(object);
        recalculate();
        return result;
    }

    public GravityObject removeLast() {
        if (!objects.isEmpty()) {
            GravityObject result = objects.removeLast();
            recalculate();
            return result;
        } else
            return null;
    }

    public int size() {
        return objects.size();
    }

    public void setCenterOfSystem(GravityObject center) {
        objects.remove(centerOfSystem);
        objects.add(center);
        centerOfSystem = center;
        recalculate();
    }

    public GravityObject getCenterOfSystem() {
        return centerOfSystem;
    }

    public void calculateCenter() {
        double xmsum = 0, ymsum = 0;
        for (GravityObject object : objects) {
            xmsum += object.getMass() * object.getCenter().x;
            ymsum += object.getMass() * object.getCenter().y;
        }
        centerOfMass.x = xmsum / getMass();
        centerOfMass.y = ymsum / getMass();
    }

    public LinkedList<GravityObject> getGravityObjects() {
        return objects;
    }


    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Point getCenter() {
        return centerOfSystem.getCenter();
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public Vector2D getAcceleration() {
        return acceleration;
    }

    @Override
    public double getRadius() {
        int maxR = 0;
        for (GravityObject o1 : objects) {
            for (GravityObject o2 : objects) {
                double r = o1.range(o2);
                if (r > maxR)
                    maxR = (int)r;
            }
        }
        return maxR/2;
    }

    @Override
    public void addAccelerationVector(Collection<? extends GravityObject> objects) {
        for (GravityObject object : objects)
            if (object != this && !object.isDestroyed())
                    acceleration.add(accelerationVectorTo(object));

    }

    @Override
    public void collide(GravityObject object) {
        collider.collide(this, object);
    }

    public void removeDestroyed() {
        LinkedList<GravityObject> destroyed = new LinkedList<>();
        for (GravityObject object : objects)
            if (object.isDestroyed())
                destroyed.add(object);
        objects.removeAll(destroyed);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return getCenter().distance(x, y) <= getRadius();
    }


    @Override
    public void move(double deltaT) {
//        velocity.x += acceleration.x * deltaT;
//        velocity.y += acceleration.y * deltaT;
        double w = deltaT * getVelocity().x,
                h = deltaT * getVelocity().y;
        run(deltaT);
        moveTo(getCenter().x + w, getCenter().y + h);
    }

    public List<GravityObject> getAndRemoveLost() {
//        for (GravityObject object : objects) {
//            if (!object.collideWith(this))
//                lost.add(object);
//        }
        objects.removeAll(lost);
        LinkedList<GravityObject> l = new LinkedList<>(lost);
        lost.clear();
        return l;
    }

    public void run(double deltaT) {
        removeDestroyed();
        for (GravityObject object : objects)
            object.interactWith(objects);
        for (GravityObject object : objects) {
            object.addAccelerationVector(objects);
            object.recalculateVelocityVector(deltaT);
        }
        for (GravityObject object : objects) {
            object.move(deltaT);
            if (!collideWith(object))
                lost.add(object);
        }
    }

    public void moveTo(double x, double y) {
        double w = x - getCenter().x,
                h = y - getCenter().y;
        for (GravityObject object : objects) {
            object.getCenter().move(w, h);
        }
    }

    @Override
    public void setSatellite(GravityObject object, double apocentre, double pericentre) {

    }

    @Override
    public boolean collideWith(GravityObject object) {
        // TODO Написать!!!!!
        return object.getCenter().distance(getCenter()) < 150;
    }

    @Override
    public Trail getTrail() {
        return centerOfSystem.getTrail();
    }

    @Override
    public int getTrailLength() {
        return 0;
    }

    @Override
    public GravityObject clone() {
        GravitySystem gs = new GravitySystem(centerOfSystem, new Vector2D(velocity.x, velocity.y));
        gs.centerOfMass = new Point(centerOfMass);
        gs.objects.clear();
        for (GravityObject object : objects)
            gs.objects.addLast(object.clone());
        gs.centerOfSystem = gs.objects.getFirst();
        gs.mass = mass;
        gs.acceleration = new Vector2D(acceleration.x, acceleration.y);

        return gs;
    }

    @Override
    public void setTrailLength(int length) {

    }

    @Override
    public String toString() {
        String s = "System \'" + name + "\' Velocity " + velocity + " Acceleration " + acceleration + " Mass " + mass;
        s += '\n';
        for (GravityObject object : objects) {
            s += "\t" + object + '\n';
        }
        return s;
    }
}
