module com.javachat {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.javachat to javafx.fxml;
    opens com.javachat.controllers to javafx.fxml;
    opens com.javachat.message to com.google.gson;

    exports com.javachat;
}
