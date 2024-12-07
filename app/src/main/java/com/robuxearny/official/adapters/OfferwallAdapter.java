/*
 * Created by FakeException on 2024/12/07 12:45
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/07 12:45
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsamurai.appsprize.AppsPrize;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.survey.CPXActivity;
import com.robuxearny.official.models.OfferWall;

import java.util.List;

public class OfferwallAdapter extends RecyclerView.Adapter<OfferwallAdapter.OfferwallViewHolder> {

    private final List<OfferWall> offerwalls;
    private final Activity context;

    public OfferwallAdapter(Activity context, List<OfferWall> offerwalls) {
        this.context = context;
        this.offerwalls = offerwalls;
    }

    @NonNull
    @Override
    public OfferwallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offerwall, parent, false);
        return new OfferwallViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferwallViewHolder holder, int position) {
        OfferWall offerwall = offerwalls.get(position);

        // Load offerwall logo using Glide
        Glide.with(holder.itemView.getContext())
                .load(offerwall.logoUrl)
                .into(holder.logoImageView);

        holder.nameTextView.setText(offerwall.name);
        holder.cardView.setOnClickListener(v -> {
            // Handle offerwall click
            switch (offerwall.name) {
                case "AppsPrize":
                    AppsPrize.launchActivity(context);
                    break;
                case "CPX Research":
                    context.startActivity(new Intent(context, CPXActivity.class));
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerwalls.size();
    }

    public static class OfferwallViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView cardView;
        public ImageView logoImageView;
        public TextView nameTextView;

        public OfferwallViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.offerwall_card_view);
            logoImageView = itemView.findViewById(R.id.offerwall_logo_image_view);
            nameTextView = itemView.findViewById(R.id.offerwall_name_text_view);
        }
    }
}