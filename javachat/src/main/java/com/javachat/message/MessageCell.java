package com.javachat.message;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;


public class MessageCell extends ListCell<Message>{
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(message.getTextRepresentation());
            setStyle(message.getStyleClass());
            setBackground(Background.EMPTY);
        }
    }
    
}
