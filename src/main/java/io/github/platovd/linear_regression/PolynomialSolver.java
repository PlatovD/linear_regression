package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class PolynomialSolver {
    private static final double DELTA = 1e-7;
    private static final double RHO = 0.9d;

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

    public static List<Double> calculateGradientAnalytic(List<Point> points, List<Double> params) {
        int n = points.size();
        int k = params.size();
        double[] grad = new double[k];
        for (Point point : points) {
            double x = point.getX();
            double y = point.getY();

            double prediction = 0;
            for (int j = params.size() - 1; j >= 0; j--) {
                prediction = prediction * x + params.get(j);
            }
            double err = prediction - y;
            double xPow = 1;
            for (int t = 0; t < k; t++) {
                grad[t] += err * xPow;
                xPow *= x;
            }
        }
        List<Double> gradientNormalized = new ArrayList<>();
        for (double val : grad) {
            gradientNormalized.add(2 * val / n);
        }
        return gradientNormalized;
    }

    public static List<Double> gradientDescent(List<Point> points, List<Double> params, int iterations, double step, double eps) {
        int n = params.size();
        points = Normalizer.normalize(points);
        for (int i = 0; i < iterations; i++) {
            List<Double> gradient = calculateGradientAnalytic(points, params);
            if (calcNorm(gradient) < eps)
                break;
            for (int j = 0; j < n; j++) {
                params.set(j, params.get(j) - gradient.get(j) * step);
            }

        }
        return params;
    }

    public static List<Double> gradientDescentDGMomentum(List<Point> points, List<Double> params, int iterations, double step, double eps) {
        int k = params.size();
        points = Normalizer.normalize(points);
        double[] vx = new double[k];
        for (int i = 0; i < iterations; i++) {
            List<Double> gradient = calculateGradientAnalytic(points, params);
            if (calcNorm(gradient) < eps)
                break;
            for (int j = 0; j < k; j++) {
                vx[j] = vx[j] * RHO + gradient.get(j);
                params.set(j, params.get(j) - vx[j] * step);
            }
        }
        return params;
    }

    public static List<Double> gradientDescentDGNesterovMomentum(List<Point> points, List<Double> params, int epochs, int batchSize, double step, double eps) {
        int k = params.size();
        points = Normalizer.normalize(points);
        double[] v = new double[k];
        List<Double> paramsInFuture = new ArrayList<>(Collections.nCopies(k, 0d));
        for (int i = 0; i < epochs; i++) {
            List<List<Point>> batches = Batcher.createBatches(points, batchSize);
            for (List<Point> batch : batches) {
                for (int j = 0; j < k; j++) {
                    paramsInFuture.set(j, params.get(j) + RHO * v[j]);
                }
                List<Double> gradient = calculateGradientAnalytic(batch, paramsInFuture);
                for (int j = 0; j < k; j++) {
                    double prevV = v[j];
                    v[j] = RHO * prevV - step * gradient.get(j);
                    params.set(j, params.get(j) + v[j]);
                }
            }
            if (calcNorm(calculateGradientAnalytic(points, params)) < eps)
                break;
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
