package com.example.lostandfoundapp;

public class Advert {

    private int id;
    private String postType;
    private String category;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;
    private String imageUri;
    private String createdAt;

    public Advert(int id, String postType, String category, String name, String phone,
                  String description, String date, String location, String imageUri, String createdAt) {
        this.id = id;
        this.postType = postType;
        this.category = category;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getPostType() {
        return postType;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}