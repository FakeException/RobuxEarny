package com.robuxearny.official.games.maze.actor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.robuxearny.official.games.maze.BaseActor;

public class Wall extends BaseActor {

    public Wall(float x, float y, float width, float height, Stage stage) {
        super(x, y, stage);
        loadTexture("square.jpg");
        setSize(width, height);
        setBoundaryRectangle();
    }
}
