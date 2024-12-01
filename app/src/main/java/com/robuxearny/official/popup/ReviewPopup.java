/*
 * Created by Fake on 7/3/24, 10:01 AM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 7/3/24, 10:01 AM
 */
package com.robuxearny.official.popup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.robuxearny.official.R;

public class ReviewPopup {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public ReviewPopup(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("review", Context.MODE_PRIVATE);
    }

    public void showPopup() {
        if (sharedPreferences.getBoolean("showed", false)) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_review, null);

        Button valutaButton = view.findViewById(R.id.review_button);
        Button chiudiButton = view.findViewById(R.id.close_button);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setView(view);
        AlertDialog dialog = builder.show();

        valutaButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            context.startActivity(intent);

            sharedPreferences.edit().putBoolean("showed", true).apply();

            dialog.dismiss();
        });

        chiudiButton.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("showed", true).apply();

            dialog.dismiss();
        });
    }
}

