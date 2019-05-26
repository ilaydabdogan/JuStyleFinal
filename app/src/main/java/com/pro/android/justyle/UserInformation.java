package com.pro.android.justyle;

class UserInformation{
    private final String name;
    private final String address;


    public UserInformation(String name, String address){
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
