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
import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.PackageAdapter;
import com.robuxearny.official.data.Package;
import com.robuxearny.official.decorators.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RedeemActivity extends BaseActivity {

    private static final String JSON_URL = "https://robuxrush.com/packages.json";
    private static final Map<String, Integer> drawableMap = new HashMap<>();

    static {
        drawableMap.put("robux", R.drawable.robux);
        drawableMap.put("robux2", R.drawable.robux2);
        drawableMap.put("robux3", R.drawable.robux3);
        drawableMap.put("robux4", R.drawable.robux4);
        drawableMap.put("robux5", R.drawable.robux5);
        drawableMap.put("robux6", R.drawable.robux6);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        List<Object> packagesList = new ArrayList<>();
        int coins = getPreferences().getInt("coins", 0);

        MaterialToolbar tbToolBar = findViewById(R.id.redeem_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(50));

        PackageAdapter adapter = new PackageAdapter(this, packagesList, coins);
        recyclerView.setAdapter(adapter);

        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
        recyclerView.setVisibility(View.GONE);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, JSON_URL, null, response -> {
            try {
                JSONArray packages = response.getJSONArray("packages");
                int startPosition = packagesList.size();

                for (int i = 0; i < packages.length(); i++) {
                    JSONObject packageObj = packages.getJSONObject(i);
                    String name = packageObj.getString("name");
                    int price = packageObj.getInt("price");
                    int quantity = packageObj.getInt("quantity");
                    String image = packageObj.getString("imageResource");

                    int resID = Objects.requireNonNull(drawableMap.getOrDefault(image, 0)); // Unbox safely

                    if (resID != 0) {
                        packagesList.add(new Package(name, price, quantity, resID));
                    } else {
                        Log.e("RedeemActivity", "Resource not found for image: " + image);
                    }
                }

                adapter.notifyItemRangeInserted(startPosition, packagesList.size() - startPosition);

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                loadingIndicator.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, error -> Log.e("Volley Error", error.toString()));

        requestQueue.add(jsonObjectRequest);
    }
}