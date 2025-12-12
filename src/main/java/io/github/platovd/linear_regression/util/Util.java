package io.github.platovd.linear_regression.util;

import io.github.platovd.linear_regression.model.Point;

public class Util {
    public static Point convertFromScreenToDecartAndBack(double x, double y, double screenHeight) {
        return new Point(x, screenHeight - y);
    }
}
