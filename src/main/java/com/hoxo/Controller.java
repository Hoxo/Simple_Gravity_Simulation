package com.hoxo;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.*;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 * Created by Hoxton on 07.09.2017.
 */
public class Controller {

    @FXML
    private Canvas canvas;
    @FXML
    private Slider radius;
    @FXML
    private CheckBox isCollide;
    @FXML
    private Label label;
    @FXML
    private CheckBox isStatic;
    @FXML
    private Label state;
    @FXML
    private Slider deltaSlider;
    @FXML
    private CheckBox isAcc;
    @FXML
    private CheckBox showTrails;
    @FXML
    private Menu templates;
    @FXML
    private Label radiusLabel;
    @FXML
    private TextField trailLength;
    @FXML
    private Slider scaleSlider;

    private static final double SCALE = 0.02;

    private MouseEventAdapter mouseEventAdapter;
    private volatile SimpleDoubleProperty delta;
    private Simulation simulation;
    private AnimationTimer timer;
    private SimpleBooleanProperty isOn;

    private class MouseEventAdapter {
        public final EventHandler<? super MouseEvent> mousePressed, mouseReleased, mouseDragged;
        private Point2D point;
        private volatile boolean pressed;

        public MouseEventAdapter() {

            mouseDragged = event -> {
                if (pressed) {
                    simulation.calculatePath(point.getX(),point.getY(),getVelocityVector(event), (int)radius.getValue(), delta.get());
                }
            };
            mousePressed = event -> {
                pressed = true;
                double scaleValue = scaleSlider.getValue();
                point = new Point2D(event.getX()*scaleValue,event.getY()*scaleValue);
            };
            mouseReleased = event -> {
                pressed = false;
                simulation.removePath();
                if (isStatic.isSelected())
                    simulation.addStaticGravityObject(point.getX(),point.getY(),(int)radius.getValue());
                else
                    simulation.addGravityObject(point.getX(),point.getY(),getVelocityVector(event),(int)radius.getValue());
            };
        }

        private Vector2D getVelocityVector(MouseEvent event) {
            return new Vector2D(SCALE * (point.getX() - event.getX() * scaleSlider.getValue()),
                        SCALE * (point.getY() - event.getY()*scaleSlider.getValue()));

        }
    }

    public Controller() {
        mouseEventAdapter = new MouseEventAdapter();
        delta = new SimpleDoubleProperty(1);
        isOn = new SimpleBooleanProperty(false);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                label.setText(simulation.getObjects().size() + "");
                simulation.tick(delta.get());
                draw(simulation);
            }
        };
        simulation = new Simulation(new SimpleGravityObjectFactory());
    }

    public void initialize() {
        readSnapShots();
        deltaSlider.setMin(0);
        deltaSlider.setMax(4);
        deltaSlider.setBlockIncrement(0.05);
        deltaSlider.setValue(1);
        delta.bind(deltaSlider.valueProperty());
        canvas.setOnKeyTyped(event -> {
            if (event.getCode().equals(KeyCode.S)){
                if (isOn.get())
                    handleStop();
                else
                    handleStart();
            }
        });
        state.textProperty().bindBidirectional(isOn, new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean object) {
                return object ? "ON" : "OFF";
            }

            @Override
            public Boolean fromString(String string) {
                return "ON".equals(string) ? Boolean.TRUE : Boolean.FALSE;
            }
        });
        radiusLabel.textProperty().bindBidirectional(radius.valueProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.intValue() + "";
            }

            @Override
            public Number fromString(String string) {
                return new Double(string);
            }
        });
        canvas.setOnMousePressed(mouseEventAdapter.mousePressed);
        canvas.setOnMouseReleased(mouseEventAdapter.mouseReleased);
        canvas.setOnMouseDragged(mouseEventAdapter.mouseDragged);
        handleStart();
    }

    private void readSnapShots() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("templates"))){
            int c = 0;
            for(;;){
                SnapShot snapShot = (SnapShot) inputStream.readObject();
                MenuItem item = new MenuItem("#" + (c+1) + " (" + snapShot.name + ")" );
                item.setOnAction(event -> {
                    simulation.restoreSnapShot(snapShot);
                });
                templates.getItems().add(item);
                c++;
            }
        } catch (Exception e) {}
    }

    @FXML
    private void setCollision() {
        simulation.setCollision(isCollide.isSelected());
    }

    @FXML
    private void setAcceleration() {
        simulation.setAcceleration(isAcc.isSelected());
    }

    private void drawTrail(Trail trail) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double scaleValue = scaleSlider.getValue();
        for (Point point : trail)
            gc.fillOval(point.x / scaleValue, point.y / scaleValue ,1, 1);
    }

    private void draw(Simulation simulation) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(GravityObject obj : simulation.getObjects()) {
            gc.setFill(Color.BLACK);
            if (showTrails.isSelected()) {
                if (!obj.isStatic)
                    drawTrail(obj.getTrail());
                Trail path = simulation.getPath();
                if (path != null)
                    drawTrail(path);
            }
            int radius = obj.radius;
            double scaleValue = scaleSlider.getValue();
            Point point = new Point(obj.point.x / scaleValue , obj.point.y / scaleValue);
            double currentRadius = obj.radius / scaleValue;

            if (obj.isStatic)
                gc.fillOval(point.x - currentRadius,point.y - currentRadius, 2 * currentRadius, 2 * currentRadius);
            else {
                gc.setFill(Color.STEELBLUE);
                gc.fillOval(point.x - currentRadius,point.y - currentRadius, 2 * currentRadius, 2 * currentRadius);
                double scale =  100 / scaleValue;
                gc.strokeLine(obj.point.x / scaleValue,obj.point.y / scaleValue,
                        obj.point.x / scaleValue + obj.acc.x * scale, obj.point.y / scaleValue + obj.acc.y * scale);
            }
        }
    }

    @FXML
    private void handleStart() {
        if (!isOn.get()) {
            timer.start();
            isOn.set(true);
        }
    }

    @FXML
    private void deleteLast() {
        simulation.deleteLast();
    }

    @FXML
    private void deleteAll() {
        simulation.deleteAll();
    }

    @FXML
    private void handleStop() {
        if (isOn.get()) {
            timer.stop();
            isOn.set(false);
        }
    }

    @FXML
    private void changeTrailLength() {
        try {
            int size = Integer.parseInt(trailLength.getText());
            simulation.changeTrailsLength(size);
        } catch (Exception e) {}
        System.gc();
    }

    @FXML
    private void dump() {
        System.out.println("_______________DUMP_______________");
        int i = 1;
        for (GravityObject o : simulation.getObjects()) {
            System.out.println("#" + i++);
            System.out.println(o);
        }
    }
}
