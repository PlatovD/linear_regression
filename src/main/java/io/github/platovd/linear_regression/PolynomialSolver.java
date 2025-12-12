package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class PolynomialSolver {
    private static final double DELTA = 1e-7;

    public static List<Double> calculateGradient(List<Point> points, List<Double> params) {
        List<Double> gradient = new ArrayList<>();
        for (int i = 0; i < params.size(); i++) {
            double baseValue = params.get(i);
            params.set(i, baseValue + DELTA);
            double errorPlusDelta = calculateError(points, params);
            params.set(i, baseValue - DELTA);
            double errorMinusDelta = calculateError(points, params);
            params.set(i, baseValue);
            gradient.add((errorPlusDelta - errorMinusDelta) / (2 * DELTA));
        }
        return gradient;
    }

    public static double calculateError(List<Point> points, List<Double> params) {
        double error = 0;
        for (Point point : points) {
            double x = point.getX();
            double y = point.getY();
            double prediction = 0;
            for (int i = params.size() - 1; i >= 0; i--) {
                prediction = prediction * x + params.get(i);
            }
            error += pow(prediction - y, 2);
        }
        return error / points.size();
    }

    public static List<Double> gradientDescent(List<Point> points, List<Double> params, int iterations, double step, double eps) {
        int n = params.size();
        points = Normalizer.normalize(points);
        System.out.println(points);
        for (int i = 0; i < iterations; i++) {
            List<Double> gradient = calculateGradient(points, params);
            if (calcNorm(gradient) < eps)
                break;
            for (int j = 0; j < n; j++) {
                params.set(j, params.get(j) - gradient.get(j) * step);
            }

        }
        return params;
    }

    private static double calcNorm(List<Double> vector) {
        double norm = 0;
        for (Double component : vector) {
            norm += pow(component, 2);
        }
        return sqrt(norm);
    }
}
