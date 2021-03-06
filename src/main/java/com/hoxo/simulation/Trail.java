package com.hoxo.simulation;

import com.hoxo.geometric.Point;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by Hoxton on 08.09.2017.
 */
public class Trail implements Iterable<Point> {
    private LinkedList<Point> trail;
    private int size;
    public Trail(int size) {
        trail = new LinkedList<>();
        this.size = size;
    }

    public void addPoint(double x, double y) {
        addPoint(new Point(x,y));
    }

    public void addPoint(Point point) {
        if (trail.size() == 0)
            trail.add(point);
        if (trail.size() > 0 && !trail.getLast().equals(point))
            trail.add(point);
        while (trail.size() > size)
            trail.poll();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public Iterator<Point> iterator() {
        return trail.iterator();
    }

    @Override
    public void forEach(Consumer<? super Point> action) {
        trail.forEach(action);
    }

    @Override
    public Spliterator<Point> spliterator() {
        return trail.spliterator();
    }
}
