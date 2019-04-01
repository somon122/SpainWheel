package com.example.user.cashearingapp;

public class AccountInfo {

    private String userId;
    private String userName;
    private String userPassword;
    private String birthDay;

    public AccountInfo(String userId, String userName, String userPassword, String birthDay) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.birthDay = birthDay;
    }

    public AccountInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
}
