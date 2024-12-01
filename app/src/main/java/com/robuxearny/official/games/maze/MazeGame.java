/*
 * Created by FakeException on 2024/12/01 12:21
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/01 12:21
 */

package com.robuxearny.official.games.maze;


public class MazeGame extends BaseMazeGame {

    public void create() {
        super.create();
        setActiveScreen(new LevelScreen());
    }
}
