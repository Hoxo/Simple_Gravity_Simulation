package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.io.Serializable;
import java.text.Format;
import java.util.Formatter;
import java.util.Locale;


/**
 * Created by Hoxton on 06.09.2017.
 */
public class GravityObject implements Cloneable, Serializable {
    public Point point;
    public Vector2D velocity, acc;
    public int mass;
    public int radius;
    public boolean isStatic;
    private transient Trail trail;

    public Trail getTrail() {
        if (trail == null) {
            trail = new Trail(1000);
        }
        return trail;
    }

    public void setTrailLength(int len) {
        trail.setSize(len);
    }

    public GravityObject(double x, double y, Vector2D velocity, int radius, int mass, boolean isStatic) {
        trail = new Trail(1000);
        this.point = new Point(x,y);
        this.mass = mass;
        this.velocity = velocity;
        this.acc = Vector2D.nullVector();
        this.radius = radius;
        this.isStatic = isStatic;
    }

    public GravityObject() {
        trail = new Trail(1000);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new GravityObject(point.x,point.y,new Vector2D(velocity.x,velocity.y),radius,mass,isStatic);
    }

    @Override
    public String toString() {
        Formatter formatter = new Formatter(Locale.ENGLISH);
        String result = formatter.format("Gravity object\n[%.1f, %.1f]\nVelocity: [%.3f, %.3f]\nAcceleration: [%.3f, %.3f]",
                point.x,point.y,velocity.x,velocity.y,acc.x,acc.y).toString();
        return result;
    }
}
