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
