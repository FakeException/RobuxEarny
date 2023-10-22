/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:27 AM
 */

package com.robuxearny.official.activities.impl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.PackageAdapter;
import com.robuxearny.official.data.AdBanner;
import com.robuxearny.official.data.Package;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RedeemActivity extends BaseActivity {

    private static final String JSON_URL = "https://robuxrush.com/packages.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        List<Object> packagesList = new ArrayList<>();
        int coins = getPreferences().getInt("coins", 0);

        MaterialToolbar tbToolBar = findViewById(R.id.redeem_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PackageAdapter adapter = new PackageAdapter(this, packagesList, coins);
        recyclerView.setAdapter(adapter);

        // Get references to loading indicator and RecyclerView
        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
        recyclerView.setVisibility(View.GONE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON request to fetch the packages
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, JSON_URL, null, response -> {
            try {
                JSONArray packages = response.getJSONArray("packages");
                int startPosition = packagesList.size(); // Get the current size

                for (int i = 0; i < packages.length(); i++) {
                    JSONObject packageObj = packages.getJSONObject(i);
                    String name = packageObj.getString("name");
                    int price = packageObj.getInt("price");
                    int quantity = packageObj.getInt("quantity");
                    String image = packageObj.getString("imageResource");

                    int resID = getResources().getIdentifier(image, "drawable", getPackageName());

                    packagesList.add(new AdBanner());
                    packagesList.add(new Package(name, price, quantity, resID));

                }

                adapter.notifyItemRangeInserted(startPosition, packagesList.size() - startPosition);

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // Hide the loading indicator and show the RecyclerView
                loadingIndicator.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, error -> Log.e("Volley Error", error.toString()));

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }


}
