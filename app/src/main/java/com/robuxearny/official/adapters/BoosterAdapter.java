/*
 * Created by Fake on 6/28/24, 10:09 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 6/28/24, 10:09 PM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robuxearny.official.R;
import com.robuxearny.official.models.Booster;
import com.robuxearny.official.utils.BoosterUtils;

import java.util.List;

public class BoosterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_BOOSTER = 1;

    private final Activity context;
    private final List<Object> items;
    private FirebaseUser user;

    public BoosterAdapter(Activity context, List<Object> items) {
        this.context = context;
        this.items = items;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BOOSTER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.booster_card, parent, false);
            return new BoosterViewHolder(itemView);
        }

        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_BOOSTER) {
            BoosterViewHolder boosterViewHolder = (BoosterViewHolder) holder;
            Booster booster = (Booster) items.get(position);
            boosterViewHolder.titleTextView.setText(booster.getTitle());
            boosterViewHolder.description.setText(booster.getDescription());
            boosterViewHolder.price.setText(context.getString(R.string.price2, booster.getCost()));
            boosterViewHolder.icon.setImageResource(booster.getIcon());
            boosterViewHolder.purchaseButton.setOnClickListener(booster.getListener());

            if (booster.getTitle().contains("4")) {
                BoosterUtils.checkHas4xBooster(user.getUid(), result -> {
                    if (result) {
                        boosterViewHolder.purchaseButton.setEnabled(false);
                    }

                });
            } else if (booster.getTitle().contains("10")) {
                BoosterUtils.checkHas10xBooster(user.getUid(), result -> {
                    if (result) {
                        boosterViewHolder.purchaseButton.setEnabled(false);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Booster) {
            return VIEW_TYPE_BOOSTER;
        }

        throw new IllegalArgumentException("Invalid item type");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BoosterViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView description;
        TextView price;
        Button purchaseButton;
        ImageView icon;

        BoosterViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.booster_name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            purchaseButton = itemView.findViewById(R.id.purchaseButton);
            icon = itemView.findViewById(R.id.robux_image);
        }
    }
}