package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Hoxo on 10.12.2017.
 */
public class GravitySystem implements GravityObject {

    private GravityObject centerOfSystem;
    private LinkedList<GravityObject> objects = new LinkedList<>();
    private Point centerOfMass = new Point(0,0);
    private double mass = 0;
    private Vector2D velocity, acceleration = Vector2D.nullVector();
    private boolean destroyed = false;
    private String name = "";

    public GravitySystem(GravityObject centerOfSystem, Vector2D velocity) {
        this.centerOfSystem = centerOfSystem;
        this.velocity = velocity;
        mass = centerOfSystem.getMass();
        centerOfMass = new Point(centerOfSystem.getCenter());
        objects.addLast(centerOfSystem);
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
        this.centerOfSystem = center;
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
        LinkedList<GravityObject> l = new LinkedList<>(objects);
        l.addLast(centerOfSystem);
        return l;
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
    public int getRadius() {
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
    public void interactWith(Collection<? extends GravityObject> objects) {
        if (isDestroyed())
            return;
        acceleration.x = 0;
        acceleration.y = 0;
        for (GravityObject object : objects)
            if (object != this && !object.isDestroyed())
                if (!collideWith(object))
                    acceleration.add(accelerationVectorTo(object));
                else
                    collide(object);
    }

    @Override
    public void collide(GravityObject object) {

    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return getCenter().distance(x, y) <= getRadius();
    }


    @Override
    public void move(double deltaT) {
        velocity.x += acceleration.x * deltaT;
        velocity.y += acceleration.y * deltaT;
        double w = deltaT * getVelocity().x,
                h = deltaT * getVelocity().y;

        run(deltaT);
        moveTo(getCenter().x + w, getCenter().y + h);
    }

    public void run(double deltaT) {
        for (GravityObject object : objects)
            object.interactWith(objects);
        for (GravityObject object : objects)
            object.move(deltaT);

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
        return centerOfSystem.collideWith(object);
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
        double R = range(object);
        double M = object.getMass();
        double a = (Util.G * M)/(R*R);
        Vector2D vec = Vector2D.vector(object.getCenter().x - this.getCenter().x , object.getCenter().y - this.getCenter().y);
        vec.setLength(a);
        return vec;
    }

    @Override
    public GravityObject clone() {
        GravitySystem gs = new GravitySystem(centerOfSystem.clone(), new Vector2D(velocity.x, velocity.y));

        gs.centerOfMass = new Point(centerOfMass);
        gs.objects.clear();
        for (GravityObject object : objects)
            gs.objects.addLast(object.clone());
        gs.mass = mass;
        gs.acceleration = new Vector2D(acceleration.x, acceleration.y);

        return gs;
    }

    @Override
    public double range(GravityObject object) {
        return getCenter().distance(object.getCenter());
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
