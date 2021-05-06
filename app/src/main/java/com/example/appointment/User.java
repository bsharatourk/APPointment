package com.example.appointment;

public class User {


    String fullName;
    String phoneNum;
    String userEmail;

    public User(String fullName,String userEmail, String phoneNum) {
        this.userEmail = userEmail;
        this.fullName = fullName;
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return fullName;
    }

    public  String getUserEmail(){
        return userEmail;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

}
