package com.mateus.resweb;

public class HolderChat {
    private String name, message;
    private int imageId;

    public HolderChat(String name, String message , int imageId) {
        this.name = name;
        this.message = message;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }
    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
}