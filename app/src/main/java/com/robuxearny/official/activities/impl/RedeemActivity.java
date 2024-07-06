package com.robuxearny.official.activities.impl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.PackageAdapter;
import com.robuxearny.official.callbacks.PackageCallback;
import com.robuxearny.official.decorators.SpacesItemDecoration;
import com.robuxearny.official.models.Package;
import com.robuxearny.official.utils.BackendUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedeemActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private PackageAdapter adapter;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        final String JSON_URL = getString(R.string.api_url) + "packages.json";

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        List<Object> packagesList = new ArrayList<>();
        int coins = getPreferences().getInt("coins", 0);

        MaterialToolbar tbToolBar = findViewById(R.id.redeem_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(50));

        adapter = new PackageAdapter(this, packagesList, coins);
        recyclerView.setAdapter(adapter);

        loadingIndicator = findViewById(R.id.loading_indicator);
        recyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);

        BackendUtils.fetchPackages(this, new PackageCallback() {
            @Override
            public void onPackagesLoaded(List<Package> packages) {
                if (packages != null) {

                    int startPosition = packagesList.size();

                    for (Package packageObj : packages) {
                        packagesList.add(packageObj);
                    }

                    // Notify the adapter of the range of newly inserted items
                    adapter.notifyItemRangeInserted(startPosition, packagesList.size() - startPosition);

                    loadingIndicator.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RedeemActivity", "Could not load packages: " + errorMessage);
            }
        });
    }
}