package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;


public class SimpleGravityObject implements GravityObject, Cloneable {
    protected Point center;
    protected Vector2D velocity, acceleration;
    protected double mass;
    protected int radius;
    protected transient Trail trail;
    protected String name = "";
    protected boolean destroyed = false;

    public Trail getTrail() {
        if (trail == null) {
            trail = new Trail(1000);
        }
        return trail;
    }

    public void setTrailLength(int len) {
        trail.setLength(len);
    }

    public SimpleGravityObject(double x, double y, Vector2D velocity, int radius, double mass) {
        trail = new Trail(1000);
        this.center = new Point(x,y);
        this.mass = mass;
        this.velocity = velocity;
        this.acceleration = Vector2D.nullVector();
        this.radius = radius;
    }

    public SimpleGravityObject() {
        trail = new Trail(1000);
    }

    @Override
    public GravityObject clone() {
        return new SimpleGravityObject(center.x, center.y,new Vector2D(velocity.x,velocity.y),radius,mass);
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
    public int getRadius() {
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean containsPoint(double x, double y) {
        return getCenter().distance(x, y) < getRadius();
    }

    @Override
    public void move(double deltaT) {
        double x = getCenter().x,
                y = getCenter().y;
        trail.addPoint(x, y);
        velocity.x += acceleration.x * deltaT;
        velocity.y += acceleration.y * deltaT;

        center.move(deltaT * velocity.x, deltaT * velocity.y);
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
    public double range(GravityObject object) {
        return center.distance(object.getCenter());
    }

    @Override
    public void setSatellite(GravityObject object, double apocentre, double pericentre) {
        object.moveTo(getCenter().x, getCenter().y + apocentre);
        object.getVelocity().x = 1;
        object.getVelocity().y = 0;
        object.getVelocity().setLength(Math.sqrt(Util.G * getMass() / (apocentre + getRadius())));
    }

    @Override
    public void moveTo(double x, double y) {
        getCenter().moveTo(x, y);
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

    public void collide(GravityObject object) {
        if (object instanceof SimpleGravityObject) {
            if (object instanceof Static) {
                ((Static) object).mass += mass;
            } else {
                double massSum = getMass() + object.getMass();
                double e1 = getMass() * Math.pow(getRadius(), 2) / 2;
                double e2 = object.getMass() * Math.pow(object.getRadius(), 2) / 2;

                double _x = (getVelocity().x * getMass() + object.getMass() * object.getVelocity().x) / massSum,
                        _y = (getVelocity().y * getMass() + object.getMass() * object.getVelocity().y) / massSum;
                SimpleGravityObject sgo;
                //Point center = new Point((getCenter().x + object.getCenter().x) / 2,  (getCenter().y + object.getCenter().y) / 2);
                if (e1 > e2) {
                    sgo = this;
                    object.destroy();
                } else {
                    sgo = ((SimpleGravityObject) object);
                    destroy();
                }
                sgo.velocity.x = _x;
                sgo.velocity.y = _y;
//                sgo.center = center;

                double density = sgo.mass / (Math.pow(sgo.radius, 2));

                sgo.mass = massSum;


                sgo.radius = (int) Math.sqrt(massSum / density);
            }
        }
    }

    @Override
    public boolean collideWith(GravityObject object) {
        return range(object) <= getRadius() + object.getRadius();
    }

    @Override
    public String toString() {
        Formatter formatter = new Formatter(Locale.ENGLISH);
        return formatter.format("Gravity object [% .1f, % .1f] Velocity: [% .3f, % .3f] Acceleration: [% .6f, % .6f]",
                center.x, center.y,velocity.x,velocity.y, acceleration.x, acceleration.y).toString();
    }

    public static class Static extends SimpleGravityObject {

        public Static(double x, double y, int radius, double mass) {
            super(x, y, Vector2D.nullVector(), radius, mass);
            trail.setLength(0);
        }

        @Override
        public void interactWith(Collection<? extends GravityObject> objects) {}

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
