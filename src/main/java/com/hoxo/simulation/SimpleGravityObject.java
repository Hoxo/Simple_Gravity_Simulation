package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;


public class SimpleGravityObject extends GravityObject {
    protected Point center;
    protected Vector2D velocity, acceleration;
    protected double mass;
    protected double radius;
    protected transient Trail trail = new Trail(1000);

    public Trail getTrail() {
        if (trail == null) {
            trail = new Trail(1000);
        }
        return trail;
    }

    public void setTrailLength(int len) {
        trail.setLength(len);
    }

    public SimpleGravityObject(double x, double y, Vector2D velocity, double radius, double mass) {
        trail = new Trail(1000);
        this.center = new Point(x,y);
        this.mass = mass;
        this.velocity = velocity;
        this.acceleration = Vector2D.nullVector();
        this.radius = radius;
//        collider = Colliders.simpleGravityObjectCollider();
        collider = new OffCollider();

    }

    public SimpleGravityObject() {
        trail = new Trail(1000);
        collider = Colliders.simpleGravityObjectCollider();
    }

    @Override
    public GravityObject clone() {
        SimpleGravityObject sgo = new SimpleGravityObject(center.x, center.y,new Vector2D(velocity.x,velocity.y),radius,mass);
        sgo.name = name;
//        System.out.println(trail);
//        sgo.trail = new Trail(trail.getLength());
//        for (Point point : trail) {
//            sgo.trail.addPoint(point);
//        }
        return sgo;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Point getCenter() {
        return center;
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
        return radius;
    }

    @Override
    public int getTrailLength() {
        return trail.getLength();
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
    public void move(double deltaT) {
        double x = getCenter().x,
                y = getCenter().y;
//        recalculateVelocityVector(deltaT);
        trail.addPoint(x, y);
        center.move(deltaT * velocity.x, deltaT * velocity.y);
    }

    @Override
    public void moveTo(double x, double y) {
        getCenter().moveTo(x, y);
    }

    @Override
    public void recalculateAccelerationVector(Collection<? extends GravityObject> objects) {
        acceleration.x = 0;
        acceleration.y = 0;
        for (GravityObject object : objects)
            if (object != this && !object.isDestroyed())
                acceleration.add(accelerationVectorTo(object));

    }

    public void collide(GravityObject object) {
        collider.collide(this, object);
    }

    @Override
    public boolean collideWith(GravityObject object) {
        return range(object) <= getRadius() + object.getRadius();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class Static extends SimpleGravityObject {

        Static() {
            collider = Colliders.staticGravityObjectCollider();
            trail = new Trail(1000);
        }

        public Static(double x, double y, double radius, double mass) {
            super(x, y, Vector2D.nullVector(), radius, mass);
            collider = Colliders.staticGravityObjectCollider();
            trail.setLength(0);
        }

        @Override
        public void interactWith(Collection<? extends GravityObject> objects) {}

        @Override
        public void recalculateAccelerationVector(Collection<? extends GravityObject> objects) {}

        @Override
        public Vector2D getVelocity() {
            return Vector2D.nullVector();
        }

        @Override
        public Vector2D getAcceleration() {
            return Vector2D.nullVector();
        }

        @Override
        public int getTrailLength() {
            return 0;
        }

        @Override
        public Trail getTrail() {
            return null;
        }

        @Override
        public void setTrailLength(int len) {}

        @Override
        public void move(double deltaT) {}

        @Override
        public GravityObject clone() {
            Static so = new Static(center.x, center.y, radius, mass);
            so.name = name;
            return so;
        }

    }

}
