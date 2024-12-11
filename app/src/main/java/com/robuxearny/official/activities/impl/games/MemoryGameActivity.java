package com.robuxearny.official.activities.impl.games;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.utils.BoosterUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGameActivity extends GameActivity {

    private static final String TAG = "MemoryGameActivity";
    private GridLayout gridLayout;
    private TextView totalPointsTextView;
    private TextView attemptsTextView;
    private List<String> imageFiles;
    private List<Button> buttons;
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private int attempts = 0;
    private int matchedPairs = 0;
    private static final int MAX_ATTEMPTS = 6;
    private static final int TOTAL_PAIRS = 8; // 16 buttons / 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        totalPointsTextView = findViewById(R.id.totalPointsTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView); // Add this TextView to your layout
        gridLayout = findViewById(R.id.gridLayout);
        buttons = new ArrayList<>();

        int points = getPreferences().getInt("coins", 0);
        setTotalPoints(points);
        totalPointsTextView.setText(getString(R.string.total_points, points));

        // Initialize attempts TextView
        attemptsTextView.setText(getString(R.string.attempts_remaining, MAX_ATTEMPTS - attempts));

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        // Initialize the game
        initializeGame();
    }

    private void initializeGame() {
        imageFiles = new ArrayList<>();
        matchedPairs = 0;
        attempts = 0;

        // Load image files from assets
        loadImageFilesFromAssets();

        Log.d(TAG, "Image files loaded: " + imageFiles.size());

        Collections.shuffle(imageFiles);

        // Get screen dimensions
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Determine if the device is in landscape mode
        boolean isLandscape = screenWidth > screenHeight;

        // Calculate button size based on available width
        int numColumns = 4; // Number of columns for buttons
        int margin = 10; // Margin size (adjust as needed)

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        // Adjust button size based on orientation
        int buttonSize = (screenWidth - (margin * (numColumns + 1))) / numColumns; // Fit within width
        // Fit within width
        if (isLandscape) {
            if (tabletSize) {
                // Smaller button size for landscape mode
                buttonSize = (int) (buttonSize * 0.4); // Reduce to 70% of calculated size for tablet landscape
            } else {
                buttonSize = (int) (buttonSize * 0.2); // Reduce to 20% of calculated size for phone landscape
            }

        } else {
            // Larger button size for portrait mode
            buttonSize = (int) (buttonSize * 0.9); // Reduce to 90% of calculated size for portrait
        }

        for (int i = 0; i < 16; i++) {
            Button button = new Button(this);
            button.setBackgroundResource(R.drawable.robux2);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(margin, margin, margin, margin); // Set margins
            button.setLayoutParams(params);

            int finalI = i;
            button.setOnClickListener(view -> onCardClicked(button, finalI));
            buttons.add(button);
            gridLayout.addView(button);
        }
    }

    private void loadImageFilesFromAssets() {
        try {
            String[] files = getAssets().list("memory");
            if (files != null) {
                for (String file : files) {
                    if (file.endsWith(".webp")) {
                        imageFiles.add(file);
                        imageFiles.add(file);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading images: " + e.getMessage());
        }
    }


    private void onCardClicked(Button button, int index) {
        if (firstCardIndex == -1) {
            firstCardIndex = index;
            button.setBackground(loadDrawableFromAssets(imageFiles.get(index)));
            button.setEnabled(false);
        } else if (secondCardIndex == -1 && index != firstCardIndex) {
            secondCardIndex = index;
            button.setBackground(loadDrawableFromAssets(imageFiles.get(index)));
            checkForMatch();
        }
    }

    private int generateRandomPoints() {
        int basePoints = getRandom().nextInt(12) + 8;
        return BoosterUtils.getMoneyBooster(basePoints);
    }

    private void checkForMatch() {
        new Handler().postDelayed(() -> {
            if (imageFiles.get(firstCardIndex).equals(imageFiles.get(secondCardIndex))) {
                // Matched pair
                buttons.get(firstCardIndex).setVisibility(View.INVISIBLE);
                buttons.get(secondCardIndex).setVisibility(View.INVISIBLE);

                int points = generateRandomPoints();
                increasePoints(points);
                updateTotalPointsTextView(totalPointsTextView);

                getPrefsHelper().addMemoryEarnings(points);

                save();

                matchedPairs++;
                Toast.makeText(this, R.string.match_found, Toast.LENGTH_SHORT).show();

                // Check if all pairs are matched
                if (matchedPairs == TOTAL_PAIRS) {
                    finishGame(true);
                }
            } else {
                // No match
                attempts++;
                attemptsTextView.setText(getString(R.string.attempts_remaining, MAX_ATTEMPTS - attempts));

                resetCardBackgrounds();
                Toast.makeText(this, R.string.no_match_found, Toast.LENGTH_SHORT).show();

                // Check if max attempts reached
                if (attempts >= MAX_ATTEMPTS && matchedPairs < TOTAL_PAIRS) {
                    finishGame(false);
                }
            }

            firstCardIndex = -1;
            secondCardIndex = -1;
        }, 1000);
    }

    private Drawable loadDrawableFromAssets(String fileName) {
        try {
            InputStream is = getAssets().open("memory/" + fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            return null;
        }
    }

    private void resetCardBackgrounds() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackgroundResource(R.drawable.robux2);
            buttons.get(i).setEnabled(true);
        }
    }

    private void save() {
        getPrefsEditor().putInt("coins", getTotalPoints()).apply();
        playCollectSound();
    }

    private void finishGame(boolean won) {
        if (won) {
            Toast.makeText(this, R.string.you_win, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.game_over, Toast.LENGTH_SHORT).show();
        }

        if (won) {
            int bonusPoints = generateRandomPoints() * 2;
            increasePoints(bonusPoints);
            updateTotalPointsTextView(totalPointsTextView);
            startRandomGameActivity(false);

            save();
        } else {
            startRandomGameActivity(true);
        }
    }
}