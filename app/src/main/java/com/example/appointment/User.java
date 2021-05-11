package com.example.appointment;

public class User {


    String fullName;
    String phoneNum;

    public User(String fullName, String phoneNum) {
        this.fullName = fullName;
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
