/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.data;

public class PackageModel {
    private final int iconResId;
    private final String name;
    private final int price;

    public PackageModel(int iconResId, String name, int price) {
        this.iconResId = iconResId;
        this.name = name;
        this.price = price;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
