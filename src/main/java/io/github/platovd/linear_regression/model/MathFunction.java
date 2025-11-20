package io.github.platovd.linear_regression.model;

@FunctionalInterface
public interface MathFunction {
    double calculate(double... vars);
}
