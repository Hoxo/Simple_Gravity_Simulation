package com.hoxo;

import com.hoxo.geometric.Point;
import com.hoxo.geometric.Vector2D;
import com.hoxo.simulation.*;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.util.StringConverter;

import java.io.*;
import java.util.*;

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
    private CheckBox showTrails;
    @FXML
    private Menu templates;
    @FXML
    private Label radiusLabel;
    @FXML
    private Label coordinates;
    @FXML
    private Label scaleLabel;
    @FXML
    private Label deltaLabel;
    @FXML
    private TextField nameField;

    private static final double VELOCITY_SCALE = 0.02;
    private static final double SHIFT_VALUE = 10;
    private static final double INC_SCALE_VALUE = 1.1;
    private static final double DEC_SCALE_VALUE = 1/INC_SCALE_VALUE;
    private static final String examplesFilename = "examples.txt";

    private List<SnapShot> snapShots = new LinkedList<>();
    private Map<KeyCode, Runnable> keyCommandMap = new HashMap<>();
    private MouseEventAdapter mouseEventAdapter;
    private volatile SimpleDoubleProperty delta;
    private Simulation simulation;
    private AnimationTimer timer;
    private SimpleBooleanProperty isOn;
    private double scale = 1;
    private Vector2D shift = new Vector2D(0,0);

    private class MouseEventAdapter {
        public final EventHandler<? super MouseEvent> mousePressed, mouseReleased, mouseDragged;
        private Point startPosition;
        private MouseEvent startPositionEvent;
        private volatile boolean pressed, creating;
        private Vector2D currentVelocity = Vector2D.nullVector();

        public MouseEventAdapter() {

            mouseDragged = event -> {
                if (pressed) {
                    if (creating) {
                        if (simulation.hasFocused())
                            startPosition = getCursorPosition(startPositionEvent);
                        currentVelocity = getVelocityVector(event);
                        calculateObjectPath(startPosition, currentVelocity);
                    }
                    else
                        if (!simulation.hasFocused()) {
                            Vector2D shift = getVelocityVector(event);
                            shift.scaleLength(1/VELOCITY_SCALE);
                            makeShift(-shift.x, - shift.y);
                        }
                }
            };

            mousePressed = event -> {
                pressed = true;
                startPositionEvent = event;
                startPosition = getCursorPosition(event);
                if (event.getButton() == MouseButton.SECONDARY)
                    creating = true;
                else
                    simulation.focus(startPosition.x, startPosition.y);
            };

            mouseReleased = event -> {
                pressed = false;
                currentVelocity.x = 0;
                currentVelocity.y = 0;
                if (simulation.hasFocused())
                    startPosition = getCursorPosition(startPositionEvent);
                simulation.removePath();
                if (creating) {
                    if (isStatic.isSelected())
                        simulation.addStaticGravityObject(startPosition.x, startPosition.y,(int)radius.getValue());
                    else
                        simulation.addGravityObject(startPosition.x, startPosition.y,getVelocityVector(event),(int)radius.getValue());
                    creating = false;
                }

            };
        }

        private Vector2D getVelocityVector(MouseEvent event) {
            Affine affine = canvas.getGraphicsContext2D().getTransform();
            Vector2D velocity = new Vector2D(VELOCITY_SCALE * (startPosition.x - (event.getX() - affine.getTx())/affine.getMxx()),
                        VELOCITY_SCALE * (startPosition.y - (event.getY() - affine.getTy())/affine.getMyy()));
            if (simulation.hasFocused())
                velocity.add(simulation.getFocused().getVelocity());
            return velocity;
        }
    }

    private void calculateObjectPath(Point start, Vector2D velocity) {
        simulation.calculatePath(start.x, start.y, velocity, radius.getValue(), delta.get());
    }

    public Controller() {
        keyCommandMap.put(KeyCode.MINUS, () -> makeScale(DEC_SCALE_VALUE));
        keyCommandMap.put(KeyCode.EQUALS, () -> makeScale(INC_SCALE_VALUE));
        keyCommandMap.put(KeyCode.RIGHT, () -> makeShift(-SHIFT_VALUE/scale, 0));
        keyCommandMap.put(KeyCode.LEFT, () -> makeShift(SHIFT_VALUE/scale, 0));
        keyCommandMap.put(KeyCode.UP, () -> makeShift(0,SHIFT_VALUE/scale));
        keyCommandMap.put(KeyCode.DOWN, () -> makeShift(0, -SHIFT_VALUE/scale));
        mouseEventAdapter = new MouseEventAdapter();
        delta = new SimpleDoubleProperty(1);
        isOn = new SimpleBooleanProperty(false);
        timer = new AnimationTimer() {
            double time = 0;

            @Override
            public void handle(long now) {
                label.setText(simulation.getObjects().size() + "");
                state.setText("Время: " + ((long)time));
                time += delta.get();
                if (simulation.hasFocused())
                    setCenterTo(simulation.getFocused().getCenter());
                simulation.tick(delta.get());
                redraw(simulation.getObjects());
            }
        };
        simulation = new Simulation(new SimpleGravityObjectFactory());
        experiment();
    }

    private void experiment() {
        double w, h = w = 1000;
        int count = 100;
        for (int i = 0; i < count; i++) {
            simulation.addGravityObject(Math.random()*w, Math.random()*h, Vector2D.nullVector(), 10);
        }
    }

    public void initialize() {
        canvas.getParent().setOnKeyPressed(this::handleKeyEvent);
        readSnapShots();
        deltaSlider.setMin(-2);
        deltaSlider.setMax(2);
        deltaSlider.setBlockIncrement(0.05);
        deltaSlider.setFocusTraversable(false);
        isCollide.setOnAction(event -> deltaSlider.setValue(-deltaSlider.getValue()));
        deltaSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Formatter formatter = new Formatter(Locale.ENGLISH);
            String s = formatter.format("Time: %.2f",newValue).toString();
            deltaLabel.setText(s);
        });
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
        canvas.setOnScroll(event -> {
            if (event.getDeltaY() > 0)
                makeScale(INC_SCALE_VALUE);
            else
                makeScale(DEC_SCALE_VALUE);
        });
        handleStart();
        updateTransformInfo();
    }

    private void handleKeyEvent(KeyEvent event) {
        Runnable command = keyCommandMap.get(event.getCode());
        if (command != null)
            command.run();
        event.consume();
    }

    private void makeScale(double value) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Point center = getCenterOfView();
        gc.scale(value, value);
        setCenterTo(center);
        scale *= value;
        updateTransformInfo();
    }

    private Point getCenterOfView() {
        Affine affine = canvas.getGraphicsContext2D().getTransform();
        return new Point((-affine.getTx() + canvas.getWidth()/2)/affine.getMxx(), (-affine.getTy() + canvas.getHeight()/2)/affine.getMyy());
    }

    private Point getCursorPosition(MouseEvent event) {
        Affine affine = canvas.getGraphicsContext2D().getTransform();
        return new Point((event.getX() - affine.getTx())/affine.getMxx() ,(event.getY() - affine.getTy())/affine.getMyy());
    }

    private void setCenterTo(Point center) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Affine affine = gc.getTransform();
        double halfW = canvas.getWidth()/ (affine.getMxx() * 2),
                halfH = canvas.getHeight() / (affine.getMyy() * 2),
                x0 = -affine.getTx()/affine.getMxx(),
                y0 = -affine.getTy()/affine.getMyy();
        gc.translate(-(center.x - halfW) + x0, -(center.y - halfH) + y0);
        updateTransformInfo();
    }

    private void makeShift(double w, double h) {
        simulation.unfocus();
        shift.x += w;
        shift.y += h;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.translate(w, h);
        updateTransformInfo();
    }

    private void updateTransformInfo() {
        Formatter formatter = new Formatter(Locale.ENGLISH);
        Affine affine = canvas.getGraphicsContext2D().getTransform();
        scaleLabel.setText(formatter.format("Масштаб: %.4f",affine.getMxx()).toString());
        formatter = new Formatter(Locale.ENGLISH);
        double x, y;
        x = affine.getTx()/affine.getMxx();
        y = affine.getTy()/affine.getMyy();
        coordinates.setText(formatter.format("Координаты:\n%.3f %.3f",-x + canvas.getWidth()/scale/2,-y + canvas.getHeight()/scale/2).toString());
    }

    private void readSnapShots() {

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(examplesFilename))){
            for (;;) {
                SnapShot snapShot = (SnapShot) inputStream.readObject();
                snapShots.add(snapShot);
                addMenuItem(snapShot);
            }
        } catch (EOFException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMenuItem(SnapShot snapShot) {
        MenuItem item = new MenuItem(snapShot.name);
        item.setOnAction(event -> simulation.restoreSnapShot(snapShot));
        templates.getItems().add(item);
    }

    private static final Color TRAIL_COLOR = Color.WHITE;
    private static final Color SELECTED_TRAIL_COLOR = Color.GREEN;
    private static final Color CALCULATED_TRAIL_COLOR = Color.PURPLE;
    private static final Color STATIC_OBJECT_COLOR = Color.rgb(200,200,0);
    private static final Color SIMPLE_OBJECT_COLOR = Color.STEELBLUE;

    private void redraw(Collection<? extends GravityObject> collection) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Affine af = gc.getTransform();
        Point begin = beginOfCoordinates();
        gc.clearRect(begin.x,begin.y,canvas.getWidth()/af.getMxx(),canvas.getHeight()/af.getMyy());
        gc.setFill(Color.BLACK);
        gc.fillRect(-af.getTx()/af.getMxx(),-af.getTy()/af.getMyy(),canvas.getWidth()/af.getMxx(),canvas.getHeight()/af.getMyy());
        drawObjects(collection);
        drawInfo();
    }

    private void drawInfo() {
        drawScalablePoint(simulation.centerOfMass(), Color.RED);
        if (mouseEventAdapter.creating) {
            Point pos = simulation.hasFocused() ? getCursorPosition(mouseEventAdapter.startPositionEvent) : mouseEventAdapter.startPosition;
            drawScalablePoint(pos, Color.AQUAMARINE);
        }
    }

    private void drawScalablePoint(Point p, Color fill) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(fill);
        gc.fillOval(p.x - 1/scale, p.y - 1/scale, 1/scale, 1/scale);
    }

    private Point beginOfCoordinates() {
        Affine af = canvas.getGraphicsContext2D().getTransform();
        return new Point(-af.getTx()/af.getMxx(),-af.getTy()/af.getMyy());
    }

    private void drawObjects(Collection<? extends GravityObject> collection) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawTrails(collection);
        for(GravityObject obj : collection) {
            if (obj instanceof GravitySystem)
                drawObjects(((GravitySystem) obj).getGravityObjects());
            else {
                Point point = new Point(obj.getCenter().x, obj.getCenter().y);
                double currentRadius = obj.getRadius();

                if (obj instanceof SimpleGravityObject.Static) {
                    gc.setFill(STATIC_OBJECT_COLOR);
                    gc.fillOval(point.x - currentRadius, point.y - currentRadius, 2 * currentRadius, 2 * currentRadius);
                }
                else {
                    gc.setFill(SIMPLE_OBJECT_COLOR);
                    gc.fillOval(point.x - currentRadius, point.y - currentRadius, 2 * currentRadius, 2 * currentRadius);
                    double scale = 100;
                    gc.strokeLine(obj.getCenter().x, obj.getCenter().y,
                            obj.getCenter().x + obj.getAcceleration().x * scale, obj.getCenter().y + obj.getAcceleration().y * scale);
                }
                gc.setFont(Font.font(8));
                gc.fillText(obj.getName(),(obj.getCenter().x + obj.getRadius() + 5), obj.getCenter().y);
            }
        }
    }

    private void drawTrails(Collection<? extends GravityObject> collection) {
        if (showTrails.isSelected()) {
            for (GravityObject obj : collection)
                drawTrail(obj.getTrail().iterator(), obj.getTrailLength(), TRAIL_COLOR);
            if (simulation.hasFocused())
                drawTrail(simulation.getFocused().getTrail().iterator(), simulation.getFocused().getTrailLength(), SELECTED_TRAIL_COLOR);
        }
        Trail path = simulation.getPath();

        if (path != null)
            drawTrail(path.descendingIterator(), path.getLength(), CALCULATED_TRAIL_COLOR);
    }


    private void drawTrail(Iterator<Point> iterator, int length, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int ctr = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            ctr++;
            gc.setFill(Color.color(color.getRed(),color.getGreen(),color.getBlue(), ctr/(double)length));
            gc.fillOval(point.x - 1 / scale, point.y - 1 / scale, 1 / scale, 1 / scale);
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
    private void dump() {
        System.out.println("_______________INFO_______________");
        int i = 1;
        for (GravityObject o : simulation.getObjects()) {
            System.out.println("#" + i++);
            System.out.println(o);
        }
        System.out.println("IMPULSE: " + simulation.summaryImpulse());
        System.out.println("CENTER OF MASS: " + simulation.centerOfMass());
    }

    @FXML
    private void saveTemplate() {
        if (nameField.getText().isEmpty())
            System.out.println("Введите имя!");
        else {
            for (MenuItem menuItem : templates.getItems())
                if (menuItem.getText().equals(nameField.getText()))
                    return;
            SnapShot snapShot = simulation.makeSnapShot(nameField.getText());
            snapShots.add(snapShot);
            addMenuItem(snapShot);
        }
    }

    public void saveAllTemplates() {

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(examplesFilename))) {
            for (SnapShot snapShot : snapShots) {
                outputStream.writeObject(snapShot);
                System.out.println(snapShot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
