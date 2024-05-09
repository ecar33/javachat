package com.javachat.message;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

/**
 * A custom ListCell for displaying messages in a ListView.
 * This class customizes the cell to handle the display of messages based on their type (sent or received).
 */
public class MessageCell extends ListCell<Message> {
    private HBox hbox = new HBox();
    private Label textLabel = new Label();
    private Pane spacer = new Pane();

    // Initializer block to set up the HBox styling and layout
    {
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setStyle("-fx-background-radius: 10;");
    }

    /**
     * Updates the item inside the cell to display a message with specific formatting.
     * This method is called automatically whenever the item in the cell changes.
     * @param message The message to be displayed, or null if the cell is empty.
     * @param empty A boolean flag that is true if the cell is empty (no message to display).
     */
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        hbox.getChildren().clear(); // Clear existing children to avoid duplication

        if (empty || message == null) {
            setGraphic(null); // Don't display anything if the cell is empty
        } else {
            textLabel.setText(message.getTextRepresentation()); // Set the text of the message
            textLabel.getStyleClass().add(message.getStyleClass()); // Apply CSS styling

            // Determine the alignment of the text within the HBox based on whether the message was sent by the user
            if (message.isSentByUser() == true) {
                hbox.getChildren().addAll(spacer, textLabel); // Align right
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push the text to the right
            } else {
                hbox.getChildren().addAll(textLabel, spacer); // Align left
                HBox.setHgrow(spacer, Priority.ALWAYS); // Push the text to the left
            }

            setGraphic(hbox); // Set the HBox as the graphic of the cell
        }
    }
}
