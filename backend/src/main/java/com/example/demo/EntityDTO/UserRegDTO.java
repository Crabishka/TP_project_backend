package com.example.demo.EntityDTO;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;

public class UserRegDTO {
    private String phoneNumber;
    private String password;
    private String name;

    public UserRegDTO(String phoneNumber, String password, String name) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
    }

    public User toUser(){
        return User.builder().phoneNumber(phoneNumber).password(password).name(name).build();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
