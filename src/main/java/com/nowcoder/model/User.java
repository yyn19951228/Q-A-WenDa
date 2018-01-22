package com.nowcoder.model;

public class User {
    private String name;
    private int id;
    private String password;
    private String salt;
    private String headUrl;

    public User() {

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String name) {this.name = name;}
}
