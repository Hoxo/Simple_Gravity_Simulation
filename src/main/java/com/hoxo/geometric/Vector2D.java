package com.hoxo.geometric;

import java.io.Serializable;

/**
 * Created by Hoxton on 06.09.2017.
 */
public class Vector2D implements Serializable {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(com.hoxo.geometric.Point point) {
        this(point.x , point.y);
    }

    public Vector2D(com.hoxo.geometric.Point from, com.hoxo.geometric.Point to) {
        x = to.x - from.x;
        y = to.y - from.y;
    }

    public void add(Vector2D vector) {
        x += vector.x;
        y += vector.y;
    }

    public void sub(Vector2D vector) {
        x -= vector.x;
        y -= vector.y;
    }

    public strictfp double angleWith(Vector2D vector) {
        return Math.abs(getAngle() - vector.getAngle());
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public void reverse() {
        x = -x;
        y = -y;
    }

    public static Vector2D nullVector() {
        return new Vector2D(0,0);
    }

    public static Vector2D vector(double x, double y) {
        return new Vector2D(x,y);
    }

    public static Vector2D vectorR(double angle, double radius) {
        Vector2D vector2D = Vector2D.nullVector();
        vector2D.x = Math.cos(angle)*radius;
        vector2D.y = Math.sin(angle)*radius;
        return vector2D;
    }

    public void setLength(double r) {
        if (x == 0 && y == 0) return;
        double len = length();
        x = x * r / len;
        y = y * r / len;
    }

    public void scaleLength(double scale) {
        setLength(length()*scale);
    }

    public double getAngle() {
        return Math.atan2(y,x);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D) {
            return x == ((Vector2D)obj).x && y == ((Vector2D)obj).y;
        }
        return super.equals(obj);
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(x,y);
    }

    @Override
    public String toString() {
        return "vector: [" + x + ", " + y + "]";
    }
}
