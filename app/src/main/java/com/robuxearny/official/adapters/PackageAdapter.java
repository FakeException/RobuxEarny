/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.PurchaseActivity;
import com.robuxearny.official.models.Package;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PACKAGE = 1;

    private final Activity context;
    private final List<Object> items;
    private final int coins;

    private static final Map<String, Integer> drawableMap = new HashMap<>();

    static {
        drawableMap.put("robux", R.drawable.robux);
        drawableMap.put("robux2", R.drawable.robux2);
        drawableMap.put("robux3", R.drawable.robux3);
        drawableMap.put("robux4", R.drawable.robux4);
        drawableMap.put("robux5", R.drawable.robux5);
        drawableMap.put("robux6", R.drawable.robux6);
    }

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
        }

        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_PACKAGE) {
            PackageViewHolder packageViewHolder = (PackageViewHolder) holder;
            Package currentPackage = (Package) items.get(position);
            packageViewHolder.titleTextView.setText(currentPackage.getName());
            packageViewHolder.currentCoins.setText(context.getString(R.string.current_coins, coins));
            packageViewHolder.redeemPriceTextView.setText(context.getString(R.string.price, currentPackage.getPrice()));
            packageViewHolder.redeemRobuxTextView.setText(context.getString(R.string.redeem_amount, currentPackage.getQuantity()));

            String imageResourceString = currentPackage.getImageResource();
            Integer imageResourceId = drawableMap.get(imageResourceString);

            if (imageResourceId != null) {
                packageViewHolder.icon.setImageResource(imageResourceId);
            } else {
                Log.e("PackageAdapter", "Resource not found for image: " + imageResourceString);
            }

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
                                Long referralCount = document.getLong("referralCount");

                                Long surveys = 0L;
                                if (document.contains("surveys")) {
                                    surveys = document.getLong("surveys");
                                }

                                if (coinsLong != null) {
                                    long coins = coinsLong;
                                    if ((int) coins >= currentPackage.getPrice()) {

                                        Intent purchase = getIntent(currentPackage, (int) coins, adsLong, referralCount, surveys);
                                        context.startActivity(purchase);
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.not_enough), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                }

            });
        }
    }

    @NonNull
    private Intent getIntent(Package currentPackage, int coins, Long adsLong, Long referralCount, Long surveys) {
        Intent purchase = new Intent(context, PurchaseActivity.class);

        purchase.putExtra("price", currentPackage.getPrice());
        purchase.putExtra("robux", currentPackage.getQuantity());
        purchase.putExtra("coins", coins);
        purchase.putExtra("referralCount", referralCount);
        purchase.putExtra("surveys", surveys);

        if (adsLong != null) {
            long ads = adsLong;
            purchase.putExtra("ads", (int) ads);
        }
        return purchase;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Package) {
            return VIEW_TYPE_PACKAGE;
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
}
