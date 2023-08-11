/*
 * Created by FakeException on 8/8/23, 4:25 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/8/23, 4:25 PM
 */

package com.robuxearny.official.games;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.robuxearny.official.R;

import java.util.Objects;
import java.util.Random;

public class SlotMachine {
    private final int[] symbolIds = {
            R.drawable.diamond, R.drawable.diamond,
            R.drawable.cherry,
            R.drawable.lemon
    };

    private final int numReels = 3;
    private final Drawable[] currentReels = new Drawable[numReels];
    private final Random random = new Random();

    public SlotMachine(Context context) {
        for (int i = 0; i < numReels; i++) {
            currentReels[i] = getRandomSymbol(context);
        }
    }

    public void spin(Context context) {
        for (int i = 0; i < numReels; i++) {
            currentReels[i] = getRandomSymbol(context);
        }
    }

    private Drawable getRandomSymbol(Context context) {
        int randomIndex = random.nextInt(symbolIds.length);
        return ResourcesCompat.getDrawable(context.getResources(), symbolIds[randomIndex], null);
    }

    public Drawable[] getCurrentReels() {
        return currentReels;
    }

    public boolean checkWin(Context context) {
        for (int i = 1; i < numReels; i++) {
            if (!compareDrawables(context, currentReels[i], currentReels[0])) {
                return false; // Not a win, at least one reel is different
            }
        }
        return true; // All reels have the same symbol, it's a win
    }

    private boolean compareDrawables(Context context, Drawable drawable1, Drawable drawable2) {
        // Compare the resource IDs of the drawables
        if (drawable1 == null || drawable2 == null) {
            return false;
        }
        return ResourcesCompat.getDrawable(context.getResources(), getDrawableResource(context, drawable1), null)
                .getConstantState()
                .equals(ResourcesCompat.getDrawable(context.getResources(), getDrawableResource(context, drawable2), null)
                        .getConstantState());
    }

    private int getDrawableResource(Context context, Drawable drawable) {
        if (drawable == null) {
            return 0;
        }
        for (int symbolId : symbolIds) {
            if (Objects.equals(drawable.getConstantState(), Objects.requireNonNull(ResourcesCompat.getDrawable(context.getResources(), symbolId, null)).getConstantState())) {
                return symbolId;
            }
        }
        return 0;
    }
}
