package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;

import java.util.List;

public class Solver {
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

    private static double calculateError(List<Point> points, double k, double b) {
        return points.stream()
                .map(point -> Math.pow(point.getX() * k + b - point.getY(), 2))
                .mapToDouble(Double::doubleValue).sum() / points.size();
    }

    public static Pair<Double, Double> gradientDescent(List<Point> points, Pair<Double, Double> initialKB,
                                                       int iterations, double stepK,double stepB, double eps) {
        double k = initialKB.getKey();
        double b = initialKB.getValue();
        for (int i = 0; i < iterations; i++) {
            Pair<Double, Double> gradient = gradient(points, k, b, DELTA);
            if (calcGradLen(gradient) < eps)
                break;
            k -= gradient.getKey() * stepK;
            b -= gradient.getValue() * stepB;
            System.out.println(k + " " + b);
        }
        return new Pair<>(k, b);
    }
    

    private static double calcGradLen(Pair<Double, Double> grad) {
        return Math.sqrt(Math.pow(grad.getKey(), 2) + Math.pow(grad.getValue(), 2));
    }
}
