package com.example.demo1;

public class LoggedInUser {
    private static int id;
    private static String username;
    private static String email;
    private static String phone;

    public static void setUsername(String username) {
        LoggedInUser.username = username;
    }

    public static void setEmail(String email) {
        LoggedInUser.email = email;
    }

    public static void setPhone(String phone) {
        LoggedInUser.phone = phone;
    }

    public static void setId(int id) {
        LoggedInUser.id = id;
    }

    public static Integer getId() {
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPhone() {
        return phone;
    }

    public static void clear() {
        id = 0;
        username = null;
        email = null;
        phone = null;
    }
}
