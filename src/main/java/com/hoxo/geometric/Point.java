package com.hoxo.geometric;

import java.io.Serializable;

/**
 * Created by Hoxton on 09.09.2017.
 */
public class Point implements Serializable {
    public double x,y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        x = point.x;
        y = point.y;
    }

    public double distance(Point point) {
        return distance(point.x, point.y);
    }

    public void move(double w, double h) {
        x += w;
        y += h;
    }

    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(Vector2D vector) {
        x += vector.x;
        y += vector.y;
    }

    public double distance(double x, double y) {
        double w = this.x - x,
                h = this.y - y;
        return Math.sqrt(w * w + h * h);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        return y != point.y;
    }

    @Override
    public String toString() {
        return "Point: [" + x + ", " + y + "]";
    }
}
