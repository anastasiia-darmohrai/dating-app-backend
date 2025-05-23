package com.lab1.datingapp.dto;

public class AuthenticationRequest {
    private String password;
    private String username;


    public AuthenticationRequest(String name, String password) {
        this.username = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
