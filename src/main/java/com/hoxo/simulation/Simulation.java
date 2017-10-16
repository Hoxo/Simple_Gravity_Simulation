package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by Hoxton on 06.09.2017.
 */
public class Simulation {
    private LinkedList<GravityObject> objects = new LinkedList<>();
    private Trail path;
    private GravityObjectFactory factory;
    private volatile boolean col, acc;
    private Collection<GravityObject> destroyed = new LinkedList<>();

    private static final double EPS = 2;

    public Simulation(GravityObjectFactory factory) {
        this.factory = factory;
    }

    public void addGravityObject(double x, double y, Vector2D v, int r) {
        objects.add(factory.gravityObject(x, y, v, r));
    }

    public void addStaticGravityObject(double x, double y, int r) {
        objects.add(factory.staticGravityObject(x,y,r));
    }

    public void setCollision(boolean v) {
        col = v;
    }

    public void setAcceleration(boolean v) {
        acc = v;
    }

    public Collection<GravityObject> getObjects() {
        return objects;
    }

    private void checkForCollisions() {
        for(int i = 0; i < objects.size(); i++)
            for(int k = i + 1; k < objects.size(); k++) {
                GravityObject o1 = objects.get(i), o2 = objects.get(k);
                double constr = o1.radius + o2.radius;
                double range = Helper.range(o1, o2);
                if (range <= constr + EPS) {
                    if (o1.isStatic || o2.isStatic) {
                        destroyed.add(o1.isStatic ? o2 : o1);
                    } else {
                        if (range > constr)
                            if (!(o1.velocity.angleWith(new Vector2D(o1.point,o2.point)) > Math.PI/2 &&
                                    o2.velocity.angleWith(new Vector2D(o2.point,o1.point)) > Math.PI/2))
                                calculateBounce(o1, o2);

                    }
                }
            }
        for(GravityObject object : destroyed)
            objects.remove(object);
        destroyed.clear();
    }

    private void updateAccelerationVectors() {
        for (GravityObject o1 : objects) {
            o1.acc = Vector2D.nullVector();
            for (GravityObject o2 : objects)
                if (o1 != o2 && !o1.isStatic) {
                    Vector2D vector = Helper.accelerationVector(o1, o2);
                    if (isIntersect(o1,o2)) {
                        vector.reverse();
                        o1.acc.add(vector);

                    }
                    else
                    if (!isCollided(o1, o2)) {
                        o1.acc.add(vector);
                    }
                }
        }
    }

    private boolean isCollided(GravityObject o1,GravityObject o2) {
        if (Math.abs(Helper.range(o1,o2) - (o1.radius + o2.radius)) < EPS)
            return true;
        else
            return false;
    }

    public void calculatePath(double x, double y, Vector2D v, int r, double delta) {
        List<GravityObject> staticObjects =
                objects.stream().filter(object -> object.isStatic).collect(Collectors.toList());
        GravityObject o1 = factory.gravityObject(x,y,v,r);
        int size = 10000;
        Trail trail = new Trail(size);
        for (int i = 0; i < size; i++) {
            if (isCollided(o1))
                break;
            trail.addPoint(new Point(o1.point));

            o1.acc = Vector2D.nullVector();
            for(GravityObject stc : staticObjects)
                o1.acc.add(Helper.accelerationVector(o1,stc));
            o1.velocity.x += o1.acc.x * delta;
            o1.velocity.y += o1.acc.y * delta;
            o1.point.move(o1.velocity.x * delta, o1.velocity.y * delta);
        }
        path = trail;
    }

    private boolean isCollided(GravityObject object) {
        for (GravityObject go : objects) {
            if (go != object) {
                double range = Helper.range(object, go);
                if (go.radius + object.radius >= range)
                    return true;
            }
        }
        return false;
    }

    public Trail getPath() {
        return path;
    }

    public void removePath() {
        path = null;
    }


    private void calculateBounce(GravityObject o1, GravityObject o2) {
        Vector2D vector2 = new Vector2D(o1.point,o2.point);
        double theta1 = o1.velocity.getAngle(),
                theta2 = o2.velocity.getAngle(),
                phi = vector2.getAngle(),
                v1 = o1.velocity.length(),
                v2 = o2.velocity.length(),
                m1 = o1.mass,
                m2 = o2.mass;
        o1.velocity.x = (v1 * Math.cos(theta1 - phi) * (m1 - m2) + 2 * m2 * v2 * Math.cos(theta2 - phi))*Math.cos(phi)/
                (m1 + m2) + v1*Math.sin(theta1 - phi)*Math.cos(phi + Math.PI/2);
        o1.velocity.y = (v1 * Math.cos(theta1 - phi) * (m1 - m2) + 2 * m2 * v2 * Math.cos(theta2 - phi))*Math.sin(phi)/
                (m1 + m2) + v1*Math.sin(theta1 - phi)*Math.sin(phi + Math.PI/2);
        o2.velocity.x = (v2 * Math.cos(theta2 - phi) * (m2 - m1) + 2 * m1 * v1 * Math.cos(theta1 - phi))*Math.cos(phi)/
                (m1 + m2) + v2*Math.sin(theta2 - phi)*Math.cos(phi + Math.PI/2);
        o2.velocity.y = (v2 * Math.cos(theta2 - phi) * (m2 - m1) + 2 * m1 * v1 * Math.cos(theta1 - phi))*Math.sin(phi)/
                (m1 + m2) + v2*Math.sin(theta2 - phi)*Math.sin(phi + Math.PI/2);
        double scale = 0.95;
        o1.velocity.scaleLength(scale);
        o2.velocity.scaleLength(scale);
    }

    private void updateVelocityVectors(double delta) {
        for (GravityObject obj : objects) {
                obj.velocity.x += obj.acc.x * delta;
                obj.velocity.y += obj.acc.y * delta;
        }
    }

    public void tick(double delta) {
        if (col)
            checkForCollisions();
        if (acc)
            updateAccelerationVectors();
        updateVelocityVectors(delta);
        move(delta);
    }

    private boolean isIntersect(GravityObject o1, GravityObject o2) {
        if (Helper.range(o1,o2) < o1.radius + o2.radius + EPS)
            return true;
        else
            return false;

    }

    private void move(double delta) {
        for (GravityObject object : objects) {
            double x = object.point.x,
                    y = object.point.y;
            object.getTrail().addPoint(x, y);
            object.point.move(delta * object.velocity.x, delta * object.velocity.y);
        }
    }

    //TODO Timer не простит
    public void deleteLast() {
        try {
            objects.removeLast();
        } catch (Exception e) {}
    }

    public void deleteAll() {
        objects.clear();
    }

    public SnapShot makeSnapShot(String name) {
        return new SnapShot(objects, name);
    }

    public void changeTrailsLength(int len) {
        factory.setTrailLength(len);
        for (GravityObject object : objects)
            object.setTrailLength(len);
    }

    public void restoreSnapShot(SnapShot snapShot) {
        objects.clear();
        for (GravityObject obj : snapShot.getObjects())
            try {
                objects.add((GravityObject) obj.clone());
            } catch (Exception e) {}
    }

}
