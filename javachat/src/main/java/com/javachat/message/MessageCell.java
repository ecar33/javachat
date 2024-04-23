package com.javachat.message;

import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

public class MessageCell extends ListCell<Message> {
    private HBox hbox = new HBox();
    private Label textLabel = new Label();
    private Pane spacer = new Pane();

    {
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setStyle("-fx-background-radius: 10;");
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        hbox.getChildren().clear(); // Clear existing children on each update

        if (empty || message == null) {
            setGraphic(null);
        } else {
            textLabel.setText(message.getTextRepresentation());
            textLabel.getStyleClass().add(message.getStyleClass());

            if (message.isSentByUser() == true) {
                hbox.getChildren().addAll(spacer, textLabel);
                HBox.setHgrow(spacer, Priority.ALWAYS);
            } else {
                hbox.getChildren().addAll(textLabel, spacer);
                HBox.setHgrow(spacer, Priority.ALWAYS);
            }

            setGraphic(hbox);
        }


    }
};
