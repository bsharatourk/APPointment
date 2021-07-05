package com.example.appointment.Common;

import com.example.appointment.User;


public class  Common {
    public static String IS_LOGIN = "NotLogged";
    public static User currentUser;
    public static String phone;
    public static boolean LOGGED_IN_FLAG = false;

    public static void setPhone(String phone) {
        Common.phone = phone;
    }

    public static void setCurrentUser(User currentUser) {
        Common.currentUser = currentUser;
    }

    public static void setIsLogin(String isLogin) {
        IS_LOGIN = isLogin;
    }

}
