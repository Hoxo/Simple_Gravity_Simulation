package com.hoxo.simulation;

import com.hoxo.geometric.Point;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Trail implements Iterable<Point> {
    private LinkedList<Point> trail;
    private int length;

    private Trail() {}

    public Trail(int length) {
        trail = new LinkedList<>();
        this.length = length;
    }

    public void addPoint(double x, double y) {
        addPoint(new Point(x,y));
    }

    public void addPoint(Point point) {
        point = new Point(point);
        if (trail.size() == 0)
            trail.add(point);
        if (trail.size() > 0 && !trail.getLast().equals(point))
            trail.add(point);
        while (trail.size() > length)
            trail.poll();
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return trail.size();
    }

    public Iterator<Point> descendingIterator() {
        return trail.descendingIterator();
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

    public static class Empty extends Trail {

        @Override
        public void addPoint(double x, double y) {
        }

        @Override
        public void addPoint(Point point) {
        }

        @Override
        public void setLength(int length) {
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public Iterator<Point> descendingIterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Iterator<Point> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public void forEach(Consumer<? super Point> action) {}

        @Override
        public Spliterator<Point> spliterator() {
            return null;
        }
    }
}
