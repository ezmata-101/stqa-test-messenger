package com.ezmata.messenger.model;

public class User {
    private long id;
    private String username;
    private String email;
    private String password;

    public User(long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }
}