module com.javachat {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.javachat to javafx.fxml;
    opens com.javachat.controllers to javafx.fxml;
    exports com.javachat;
}
