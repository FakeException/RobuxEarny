/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.data;

public class Package {
    private String title;
    private int cost;
    private int redeem;
    private int icon;

    public Package(String title, int cost, int redeem, int icon) {
        this.title = title;
        this.cost = cost;
        this.redeem = redeem;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRedeem() {
        return redeem;
    }

    public void setRedeem(int redeem) {
        this.redeem = redeem;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
