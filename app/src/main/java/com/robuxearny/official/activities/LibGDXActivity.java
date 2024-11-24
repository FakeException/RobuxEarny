/*
 * Created by FakeException on 2024/11/23 11:30
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/23 11:30
 */

package com.robuxearny.official.activities;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.robuxearny.official.activities.impl.games.ColorBurstGame;

public class LibGDXActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new ColorBurstGame(this), config);
    }
}