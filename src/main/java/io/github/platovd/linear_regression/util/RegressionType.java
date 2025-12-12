package io.github.platovd.linear_regression.util;

public enum RegressionType {
    LINEAR("Линейная"),
    POLYNOMIAL("Полиномиальная");

    private String info;

    RegressionType(String val) {
        info = val;
    }

    @Override
    public String toString() {
        return info;
    }
}
