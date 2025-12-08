package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Solver {
    private static final double DELTA = 1e-7;
    private static final double EPS = 1e-6;
    private static double sumX;
    private static double sumY;
    private static double stdX;
    private static double stdY;

    private static List<Point> normalize(List<Point> points) {
        List<Point> normalized = new ArrayList<>();
        int n = points.size();
        sumX = points.stream().mapToDouble(Point::getX).sum() / n;
        sumY = points.stream().mapToDouble(Point::getY).sum() / n;
        double dispersionX = points.stream().mapToDouble(point -> pow(point.getX() - sumX, 2)).sum() / n;
        double dispersionY = points.stream().mapToDouble(point -> pow(point.getY() - sumY, 2)).sum() / n;
        stdX = sqrt(dispersionX);
        stdY = sqrt(dispersionY);
        if (stdX < 1e-10) stdX = 1.0;
        if (stdY < 1e-10) stdY = 1.0;
        for (Point point : points) {
            Point newPoint = new Point(
                    (point.getX() - sumX) / stdX,
                    (point.getY() - sumY) / stdY
            );
            normalized.add(newPoint);
        }
        return normalized;
    }

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

    public static Pair<Double, Double> gradientDescent(List<Point> points, Pair<Double, Double> initialKB,
                                                       int iterations, double stepK, double stepB, double eps) {
        points = normalize(points);
        double k = initialKB.getKey();
        double b = initialKB.getValue();
        for (int i = 0; i < iterations; i++) {
//            Pair<Double, Double> gradient = gradient(points, k, b, DELTA);
            Pair<Double, Double> gradient = analyticGradient(points, k, b);
            if (calcGradLen(gradient) < eps)
                break;
            k -= gradient.getKey() * stepK;
            b -= gradient.getValue() * stepK;
            System.out.println(k + " " + b);
        }
        return denormalizeParams(k, b);
    }


    private static double calcGradLen(Pair<Double, Double> grad) {
        return Math.sqrt(pow(grad.getKey(), 2) + pow(grad.getValue(), 2));
    }

    private static Pair<Double, Double> denormalizeParams(
            double kNorm, double bNorm) {

        double kReal = kNorm * (stdY / stdX);
        double bReal = bNorm * stdY + sumY - kReal * sumX;

        return new Pair<>(kReal, bReal);
    }
}
