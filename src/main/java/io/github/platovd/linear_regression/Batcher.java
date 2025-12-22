package io.github.platovd.linear_regression;

import io.github.platovd.linear_regression.model.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Batcher {
    public static List<List<Point>> createBatches(List<Point> points, int batchSize) {
        List<Point> shuffledPoints = new ArrayList<>(points);
        Collections.shuffle(shuffledPoints);

        List<List<Point>> batches = new ArrayList<>();
        for (int i = 0; i < shuffledPoints.size(); i += batchSize) {
            int end = Math.min(i + batchSize, shuffledPoints.size());
            batches.add(shuffledPoints.subList(i, end));
        }
        return batches;
    }
}
