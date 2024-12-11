package com.robuxearny.official.activities.impl.games;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.robuxearny.official.activities.LibGDXBaseGame;
import com.robuxearny.official.utils.BoosterUtils;

public class ColorBurstGame extends LibGDXBaseGame {

    private final AndroidApplication application;

    public ColorBurstGame(AndroidApplication androidApplication) {
        this.application = androidApplication;
        this.preferences = androidApplication.getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);
    }

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture texture; // For target color rectangle


    private float screenWidth;
    private float screenHeight;
    private float circleRadius;
    private float targetX;
    private float targetY;
    private float targetWidth;
    private float targetHeight;

    private Color targetColor;
    private Color currentCircleColor = Color.WHITE;

    private int coinsEarned = 0;
    private int difficulty = 1;
    private long lastDifficultyIncreaseTime;
    private final GlyphLayout glyphLayout = new GlyphLayout(); // Create GlyphLayout instance

    // Cooldown variables
    private long lastTapTime = 0;
    private final long cooldownDuration = 500;

    // Circle movement variables
    private float circleX;
    private float circleY;
    private float circleSpeed = 400f;
    private float circleDirectionX = 1f;
    private float circleDirectionY = 1f;

    private float circleScale = 1f;
    private final float maxCircleScale = 1.5f;
    private final float scaleSpeed = 2f;

    private int scaleResetCounter = 0;
    private final int scaleResetDelay = 5;

    private float gameTime;
    private float elapsedTime = 0f;
    private boolean endToastShowed;
    private final SharedPreferences preferences;

    @Override
    public void create() {
        initialize(application);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        texture = new Texture("bobux.png");

        gameTime = MathUtils.random(20f, 35f);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        circleRadius = screenWidth * 0.1f;
        targetX = screenWidth * 0.1f;
        targetY = screenHeight * 0.8f;
        targetWidth = screenWidth * 0.2f;
        targetHeight = screenHeight * 0.1f;

        targetColor = getRandomColor();

        // Initialize circle position
        circleX = screenWidth / 2f;
        circleY = screenHeight / 2f;

        Gdx.input.setInputProcessor(new MyInputProcessor());

        Appodeal.cache(application, Appodeal.REWARDED_VIDEO);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // Calculate color change interval
        long colorChangeInterval = 1200 - (difficulty * 50L); // Start with a longer interval and decrease it slowly
        colorChangeInterval = Math.max(colorChangeInterval, 200); // Set a higher minimum

        // Change circle color periodically
        if (TimeUtils.millis() - lastDifficultyIncreaseTime > colorChangeInterval) {
            currentCircleColor = getRandomColor();

            lastDifficultyIncreaseTime = TimeUtils.millis();
        }

        // Update circle position
        float deltaTime = Gdx.graphics.getDeltaTime();
        circleX += circleSpeed * circleDirectionX * deltaTime;
        circleY += circleSpeed * circleDirectionY * deltaTime;

        elapsedTime += deltaTime; // Increment elapsed time

        // Check for game over
        if (elapsedTime >= gameTime) {

            if (!endToastShowed) {
                application.runOnUiThread(() -> {
                    String winMessage = getLocale().format("win", coinsEarned);
                    Toast.makeText(application.getContext(), winMessage, Toast.LENGTH_LONG).show();
                    endToastShowed = true;
                });

                Gdx.app.log("ColorBurstGame", "Game Over!");
                increaseCoins(coinsEarned);

                getPrefsHelper().addCBEarnings(coinsEarned);

                int totalCoins = preferences.getInt("coins", 0);
                preferences.edit().putInt("coins", totalCoins + coinsEarned).apply();

                startRandomGameActivity(application);
            }


        }

        // Check for collisions with screen boundaries
        if (circleX + circleRadius > screenWidth || circleX - circleRadius < 0) {
            circleDirectionX *= -1;
        }
        if (circleY + circleRadius > screenHeight || circleY - circleRadius < 0) {
            circleDirectionY *= -1;
        }

        // Update circle scale
        if (circleScale > 1f) {
            circleScale -= scaleSpeed * deltaTime;
            circleScale = Math.max(circleScale, 1f);
        }

        // Reset scale after delay
        if (scaleResetCounter > 0) {
            scaleResetCounter--;
            if (scaleResetCounter == 0) {
                circleScale = 1f;
            }
        }

        // Draw central circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(currentCircleColor);
        shapeRenderer.circle(circleX, circleY, circleRadius * circleScale);
        shapeRenderer.end();

        // Draw target color rectangle
        batch.begin();
        batch.setColor(targetColor);
        batch.draw(texture, targetX, targetY, targetWidth, targetHeight);

        // Display coins earned (centered)
        String coinsText = getLocale().format("coins", coinsEarned);
        glyphLayout.setText(getFont(), coinsText); // Set text for GlyphLayout
        float coinsTextWidth = glyphLayout.width; // Get width from GlyphLayout
        float coinsX = (screenWidth - coinsTextWidth) / 2f;
        getFont().draw(batch, coinsText, coinsX, screenHeight - (10 + 160));

        // Display difficulty (centered)
        String difficultyText = getLocale().format("difficulty", difficulty);
        glyphLayout.setText(getFont(), difficultyText); // Set text for GlyphLayout
        float difficultyX = 20;
        getFont().draw(batch, difficultyText, difficultyX, screenHeight - (10 + 160)); // Adjust y-coordinate as needed

        // Display timer
        String timerText = getLocale().format("time", Math.max(0, (int) (gameTime - elapsedTime)));
        glyphLayout.setText(getFont(), timerText);
        float timerTextWidth = glyphLayout.width;
        float timerX = (screenWidth - timerTextWidth) - 20;
        getFont().draw(batch, timerText, timerX, screenHeight - (10 + 160));

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        texture.dispose();
        getFont().dispose();
    }

    private Color getRandomColor() {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        return colors[MathUtils.random(colors.length - 1)];
    }

    private class MyInputProcessor implements InputProcessor {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // Check if cooldown period has passed
            if (TimeUtils.millis() - lastTapTime > cooldownDuration) {

                float distance = (float) Math.sqrt(Math.pow(screenX - circleX, 2) + Math.pow(screenY - (screenHeight - circleY), 2));

                // Check if touch is within circle bounds (with buffer)
                float touchThreshold = circleRadius * circleScale + 20f; // Add 20f buffer
                if (distance <= touchThreshold) {
                    if (currentCircleColor.equals(targetColor)) {
                        circleSpeed += 20;
                        difficulty += 1;
                        circleScale = maxCircleScale; // Increase circle size on tap
                        scaleResetCounter = scaleResetDelay; // Start the reset delay

                        coinsEarned += BoosterUtils.getMoneyBooster(difficulty); // Award coins based on difficulty

                        targetColor = getRandomColor();
                    } else {
                        Gdx.input.vibrate(300);
                        coinsEarned /= 2;
                    }
                    lastTapTime = TimeUtils.millis(); // Update last tap time
                }
            }
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    }
}