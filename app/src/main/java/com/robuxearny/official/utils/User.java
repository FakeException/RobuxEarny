package com.robuxearny.official.utils;

public class User {

    private String uid;
    private int coins;

    public User(String uid, int coins) {

        this.uid = uid;
        this.coins = coins;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
