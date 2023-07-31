package com.robuxearny.official.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.robuxearny.official.R;
import com.robuxearny.official.adapters.IntroSliderAdapter;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.network.api.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity {

    private LinearLayout indicatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", 0);
        boolean isLogged = sharedPrefs.contains("token");
        if (isLogged) {
            String token = sharedPrefs.getString("token", "");
            try {

                if (Backend.tokenValid(token).equals("true")) {
                    Intent intent = new Intent(this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch (ExecutionException | InterruptedException e) {
                Toast error = Toast.makeText(this, "Please check your connection", Toast.LENGTH_LONG);
                error.show();
            }
        }


        ViewPager viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        List<IntroSlide> introSlides = new ArrayList<>();
        introSlides.add(new IntroSlide(getString(R.string.welcome), getString(R.string.welcome_desc)));
        introSlides.add(new IntroSlide(getString(R.string.coinsystem), getString(R.string.coinsystem_desc)));
        introSlides.add(new IntroSlide(getString(R.string.ready), getString(R.string.ready_desc)));

        IntroSliderAdapter introSliderAdapter = new IntroSliderAdapter(this, introSlides);
        viewPager.setAdapter(introSliderAdapter);

        setupIndicator(introSlides.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupIndicator(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }

        setIndicator(0);
    }

    private void setIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.indicator_active);
            } else {
                indicator.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }
}
