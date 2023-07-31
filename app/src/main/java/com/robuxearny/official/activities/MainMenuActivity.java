package com.robuxearny.official.activities;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.robuxearny.official.R;

public class MainMenuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MaterialButton buttonPlay = findViewById(R.id.buttonPlay);
        MaterialButton buttonRedeem = findViewById(R.id.buttonRedeem);

        buttonPlay.setOnClickListener(v -> {
            // Handle "Play" button click here
            // Start the appropriate activity for playing the game
        });

        buttonRedeem.setOnClickListener(v -> {
            // Handle "Redeem" button click here
            // Start the appropriate activity for redeeming rewards
        });
    }
}
