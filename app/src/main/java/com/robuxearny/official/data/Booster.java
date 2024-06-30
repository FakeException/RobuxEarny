/*
 * Created by Fake on 6/28/24, 10:50 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 6/28/24, 10:50 PM
 */

package com.robuxearny.official.data;

import android.view.View;

public class Booster {
    private String title;
    private String description;
    private String cost;
    private int icon;
    private View.OnClickListener listener;

    public Booster(String title, String description, String cost, int icon, View.OnClickListener onClickListener) {
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.icon = icon;
        this.listener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
