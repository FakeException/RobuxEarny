/*
 * Created by FakeException on 2024/11/20 19:32
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/20 19:32
 */

package com.robuxearny.official.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Sector {

    @SerializedName("coins")
    private final Integer coins; // Use Integer to handle nulls

    @SerializedName("again")
    private final Boolean again;

    public Sector(Integer coins, Boolean again) {
        this.coins = coins;
        this.again = again;
    }

    public Integer getCoins() {
        return coins;
    }

    @NonNull
    @Override
    public String toString() {
        return "Sector{" +
                "coins=" + coins +
                ", try_again=" + again +
                '}';
    }

    public Boolean getTryAgain() {
        return again;
    }
}