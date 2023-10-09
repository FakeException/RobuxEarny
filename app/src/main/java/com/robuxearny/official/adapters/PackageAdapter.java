/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.PurchaseActivity;
import com.robuxearny.official.data.AdBanner;
import com.robuxearny.official.data.Package;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PACKAGE = 1;
    private static final int VIEW_TYPE_AD_BANNER = 2;

    private final Activity context;
    private final List<Object> items;
    private final int coins;

    public PackageAdapter(Activity context, List<Object> items, int coins) {
        this.context = context;
        this.items = items;
        this.coins = coins;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PACKAGE) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.package_card, parent, false);
            return new PackageViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_AD_BANNER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ad_view, parent, false);
            return new AdBannerViewHolder(itemView);
        }

        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_PACKAGE) {
            PackageViewHolder packageViewHolder = (PackageViewHolder) holder;
            Package currentPackage = (Package) items.get(position);
            packageViewHolder.titleTextView.setText(currentPackage.getTitle());
            packageViewHolder.currentCoins.setText(context.getString(R.string.current_coins, coins));
            packageViewHolder.redeemPriceTextView.setText(context.getString(R.string.price, currentPackage.getCost()));
            packageViewHolder.redeemRobuxTextView.setText(context.getString(R.string.redeem_amount, currentPackage.getRedeem()));
            packageViewHolder.icon.setImageResource(currentPackage.getIcon());

            packageViewHolder.redeemButton.setOnClickListener(view -> {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();

                    db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long coinsLong = document.getLong("coins");
                                Long adsLong = document.getLong("ads");
                                if (coinsLong != null) {
                                    long coins = coinsLong;
                                    if ((int) coins >= currentPackage.getCost()) {

                                        Intent purchase = new Intent(context, PurchaseActivity.class);

                                        purchase.putExtra("price", currentPackage.getCost());
                                        purchase.putExtra("robux", currentPackage.getRedeem());
                                        purchase.putExtra("coins", (int) coins);

                                        if (adsLong != null) {
                                            long ads = adsLong;
                                            purchase.putExtra("ads", (int) ads);
                                        }

                                        context.startActivity(purchase);
                                        context.finish();
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.not_enough), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }

            });
        } else if (viewType == VIEW_TYPE_AD_BANNER) {
            AdBannerViewHolder adBannerViewHolder = (AdBannerViewHolder) holder;

            AdView adView = adBannerViewHolder.adView;
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Package) {
            return VIEW_TYPE_PACKAGE;
        } else if (item instanceof AdBanner) {
            return VIEW_TYPE_AD_BANNER;
        }

        throw new IllegalArgumentException("Invalid item type");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView currentCoins;
        TextView redeemPriceTextView;
        TextView redeemRobuxTextView;
        Button redeemButton;
        ImageView icon;

        PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.package_name);
            currentCoins = itemView.findViewById(R.id.currentCoins);
            redeemPriceTextView = itemView.findViewById(R.id.price);
            redeemRobuxTextView = itemView.findViewById(R.id.redeem_amount);
            redeemButton = itemView.findViewById(R.id.redeemButton);
            icon = itemView.findViewById(R.id.robux_image);
        }
    }

    static class AdBannerViewHolder extends RecyclerView.ViewHolder {
        AdView adView;

        AdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            adView = itemView.findViewById(R.id.adView);
        }
    }
}
