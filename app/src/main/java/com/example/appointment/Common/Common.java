package com.example.appointment.Common;

import android.content.Intent;

import com.example.appointment.Model.Salon;
import com.example.appointment.User;


public class  Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KET_SALON_STORE = "SALON_SAVE";
    public static String IS_LOGIN = "NotLogged";
    public static User currentUser;
    public static String phone="0000000000";
    public static String name;
    public static boolean LOGGED_IN_FLAG = false;
    public static Salon currentSalon;
    public static int step = 0; // init at first should be 0

    public static void setPhone(String phone) {
        Common.phone = phone;
    }
    public static void setName(String name) {
        Common.name = name;
    }


    public static void setCurrentUser(User currentUser) {
        Common.currentUser = currentUser;
    }

    public static void setIsLogin(String isLogin) {
        IS_LOGIN = isLogin;
    }

}
