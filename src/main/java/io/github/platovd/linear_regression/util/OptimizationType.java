package io.github.platovd.linear_regression.util;

public enum OptimizationType {
    NO_OPTIMIZATION("Без оптимизации"),
    SGD_MOMENTUM(""),
    GD_MOMENTUM("С инерцией"),
    SGD_NESTEROV_MOMENTUM("Стохастический + метод инерции Нестерова");

    private final String description;

    OptimizationType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
