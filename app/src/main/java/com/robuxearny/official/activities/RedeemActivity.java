package com.robuxearny.official.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robuxearny.official.R;
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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Object> packages = createMixedItemList();

        PackageAdapter adapter = new PackageAdapter(this, packages);
        recyclerView.setAdapter(adapter);
    }

    private List<Object> createMixedItemList() {
        List<Object> mixedItems = new ArrayList<>();
        mixedItems.add(new Package("Basic", 500, 10, R.drawable.robux));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Deluxe", 1000, 25, R.drawable.robux2));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Premium", 2000, 60, R.drawable.robux3));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Ultimate", 5000, 170, R.drawable.robux4));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Elite", 9000, 300, R.drawable.robux5));
        mixedItems.add(new AdBanner());
        mixedItems.add(new Package("Supreme", 12000, 440, R.drawable.robux6));
        mixedItems.add(new AdBanner());
        mixedItems.add(new AdBanner());
        return mixedItems;
    }

}
