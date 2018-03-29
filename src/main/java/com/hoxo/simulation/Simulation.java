package com.hoxo.simulation;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Simulation {
    private LinkedList<GravityObject> objects = new LinkedList<>();
    private volatile Trail path;
    private GravityObjectFactory factory;
    private List<GravityObject> lost = new LinkedList<>();
    private GravityObject focused;

    private static int LIMIT_RANGE;
    private static int CALCULATED_PATH_LENGTH;

    static {
        loadConfig();
    }

    public Simulation(GravityObjectFactory factory) {
        this.factory = factory;
    }

    private static void loadConfig() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(new File("config/simulation.cfg")));
            String lr = properties.getProperty("limit_range","-1");
            LIMIT_RANGE = Integer.parseInt(lr);
            String cpl = properties.getProperty("calculated_path_length","1000");
            CALCULATED_PATH_LENGTH = Integer.parseInt(cpl);
        } catch (IOException e) {
            e.printStackTrace();
            setDefaultValues();
        }
    }

    private static void setDefaultValues() {
        LIMIT_RANGE = -1;
        CALCULATED_PATH_LENGTH = 1000;
    }

    public void addGravityObject(double x, double y, Vector2D v, int r) {
        objects.add(factory.gravityObject(x, y, v, r));
    }

    public boolean focus(double x, double y) {
        focused = null;
        for (GravityObject object : objects)
            if (object.containsPoint(x, y)) {
                focused = object;
                break;
            }
        return focused != null;
    }

    public GravityObject getFocused() {
        return focused;
    }

    public boolean hasFocused() {
        return focused != null;
    }

    public void unfocus() {
        focused = null;
    }

    public void addStaticGravityObject(double x, double y, int r) {
        objects.add(factory.staticGravityObject(x,y,r));
    }

    public void add(GravityObject object) {
        objects.addLast(object);
    }

    public LinkedList<GravityObject> getObjects() {
        return objects;
    }

    public void calculatePath(double x, double y, Vector2D v, double r, double delta) {
        SimpleGravityObject object = factory.gravityObject(x,y,v,r);
        Simulation clone = clone();
        clone.add(object);
        int size = CALCULATED_PATH_LENGTH;
        Trail trail = new Trail(size);
        for (int i = 0; i < size; i++) {
            clone.tick(delta);
            if (object.isDestroyed())
                break;
            else
                trail.addPoint(object.center);
        }
        path = trail;
    }

    public Simulation clone() {
        Simulation simulation = new Simulation(factory);
        for (GravityObject object : objects)
            simulation.objects.add(object.clone());
        return simulation;
    }

    public Trail getPath() {
        return path;
    }

    public void removePath() {
        path = null;
    }

    public void tick(double delta) {
        addOutOfGravitySystemsObjects();
        if (delta > 0) {
            doCalculations(delta);
            moveAll(delta);
        } else {
            moveAll(delta);
            doCalculations(delta);
        }
    }

    private void doCalculations(double delta) {
        resetAccelerations();
        calculateInteractions();
        cleanup();
        recalculateAllAccelerationVectors();
        recalculateAllVelocityVectors(delta);
    }

    public void resetAccelerations() {
        for (GravityObject object : objects) {
            object.resetAcceleration();
        }
    }

    private void moveAll(double delta) {
        for (GravityObject object : objects)
            object.move(delta);
    }

    private void calculateInteractions() {
        for (GravityObject object : objects)
            object.interactWith(objects);
    }

    private void recalculateAllVelocityVectors(double delta) {
        for (GravityObject object : objects) {
            object.recalculateVelocityVector(delta);
        }
    }

    private void recalculateAllAccelerationVectors() {
        for (GravityObject object : objects) {
            object.addAccelerationVector(objects);
        }
    }

    private void addOutOfGravitySystemsObjects() {
        lost.clear();
        for (GravityObject object : objects) {
            if (object instanceof GravitySystem)
                lost.addAll(((GravitySystem) object).getAndRemoveLost());
        }
        objects.addAll(lost);
    }

    private void cleanup() {
        LinkedList<GravityObject> destroyed = new LinkedList<>();
        for (GravityObject object : objects)
            if (object.isDestroyed())
                destroyed.add(object);
        objects.removeAll(destroyed);

    }

    public Vector2D summaryImpulse() {
        Vector2D sum = Vector2D.nullVector();
        for (GravityObject object : objects) {
            Vector2D vel = object.getVelocity().clone();
            vel.scaleLength(object.getMass());
            sum.add(vel);
        }
        return sum;
    }

    public Point centerOfMass() {
        Point p = new Point(0,0);
        double xmsum = 0, ymsum = 0, mass = 0;
        for (GravityObject object : objects) {
            mass += object.getMass();
            xmsum += object.getMass() * object.getCenter().x;
            ymsum += object.getMass() * object.getCenter().y;
        }
        p.x = xmsum / mass;
        p.y = ymsum / mass;
        return p;
    }

    private void destroyLostObjects() {
        objects.removeIf(object -> object.getCenter().distance(0,0) > LIMIT_RANGE);
    }

    public void deleteLast() {
        try {
            objects.removeLast();
        } catch (Exception e) {}
    }

    public void deleteAll() {
        objects.clear();
        unfocus();
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
            objects.add(obj.clone());
    }

}
