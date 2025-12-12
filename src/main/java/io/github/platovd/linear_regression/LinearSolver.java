package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;

import java.util.List;

import static java.lang.Math.pow;

public class LinearSolver {
    private static final double DELTA = 1e-7;
    private static final double EPS = 1e-6;

    // 4 раза посчитать функцию ошибок, для k + delta, k - delta, b + delta, b - delta.
    public static Pair<Double, Double> gradient(List<Point> points, double k, double b, double delta) {
        double errorKPlusDelta = calculateError(points, k + delta, b);
        double errorKMinusDelta = calculateError(points, k - delta, b);

        double errorBPlusDelta = calculateError(points, k, b + delta);
        double errorBMinusDelta = calculateError(points, k, b - delta);
        return new Pair<>(
                (errorKPlusDelta - errorKMinusDelta) / (2 * delta),
                (errorBPlusDelta - errorBMinusDelta) / (2 * delta)
        );
    }

    public static Pair<Double, Double> analyticGradient(List<Point> points, double k, double b) {
        double errorByX = calculateErrorDerivativeK(points, k, b);
        double errorByY = calculateErrorDerivativeB(points, k, b);
        return new Pair<>(errorByX, errorByY);
    }

    private static double calculateErrorDerivativeK(List<Point> points, double k, double b) {
        int n = points.size();
        double error = 0;
        for (Point point : points) {
            error += (point.getX() * k + b - point.getY()) * point.getX();
        }
        return 2.0 / n * error;
    }

    private static double calculateErrorDerivativeB(List<Point> points, double k, double b) {
        int n = points.size();
        double error = 0;
        for (Point point : points) {
            error += point.getX() * k + b - point.getY();
        }
        return 2.0 / n * error;
    }

    private static double calculateError(List<Point> points, double k, double b) {
        return points.stream()
                .map(point -> pow(point.getX() * k + b - point.getY(), 2))
                .mapToDouble(Double::doubleValue).sum() / points.size();
    }

    public static Pair<Double, Double> gradientDescent(List<Point> points, Pair<Double, Double> initialKB, int iterations, double stepK, double eps) {
        points = Normalizer.normalize(points);
        double k = initialKB.getKey();
        double b = initialKB.getValue();
        for (int i = 0; i < iterations; i++) {
//            Pair<Double, Double> gradient = gradient(points, k, b, DELTA);
            Pair<Double, Double> gradient = analyticGradient(points, k, b);
            if (calcGradLen(gradient) < eps)
                break;
            k -= gradient.getKey() * stepK;
            b -= gradient.getValue() * stepK;
        }
//        return Normalizer.denormalizeParams(k, b);
        return new Pair<>(k, b);
    }


    private static double calcGradLen(Pair<Double, Double> grad) {
        return Math.sqrt(pow(grad.getKey(), 2) + pow(grad.getValue(), 2));
    }


}
