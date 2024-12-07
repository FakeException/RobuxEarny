/*
 * Created by FakeException on 2024/12/07 12:41
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/07 12:41
 */

package com.robuxearny.official.models;

import com.google.gson.annotations.SerializedName;

public class OfferWall {

    @SerializedName("name")
    public String name;
    @SerializedName("logoUrl")
    public String logoUrl;

    public OfferWall(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}