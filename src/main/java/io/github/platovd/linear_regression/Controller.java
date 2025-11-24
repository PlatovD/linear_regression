package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Canvas canvas;

    @FXML
    private Button clearButton;

    private Pair<Double, Double> KB_INITIAL;
    private final int ITERATIONS_DEFAULT = 1000000;
    private final double STEP_DEFAULT = 1e-6;
    private final double EPS_DEFAULT = 1e-9;
    private final int POINT_RADIUS = 6;
    private final Color POINT_COLOR = Color.web("#fff");
    private final int AXIS_WIDTH = 5;
    private final Color AXIS_COLOR = Color.web("#000");
    private List<Point> points = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureGC();
        clearButton.setOnAction(
                this::handleClear
        );
        canvas.setOnMouseClicked(
                this::handlePointCreation
        );
        KB_INITIAL = new Pair<>(0d, 0d);
    }

    private void handlePointCreation(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Point point = Util.convertFromScreenToDecartAndBack(x, y, canvas.getHeight());
        points.add(point);
        redraw();
        drawLine();
    }

    private void drawLine() {
        Pair<Double, Double> KB = Solver.gradientDescent(points, KB_INITIAL, ITERATIONS_DEFAULT, STEP_DEFAULT, STEP_DEFAULT * 100000, EPS_DEFAULT);
        double k = KB.getKey();
        double b = KB.getValue();
        Point startPoint = Util.convertFromScreenToDecartAndBack(0, 0 * k + b, canvas.getHeight());
        Point endPoint = Util.convertFromScreenToDecartAndBack(canvas.getWidth(), canvas.getWidth() * k + b, canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        configureGC();
        for (Point point : points)
            drawPoint(point);
    }

    private void drawPoint(Point point) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Point drawable = Util.convertFromScreenToDecartAndBack(point.getX(), point.getY(), canvas.getHeight());
        gc.fillOval(drawable.getX() - POINT_RADIUS / 2d, drawable.getY() - POINT_RADIUS / 2d, POINT_RADIUS, POINT_RADIUS);
    }

    private void handleClear(ActionEvent actionEvent) {
        points.clear();
        redraw();
    }

    private void configureGC() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(POINT_COLOR);
        gc.setStroke(AXIS_COLOR);
        gc.setLineWidth(AXIS_WIDTH);
        gc.strokeLine(0, 0, 0, canvas.getHeight());
        gc.strokeLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight());

        for (int i = 50; i < canvas.getWidth(); i += 50) {
            gc.setFont(Font.font(10));
            gc.fillText(String.valueOf(i), i, canvas.getHeight() - 10);
        }

        for (int i = 50; i < canvas.getHeight(); i += 50) {
            gc.setFont(Font.font(10));
            gc.fillText(String.valueOf(i), 10, canvas.getHeight() - i);
        }
    }
}