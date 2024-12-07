/*
 * Created by FakeException on 2024/12/07 12:48
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/07 12:48
 */

package com.robuxearny.official.activities.impl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.OfferwallAdapter;
import com.robuxearny.official.callbacks.OfferwallsCallback;
import com.robuxearny.official.models.OfferWall;
import com.robuxearny.official.utils.BackendUtils;

import java.util.List;

public class OfferwallsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerwalls);

        MaterialToolbar tbToolBar = findViewById(R.id.offerwalls_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.offerwalls_recycler_view);


        loadingIndicator = findViewById(R.id.loading_indicator);
        recyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);

        BackendUtils backendUtils = new BackendUtils(this);
        backendUtils.fetchOfferwalls(new OfferwallsCallback() {
            @Override
            public void onOfferwallsRetrieved(List<OfferWall> offerWalls) {
                recyclerView.setAdapter(new OfferwallAdapter(OfferwallsActivity.this, offerWalls));

                loadingIndicator.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Offerwalls", errorMessage);
            }
        });
    }
}
