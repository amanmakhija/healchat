package com.sandeep.chatassistant.data;

public class Message {

    // string to store our message and sender
    private String message;
    private String sender;
    private String  imageUri;

    // constructor.
    public Message(String message, String sender, String imageUri) {
        this.message = message;
        this.sender = sender;
        this.imageUri = imageUri;
    }

    // getter and setter methods.
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
