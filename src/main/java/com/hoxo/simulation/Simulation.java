package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Simulation {
    private LinkedList<SimpleGravityObject> objects = new LinkedList<>();
    private Trail path;
    private GravityObjectFactory factory;
    private volatile boolean col, acc;
    private Collection<SimpleGravityObject> destroyed = new LinkedList<>();

    private static final double     EPS = 2;
    private static final double     BOUNCE_SCALE = 0.85;
    private static final int        LIMIT_RANGE = 10000;
    private static final int        CALCULATED_PATH_LENGTH = 10000;



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

    public Collection<SimpleGravityObject> getObjects() {
        return objects;
    }

    private void checkForCollisions() {
        for(int i = 0; i < objects.size(); i++)
            for(int k = i + 1; k < objects.size(); k++) {
                SimpleGravityObject o1 = objects.get(i), o2 = objects.get(k);
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
        objects.removeAll(destroyed);
        destroyed.clear();
    }

    private void updateAccelerationVectors() {
        for (SimpleGravityObject o1 : objects) {
            o1.acc = Vector2D.nullVector();
            for (SimpleGravityObject o2 : objects)
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

    private boolean isCollided(SimpleGravityObject o1, SimpleGravityObject o2) {
        return Math.abs(Helper.range(o1, o2) - (o1.radius + o2.radius)) < EPS;
    }

    public void calculatePath(double x, double y, Vector2D v, int r, double delta) {
        List<SimpleGravityObject> staticObjects =
                objects.stream().filter(object -> object.isStatic).collect(Collectors.toList());
        SimpleGravityObject o1 = factory.gravityObject(x,y,v,r);
        int size = CALCULATED_PATH_LENGTH;
        Trail trail = new Trail(size);
        for (int i = 0; i < size; i++) {
            if (isCollided(o1))
                break;
            trail.addPoint(new Point(o1.point));

            o1.acc = Vector2D.nullVector();
            for(SimpleGravityObject stc : staticObjects)
                o1.acc.add(Helper.accelerationVector(o1,stc));
            o1.velocity.x += o1.acc.x * delta;
            o1.velocity.y += o1.acc.y * delta;
            o1.point.move(o1.velocity.x * delta, o1.velocity.y * delta);
        }
        path = trail;
    }

    private boolean isCollided(SimpleGravityObject object) {
        for (SimpleGravityObject go : objects) {
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


    private void calculateBounce(SimpleGravityObject o1, SimpleGravityObject o2) {
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

        o1.velocity.scaleLength(BOUNCE_SCALE);
        o2.velocity.scaleLength(BOUNCE_SCALE);
    }

    private void updateVelocityVectors(double delta) {
        for (SimpleGravityObject obj : objects) {
                obj.velocity.x += obj.acc.x * delta;
                obj.velocity.y += obj.acc.y * delta;
        }
    }

    public void tick(double delta) {
        destroyLostObjects();
        if (col)
            checkForCollisions();
        if (acc)
            updateAccelerationVectors();
        updateVelocityVectors(delta);
        move(delta);
    }

    private void destroyLostObjects() {
        objects.removeIf(object -> object.point.distance(0,0) > LIMIT_RANGE);
    }

    private boolean isIntersect(SimpleGravityObject o1, SimpleGravityObject o2) {
        return Helper.range(o1, o2) < o1.radius + o2.radius;
    }

    private void move(double delta) {
        for (SimpleGravityObject object : objects) {
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
        for (SimpleGravityObject object : objects)
            object.setTrailLength(len);
    }

    public void restoreSnapShot(SnapShot snapShot) {
        objects.clear();
        for (SimpleGravityObject obj : snapShot.getObjects())
            try {
                objects.add((SimpleGravityObject) obj.clone());
            } catch (Exception e) {}
    }

}
