package com.stlcfib4.gcashapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String phoneNumber;
    private String pin;
    private String fullName;
    private double balance;
}
