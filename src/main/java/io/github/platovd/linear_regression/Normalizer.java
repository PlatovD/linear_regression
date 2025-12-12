package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Pair;
import io.github.platovd.linear_regression.model.Point;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * Z нормализация. Нормализованное_значение = (исходное_значение - среднее) / стандартное_отклонение
 */
public class Normalizer {
    record NormalizationData(double meanX, double meanY, double stdX, double stdY) {
    }

    private static NormalizationData lastCallData;

    public static List<Point> normalize(List<Point> points) {
        List<Point> normalized = new ArrayList<>();
        updateNormalizationData(points);
        for (Point point : points) {
            Point newPoint = new Point(
                    (point.getX() - lastCallData.meanX) / lastCallData.stdX,
                    (point.getY() - lastCallData.meanY) / lastCallData.stdY
            );
            normalized.add(newPoint);
        }
        return normalized;
    }

    private static void updateNormalizationData(List<Point> points) {
        int n = points.size();
        final double meanX = points.stream().mapToDouble(Point::getX).sum() / n;
        final double meanY = points.stream().mapToDouble(Point::getY).sum() / n;
        double dispersionX = points.stream().mapToDouble(point -> pow(point.getX() - meanX, 2)).sum() / n;
        double dispersionY = points.stream().mapToDouble(point -> pow(point.getY() - meanY, 2)).sum() / n;
        double stdX = sqrt(dispersionX);
        double stdY = sqrt(dispersionY);
        if (stdX < 1e-10) stdX = 1.0;
        if (stdY < 1e-10) stdY = 1.0;
        lastCallData = new NormalizationData(meanX, meanY, stdX, stdY);
    }

    public static Pair<Double, Double> denormalizeParams(
            double kNorm, double bNorm) {

        double kReal = kNorm * (lastCallData.stdY / lastCallData.stdX);
        double bReal = bNorm * lastCallData.stdY + lastCallData.meanY - kReal * lastCallData.meanX;
        return new Pair<>(kReal, bReal);
    }

    public static double normalizeX(double x) {
        return (x - lastCallData.meanX) / lastCallData.stdX;
    }

    public static double denormalizeY(double yNormalized) {
        return yNormalized * lastCallData.stdY + lastCallData.meanY;
    }
}
