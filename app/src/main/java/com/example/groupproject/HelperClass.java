package com.example.groupproject;

public class HelperClass {

    public String email;
    public String username;
    public String uid;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HelperClass(String email, String username, String uid) {
        this.email = email;
        this.username = username;
        this.uid = uid;
    }

    public HelperClass() {
    }
}