package com.ui.g5.hores;

public class User {
    private int Id;
    private String UserName;
    private  String Email;
    private  String Password;
    private String Phone;

    public User(int id, String userName, String email, String password, String phone) {
        Id = id;
        UserName = userName;
        Email = email;
        Password = password;
        Phone = phone;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
