package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;
import io.github.platovd.linear_regression.util.OptimizationType;
import io.github.platovd.linear_regression.util.RegressionType;
import io.github.platovd.linear_regression.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    private Canvas canvas;

    @FXML
    private Button clearButton;

    @FXML
    private ChoiceBox<RegressionType> regressionTypeChoiceBox;

    @FXML
    private ChoiceBox<OptimizationType> optimizationTypeChoiceBox;

    @FXML
    private Spinner<Integer> integerSpinner;

    private Pair<Double, Double> KB_INITIAL;
    private List<Double> PARAMS_INITIAL;

    private final int ITERATIONS_DEFAULT = 100000;
    private final int EPOCHS_DEFAULT = 5000;
    private final int DEFAULT_BATCH_SIZE = 32;
    private final double STEP_DEFAULT = 1e-4;
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
        regressionTypeChoiceBox.itemsProperty().getValue().addAll(
                RegressionType.LINEAR,
                RegressionType.POLYNOMIAL
        );
        optimizationTypeChoiceBox.itemsProperty().getValue().addAll(
                OptimizationType.NO_OPTIMIZATION,
                OptimizationType.GD_MOMENTUM,
                OptimizationType.SGD_NESTEROV_MOMENTUM
        );
        integerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));
        integerSpinner.valueProperty().addListener(e -> {
            changeInitialPolynomialParams(integerSpinner.getValue());
            drawGraph();
        });
        optimizationTypeChoiceBox.setValue(OptimizationType.GD_MOMENTUM);
        regressionTypeChoiceBox.setValue(RegressionType.POLYNOMIAL);
        regressionTypeChoiceBox.setOnAction(e -> drawGraph());
        KB_INITIAL = new Pair<>(0d, 0d);
        changeInitialPolynomialParams(integerSpinner.getValue());
    }

    private void changeInitialPolynomialParams(int i) {
        PARAMS_INITIAL = new ArrayList<>(Collections.nCopies(i + 1, 0.0));
    }

    private void handlePointCreation(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Point point = Util.convertFromScreenToDecartAndBack(x, y, canvas.getHeight());
        points.add(point);
        drawGraph();
    }

    private void drawGraph() {
        redraw();
        if (regressionTypeChoiceBox.getValue().equals(RegressionType.LINEAR))
            drawLine();
        else drawPolynomialFunction();
    }

    private void drawLine() {
        Pair<Double, Double> KB = LinearSolver.gradientDescent(points, KB_INITIAL, ITERATIONS_DEFAULT, STEP_DEFAULT, EPS_DEFAULT);
        double k = KB.getKey();
        double b = KB.getValue();
        Point startPoint = Util.convertFromScreenToDecartAndBack(0, Normalizer.denormalizeY(Normalizer.normalizeX(0) * k + b), canvas.getHeight());
        Point endPoint = Util.convertFromScreenToDecartAndBack(canvas.getWidth(), Normalizer.denormalizeY(Normalizer.normalizeX(canvas.getWidth()) * k + b), canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }

    private void drawPolynomialFunction() {
        double height = canvas.getHeight();
        List<Double> params = null;
        switch (optimizationTypeChoiceBox.getValue()) {
            case GD_MOMENTUM -> {
                params = PolynomialSolver.gradientDescentDGMomentum(points, PARAMS_INITIAL, ITERATIONS_DEFAULT, STEP_DEFAULT, EPS_DEFAULT);
            }
            case NO_OPTIMIZATION -> {
                params = PolynomialSolver.gradientDescent(points, PARAMS_INITIAL, ITERATIONS_DEFAULT, STEP_DEFAULT, EPS_DEFAULT);
            }
            case SGD_NESTEROV_MOMENTUM -> {
                params = PolynomialSolver.gradientDescentDGNesterovMomentum(points, PARAMS_INITIAL, EPOCHS_DEFAULT, DEFAULT_BATCH_SIZE, STEP_DEFAULT, EPS_DEFAULT);
            }
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.beginPath();
        for (int x = 0; x < canvas.getWidth(); x++) {
            double y = height - Normalizer.denormalizeY(calcPolynomialFunctionValue(params, Normalizer.normalizeX(x)));
            gc.lineTo(x, y);
        }
        gc.stroke();
    }

    /**
     * Прикольная штука - схема Горнера
     */
    private double calcPolynomialFunctionValue(List<Double> params, double x) {
        double val = 0;
        for (int i = params.size() - 1; i >= 0; i--) {
            val = val * x + params.get(i);
        }
        return val;
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