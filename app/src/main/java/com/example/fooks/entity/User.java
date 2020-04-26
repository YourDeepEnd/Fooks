package com.example.fooks.entity;

public class User {
    int id;
    String username;
    String password;
    String nickname;
    String usersex;
    String userresume;

    public String getUsersex() {
        return usersex;
    }

    public void setUsersex(String usersex) {
        this.usersex = usersex;
    }

    public String getUserresume() {
        return userresume;
    }

    public void setUserresume(String userresume) {
        this.userresume = userresume;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {
    }

    public User(int id, String username, String password, String nickname, String usersex, String userresume) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.usersex = usersex;
        this.userresume = userresume;
    }
    public User( String username, String password, String nickname, String usersex, String userresume) {

        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.usersex = usersex;
        this.userresume = userresume;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", usersex='" + usersex + '\'' +
                ", userresume='" + userresume + '\'' +
                '}';
    }
}
