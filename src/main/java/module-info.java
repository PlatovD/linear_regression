module io.github.platovd.linear_regression {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens io.github.platovd.linear_regression to javafx.fxml;
    exports io.github.platovd.linear_regression.model;
    exports io.github.platovd.linear_regression;
}