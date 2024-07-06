/*
 * Created by FakeException on 6/28/24, 8:59 PM
 * Copyright (c) 2024. All rights reserved.
 * Last modified 6/28/24, 8:59 PM
 */

package com.robuxearny.official.activities.impl;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ProductDetails;
import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.robuxearny.official.R;
import com.robuxearny.official.Robux;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.BoosterAdapter;
import com.robuxearny.official.models.Booster;
import com.robuxearny.official.decorators.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class BoostersActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boosters);

        MaterialToolbar tbToolBar = findViewById(R.id.settings_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        List<Object> boostersList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(50));

        BoosterAdapter adapter = new BoosterAdapter(this, boostersList);
        recyclerView.setAdapter(adapter);

        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);

        List<ProductDetails> productDetailsList = Robux.getInstance().getProductDetailsList();

        if (productDetailsList == null || productDetailsList.isEmpty()) {
            finish();
            return;
            // nigger not having google play installed
        }

        int startPosition = productDetailsList.size();

        for (ProductDetails productDetails : productDetailsList) {
            if (productDetails.getName().contains("4")) {
                boostersList.add(new Booster(productDetails.getName(), productDetails.getDescription(), productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice(), R.drawable.boost3, (listener)
                        -> launchPurchaseFlow(productDetails)));
            } else {
                boostersList.add(new Booster(productDetails.getName(), productDetails.getDescription(), productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice(), R.drawable.boost2, (listener)
                        -> launchPurchaseFlow(productDetails)));
            }
        }

        adapter.notifyItemRangeInserted(startPosition, boostersList.size() - startPosition);

        loadingIndicator.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    void launchPurchaseFlow(ProductDetails productDetails) {
        ArrayList<BillingFlowParams.ProductDetailsParams> productList = new ArrayList<>();

        productList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productList)
                .build();

        Robux.getInstance().getBillingClient().launchBillingFlow(this, billingFlowParams);
    }
}
