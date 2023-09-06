/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:27 AM
 */

package com.robuxearny.official.activities.impl;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.PackageAdapter;
import com.robuxearny.official.data.AdBanner;
import com.robuxearny.official.data.Package;

import java.util.ArrayList;
import java.util.List;

public class RedeemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        MaterialToolbar tbToolBar = findViewById(R.id.redeem_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Object> packages = createMixedItemList();

        PackageAdapter adapter = new PackageAdapter(this, packages);
        recyclerView.setAdapter(adapter);
    }

    private List<Object> createMixedItemList() {
        List<Object> mixedItems = new ArrayList<>();
        mixedItems.add(new Package("Basic", 2700, 10, R.drawable.robux));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Deluxe", 3500, 25, R.drawable.robux2));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Premium", 4400, 60, R.drawable.robux3));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Ultimate", 6900, 170, R.drawable.robux4));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Elite", 10800, 300, R.drawable.robux5));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Supreme", 16200, 440, R.drawable.robux6));
        mixedItems.add(new AdBanner());
        mixedItems.add(new AdBanner());
        return mixedItems;
    }

}
